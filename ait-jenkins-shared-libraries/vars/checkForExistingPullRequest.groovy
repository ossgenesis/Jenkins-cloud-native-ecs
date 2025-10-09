// Function to check if a pull request already exists for a specific branch
def call(String username, String password, String folderName, String url) {
    try {
        def auth = sh(script: "echo -n ${username}:${password} | base64", returnStdout: true).trim()
        
        
        def response = sh(script: """
        curl --request GET \
             --url ${url} \
             --header 'Content-Type: application/json' \
             --header 'Authorization: Basic ${auth}' \
             --silent
        """, returnStdout: true).trim()
        
        def jsonResponse = new groovy.json.JsonSlurper().parseText(response)
        def existingPR = jsonResponse.values.find { it.source.branch.name == "feature-${folderName}" }
        return existingPR != null
    } catch (Exception e) {
        error "Error checking pull requests: ${e.message}"
    }
}