def getRepositoryName(String searchTerm) {
    withCredentials([usernamePassword(credentialsId: 'jira-token', usernameVariable: 'JIRA_USERNAME', passwordVariable: 'JIRA_API_TOKEN')]) {
        // Load script content from Jenkins shared library
        def scriptContent = libraryResource('scripts/jira_helper.py')

        // Define where the script will be saved
        def scriptFile = "${env.WORKSPACE}/jira_helper.py"

        // Write the script to a file in the workspace
        writeFile(file: scriptFile, text: scriptContent)

        // Ensure the script is executable
        sh "chmod +x ${scriptFile}"

        // Debugging - Check file existence
        sh "ls -l ${scriptFile} && pwd"

        // Execute the Python script and capture output
        def rawOutput = sh(
            script: """
                export JIRA_USERNAME=\$JIRA_USERNAME
                export JIRA_API_TOKEN=\$JIRA_API_TOKEN
                python3 ${scriptFile} "${searchTerm}"
            """,
            returnStdout: true
        ).trim()

        // Print raw output for debugging
        echo "Raw Python Output: ${rawOutput}"

        // Ensure the output is valid JSON before parsing
        if (!rawOutput.startsWith("{") || !rawOutput.endsWith("}")) {
            error "Invalid JSON received from Python script: ${rawOutput}"
        }

        // Parse JSON output
        def descriptionMap = readJSON(text: rawOutput)

        // Check if the response contains an error
        if (descriptionMap.containsKey("error")) {
            error "Jira Script Failed: ${descriptionMap.error}"
        }

        return descriptionMap
    }
}
