// vars/deleteMergedBranches.groovy

def call(String repoName, String credentialsId = 'bitbucket-repo-creation') {
    // Clone the repository
    sh "rm -rf ${repoName}"
    withCredentials([usernamePassword(credentialsId: credentialsId, passwordVariable: 'BITBUCKET_CREDS_PSW', usernameVariable: 'BITBUCKET_CREDS_USR')]) {
        sh "git clone https://${BITBUCKET_CREDS_USR}:${BITBUCKET_CREDS_PSW}@bitbucket.org/its-ait-confluent/${repoName}.git"
    }

    dir(repoName) {
        // Fetch all remote branches
        sh 'git fetch -p'

        // Debugging: List all branches
        sh 'git branch -r'

        // Debugging: Check branches merged into dev
        def mergedBranchesOutput
        try {
            mergedBranchesOutput = sh(
                script: 'git branch -r --merged origin/dev',
                returnStdout: true
            ).trim()
        } catch (Exception e) {
            echo "Error fetching merged branches: ${e.message}"
            mergedBranchesOutput = ""
        }

        echo "Debug: Merged branches raw output: ${mergedBranchesOutput}"

        def branches = []
        if (mergedBranchesOutput) {
            branches = mergedBranchesOutput.split("\n")
                .collect { it.replaceFirst(/origin\//, '').trim() }
                .findAll { it.startsWith('feature-') }
        }

        echo "Debug: Filtered branches: ${branches}"

        if (branches.size() == 0) {
            echo "No branches to process for deletion."
        } else {
            echo "Branches to be processed for deletion:"
            branches.each { branch ->
                echo "Processing branch: '${branch}'"
            }

            // Loop through each branch and delete it from the remote and local
            branches.each { branch ->
                try {
                    echo "Deleting remote branch: ${branch}"
                    sh "git push origin --delete ${branch}"
                    
                    // Check if the branch exists locally before trying to delete it
                    def localBranchExists = sh(
                        script: "git branch --list ${branch}",
                        returnStdout: true
                    ).trim()

                    if (localBranchExists) {
                        echo "Deleting local branch: ${branch}"
                        sh "git branch -d ${branch} || git branch -D ${branch}"
                    } else {
                        echo "Local branch ${branch} does not exist."
                    }
                } catch (Exception e) {
                    echo "Failed to delete branch ${branch}: ${e.message}"
                }
            }
        }
    }

    // Cleanup
    sh "rm -rf ${repoName}"
}
