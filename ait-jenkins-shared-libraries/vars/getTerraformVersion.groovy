// This file defines a global variable named 'terraformVersion' in Jenkins Shared Library
def call() {
    // Executes the 'terraform version' command and returns its output
    def output = sh(script: 'terraform version', returnStdout: true).trim()
    return output
}
