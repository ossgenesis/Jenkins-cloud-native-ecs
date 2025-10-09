// vars/getBuParameter.groovy

def call(String kafkaServiceAccount, String kafkaClusterName, String env) {
    // Fetch the JSON object from SSM
    def jsonData = sh(script: "aws ssm get-parameter --name /confluent/${env}/${kafkaClusterName}/keys/${kafkaServiceAccount} --with-decryption --query Parameter.Value --output text", returnStdout: true).trim()
    
    // Parse the JSON object to extract values
    def parsedJson = readJSON text: jsonData
    def kafkaApiKeyId = parsedJson.kafka_api_key_id
    def kafkaApiKeySecret = parsedJson.kafka_api_key_secret
    def serviceAccount = parsedJson.service_account_id

    // Read the existing decrypt.tfvars file
    /*def decryptedFileContent = readFile('decrypt.tfvars').readLines()

    // Update the kafka_api_key and kafka_api_secret values, and add kafka_service_account_id
    def updatedContent = decryptedFileContent.collect { line ->
        if (line.contains("kafka_api_key")) {
            return "kafka_api_key = \"${kafkaApiKeyId}\""
        } else if (line.contains("kafka_api_secret")) {
            return "kafka_api_secret = \"${kafkaApiKeySecret}\""
        } else {
            return line
        }
    }

    // Add kafka_service_account_id to the content
    updatedContent << "kafka_service_account_id = \"${serviceAccount}\""

    // Write the updated content back to decrypt.tfvars
    writeFile(file: 'decrypt.tfvars', text: updatedContent.join("\n"))
    */
    // Return the required variables
    return [kafkaApiKeyId: kafkaApiKeyId, kafkaApiKeySecret: kafkaApiKeySecret]
}
