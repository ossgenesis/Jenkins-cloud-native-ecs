// vars/promoteToNextEnvironment.groovy

def call(String repoName, String currentEnv, String nextEnv) {
    script {
        withCredentials([usernamePassword(credentialsId: 'app-user-bb-sa', usernameVariable: 'BITBUCKET_CREDS_USR', passwordVariable: 'BITBUCKET_CREDS_PSW')]) {
            try {
                // Prepare authentication credentials
                def authCred = "${BITBUCKET_CREDS_USR}:${BITBUCKET_CREDS_PSW}"

                // Construct the API URL dynamically using the repoName
                def apiUrl = "https://api.bitbucket.org/2.0/repositories/its-ait-confluent/${repoName}/pullrequests"

                // Create the JSON payload for the pull request
                def jsonPayload = [
                    title: "PR for promotion to ${nextEnv} from ${currentEnv}",
                    source: [branch: [name: "${currentEnv}"]],
                    destination: [branch: [name: "${nextEnv}"]],
                    description: "Pull Request for promoting from ${repoName} ${currentEnv} to ${nextEnv}."
                ]

                // Convert the JSON payload to a string
                def jsonString = writeJSON(returnText: true, json: jsonPayload)

                // Execute the API request to create a pull request
                def response = sh(
                    script: """
                        curl -s -o response.txt -w "%{http_code}" --user "$authCred" \
                        --request POST --data '${jsonString}' \
                        --header 'Content-Type: application/json' '${apiUrl}'
                    """,
                    returnStdout: true
                ).trim()

                echo "HTTP Response Code: ${response}"

                // Capture response body for debugging
                def responseBody = readFile('response.txt').trim()
                echo "Response Body: ${responseBody}"

                // Check if the PR was successfully created
                if (response != '201') {
                    error "Failed to raise pull request. Response: ${responseBody}"
                }

            } catch (Exception e) {
                echo "Exception caught: ${e.message}"
                currentBuild.result = 'FAILURE'
                throw e
            }
        }
    }
}
