def call(String branchName) {
    switch (branchName) {
        case 'devops':
            // terraform state file
            env.S3_BUCKET_STATE = "ww-ait-euw1-access-iac-tf.state-s3"
            // s3 bucket path to store the state
            // env.S3_BUCKET_KEY = "ait-lambda/nonprod/terraform.tfstate"
            env.AWS_REGION = "eu-west-1"
            env.DYNAMODB_TABLE = "woolworths_ait_access_tf_state"
            break
        default:
            println "No specific configuration for branch: $branchName"
            // You may choose to handle unsupported branches differently
    }
}

    // Print environment variables
    echo "S3_BUCKET_STATE = ${env.S3_BUCKET_STATE}"
    // echo "S3_BUCKET_KEY = ${env.S3_BUCKET_KEY}"
    echo "AWS_REGION = ${env.AWS_REGION}"
    echo "DYNAMODB_TABLE = ${env.DYNAMODB_TABLE}"
