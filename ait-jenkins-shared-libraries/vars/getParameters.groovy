// vars/getParameters.groovy
def call(String env, String kafkaClusterName) {
    // Fetch the JSON object from AWS SSM
    //def jsonData = sh(script: "aws ssm get-parameter --name /confluent/master/dev/keys --with-decryption --query Parameter.Value --output text", returnStdout: true).trim()
    //def jsonDataAws = sh(script: "aws ssm get-parameter --name /confluent/master/${env}/aws/keys --with-decryption --query Parameter.Value --output text", returnStdout: true).trim()
    //def jsonDataConfluentCloud = sh(script: "aws ssm get-parameter --name /confluent/master/${env}/cloud_api/keys --with-decryption --query Parameter.Value --output text", returnStdout: true).trim()
    def jsonDataKafka = sh(script: "aws ssm get-parameter --name /confluent/master/${env}/kafka_api/${kafkaClusterName}/keys --with-decryption --query Parameter.Value --output text", returnStdout: true).trim()
    //def jsonDataElastic = sh(script: "aws ssm get-parameter --name /confluent/master/${env}/elastic/keys --with-decryption --query Parameter.Value --output text", returnStdout: true).trim()
    
    // Parse the JSON object to extract values
    //def parsedJson = readJSON text: jsonData
    //def parsedJsonAws = readJSON text: jsonDataAws
    //def parsedJsonConfluentCloud = readJSON text: jsonDataConfluentCloud
    //def parsedJsonElastic = readJSON text: jsonDataElastic
    def parsedJsonKafka = readJSON text: jsonDataKafka

    def kafkaApiKeyIdAdmin = parsedJsonKafka.kafka_api_key_id
    def kafkaApiKeySecretAdmin = parsedJsonKafka.kafka_api_key_secret
    //def confluentCloudApiKey = parsedJsonConfluentCloud.confluent_cloud_api_key
    //def confluentCloudApiSecret = parsedJsonConfluentCloud.confluent_cloud_api_secret
    //def connectionUsername = parsedJsonElastic.connection_username
    //def connectionPassword = parsedJsonElastic.connection_password
    //def awsAccessKeyId = parsedJsonAws.aws_access_key_id
    //def awsSecretAccessKey = parsedJsonAws.aws_secret_access_key
    
    // Create a temporary file to store the decrypted values
    /*def decryptedFileContent = """
    kafka_api_key = "${kafkaApiKeyIdAdmin}"
    kafka_api_secret = "${kafkaApiKeySecretAdmin}"
    connection_username = "${connectionUsername}"
    connection_password = "${connectionPassword}"
    confluent_cloud_api_key = "${confluentCloudApiKey}"
    confluent_cloud_api_secret = "${confluentCloudApiSecret}"
    aws_access_key_id  = "${awsAccessKeyId}"
    aws_secret_access_key = "${awsSecretAccessKey}"
    """
    
    // Write the content to 'decrypt.tfvars'
    writeFile(file: 'decrypt.tfvars', text: decryptedFileContent)
    
    // Output the content of the file to the console
    sh "cat decrypt.tfvars"
    */
    // Return the required variables
    return [kafkaApiKeyIdAdmin: kafkaApiKeyIdAdmin, kafkaApiKeySecretAdmin: kafkaApiKeySecretAdmin]
}