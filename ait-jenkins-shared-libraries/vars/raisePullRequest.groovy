// vars/raisePullRequest.groovy

def call(String username, String password, String sourceBranch, String targetBranch, String title, String url, String description) {
    try {
        def auth = sh(script: "echo -n ${username}:${password} | base64", returnStdout: true).trim()
        def payload = """
        {
          "title": "${title}",
          "source": {
            "branch": {
              "name": "${sourceBranch}"
            }
          },
          "destination": {
              "branch": {
                  "name": "${targetBranch}"
              }
          },
          "description": "${description}"
        }
        """
        
        def response = sh(script: """
        curl --request POST \
             --url ${url} \
             --header 'Content-Type: application/json' \
             --header 'Authorization: Basic ${auth}' \
             --data '${payload}' \
             --write-out 'HTTPSTATUS:%{http_code}' \
             --silent --output /dev/null
        """, returnStdout: true).trim()
        
        def httpStatus = response.substring(response.indexOf('HTTPSTATUS:') + 11)
        
        if (httpStatus != '201') {
            error "Failed to create pull request. HTTP Status: ${httpStatus}"
        } else {
            echo "Pull request created successfully. HTTP Status: ${httpStatus}"
        }
    } catch (Exception e) {
        error "Error creating pull request: ${e.message}"
    }
}
