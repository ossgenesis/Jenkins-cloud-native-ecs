def call(String kafkaClusterId, List<String> kafkaTopics, String kafkaApiKey, String kafkaApiSecret, String kafkaApiKeyIdAdmin, String kafkaApiKeySecretAdmin, String adminDirectory, String httpEndpoint, String env) {

    sh "rm -rf ait-connector-confluent-admin*"
    sh "git clone -b dev https://${BITBUCKET_CREDS_USR}:${BITBUCKET_CREDS_PSW}@bitbucket.org/its-ait-confluent/ait-connector-confluent-admin.git"

    dir('ait-connector-confluent-admin') {
        def folderName = "update-topics-acl"
        sh """
            git checkout -b feature-${folderName}
            git push --set-upstream origin feature-${folderName}
        """
        
        def url = "https://api.bitbucket.org/2.0/repositories/its-ait-confluent/ait-connector-confluent-admin/pullrequests?state=OPEN"
        def folderPath = "${adminDirectory}"
        def devAdminTfvarsPath = "${folderPath}/${env}.tfvars".toLowerCase()
        def originalDevTfvarsContent = ""
        def nonExistingTopics = []
        def nonExistingTopicsACL = []

        if (fileExists(devAdminTfvarsPath)) {
            originalDevTfvarsContent = readFile(devAdminTfvarsPath)
        }
        for (def topicName in kafkaTopics) {
            boolean topicExists = false
            boolean aclExists = false
            def restApi = "${httpEndpoint}"
            def restUrl = restApi.replace("https://", "").replace(":443", "")
            try {
// checking if topic exist                
                def topicResponse = sh(script: """
                    curl -s -o /dev/null -w "%{http_code}" -u "${kafkaApiKeyIdAdmin}:${kafkaApiKeySecretAdmin}" "https://${restUrl}/kafka/v3/clusters/${kafkaClusterId}/topics/${topicName}"
                """, returnStdout: true).trim()

                if (topicResponse == "200") {
                    topicExists = true
                    echo "Topic '${topicName}' exists in cluster '${kafkaClusterId}'"
                } else if (topicResponse == "404") {
                    echo "Topic '${topicName}' does not exist in cluster '${kafkaClusterId}', updating dev.tfvars"
                    // Add the topic to the list of non-existing topics
                    nonExistingTopics.add(topicName)
                } else {
                    echo "Unexpected response: ${topicResponse} while checking topic '${topicName}' in cluster '${kafkaClusterId}'"
                    currentBuild.result = 'FAILURE'
                    error "Pipeline stopped auth issue."
                }
// checking if topic acl exist
                def aclResponse = sh(script: """
                    curl -s -u "${kafkaApiKey}:${kafkaApiSecret}" "https://${restUrl}/kafka/v3/clusters/${kafkaClusterId}/acls?resource_type=TOPIC&resource_name=${topicName}"
                """, returnStdout: true)

                if (aclResponse.contains('"error_code"')) {
                    error "Error fetching ACLs for topic '${topicName}'"
                    currentBuild.result = 'FAILURE'
                    error "Pipeline stopped auth issue."
                }

                aclExists = aclResponse.contains("\"resource_name\":\"${topicName}\"")
                
                if (!aclExists) {
                    echo "ACL for topic '${topicName}' does not exist, updating dev.tfvars"

                    def aclEntryExists = sh(script: "grep -q 'resource_name = \"${topicName}\"' ${devAdminTfvarsPath}", returnStatus: true) == 0

                    if (!aclEntryExists) {
                        // Add the topic to the list of non-existing topics
                        nonExistingTopicsACL.add(topicName)
                    } else {
                        echo "ACL for topic '${topicName}' already exists in the dev.tfvars file, skipping update."
                    }
                } else {
                    echo "ACL for topic '${topicName}' already exists in cluster '${kafkaClusterId}'"
                }

            } catch (Exception e) {
                echo "Error during topic and ACL checks: ${e.getMessage()}"
            }
        }    
        // Process the list of non-existing topics
        if (!nonExistingTopics.isEmpty()) {
            echo "Non-existing topics: ${nonExistingTopics}"
            for (def topic in nonExistingTopics) {
                    sh """
                        sed -i '/#please append any new Topic above this line ###########/i\\  {\\n    name        = \\"${topic}\\"\\n    partitions  = \\"1\\"\\n  },' ${devAdminTfvarsPath}

                    """
                    echo "Updated ${devAdminTfvarsPath} with new topic and cluster details."	
            }
        }
        // Process the list of non-existing topics and its ACLS
        if (!nonExistingTopicsACL.isEmpty()) {
            echo "Non-existing topics ACLs: ${nonExistingTopicsACL}"
            for (def topic in nonExistingTopicsACL) {
                    sh """
                        sed -i '/#please append any new ACL above this line ##############/i\\  {\\n      resource_type = \\"TOPIC\\"\\n      resource_name = \\"${topic}\\"\\n      pattern_type  = \\"LITERAL\\"\\n      kafka_operations = [\\"READ\\", \\"WRITE\\", \\"DESCRIBE_CONFIGS\\"]\\n  },' ${devAdminTfvarsPath}

                    """
                    echo "Updated ${devAdminTfvarsPath} with new ACL details."
            }
        }
        def updatedDevTfvarsContent = readFile(devAdminTfvarsPath)
        if (originalDevTfvarsContent != updatedDevTfvarsContent) {
            sh """
                git status
                git add .
                git commit -m 'Adding Topic and ACL to ${kafkaClusterId}'
                git push
            """

            def prUrl = raisePullRequest("${BITBUCKET_CREDS_USR}", "${BITBUCKET_CREDS_PSW}", "feature-${folderName}", "dev", "Adding Topic and ACL to ${kafkaClusterId}", "${url}", "Please review the changes.")
            sh "rm -rf ait-connector-confluent-admin*"
            
            currentBuild.result = 'UNSTABLE'
            error "Pipeline stopped after marking build as UNSTABLE due to PR creation."
            
        } else {
            echo "No changes made to ${devAdminTfvarsPath}, skipping commit and pull request."
        }
    }

    sh "rm -rf ait-connector-confluent-admin*"
    
}