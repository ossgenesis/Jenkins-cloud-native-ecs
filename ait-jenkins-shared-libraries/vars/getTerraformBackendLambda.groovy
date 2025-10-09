def call(String branchName) {
    switch (branchName) {
        case 'monitoring':
            // terraform state file
            env.S3_BUCKET_STATE = "ww-ait-euw1-monitoring-iac-tf.state-s3"
            // s3 bucket path to store the state
            // env.S3_BUCKET_KEY = "ait-lambda/nonprod/terraform.tfstate"
            env.AWS_REGION = "eu-west-1"
            // env.DYNAMODB_TABLE = "woolworths_ait_devops_nonprod_lambda_tf_state"
            break
        case 'sg-dev':
            // terraform state file
            env.S3_BUCKET_STATE = "ww-ait-euw1-sg-non-prod-dev-iac-tf.state-s3"
            // s3 bucket path to store the state
            // env.S3_BUCKET_KEY = "ait-lambda/nonprod/terraform.tfstate"
            env.AWS_REGION = "eu-west-1"
            env.DYNAMODB_TABLE = "woolworths_ait_sg_nonprod_dev_tf_state"
            break
        case 'dev':
            // terraform state file
            env.S3_BUCKET_STATE = "ww-ait-euw1-devops-non-prod-dev-lambda-iac-tf.state-s3"
            // s3 bucket path to store the state
            // env.S3_BUCKET_KEY = "ait-lambda/nonprod/terraform.tfstate"
            env.AWS_REGION = "eu-west-1"
            env.DYNAMODB_TABLE = "woolworths_ait_devops_nonprod_lambda_tf_state"
            break
        case 'qa':
            // terraform state file
            env.S3_BUCKET_STATE = "ww-ait-euw1-devops-non-prod-dev-lambda-iac-tf.state-s3"
            // s3 bucket path to store the state
            // env.S3_BUCKET_KEY = "ait-lambda/nonprod/terraform.tfstate"
            env.AWS_REGION = "eu-west-1"
            env.DYNAMODB_TABLE = "woolworths_ait_devops_nonprod_lambda_tf_state"
            break
        case 'pre-prod':
            // terraform state file
            env.S3_BUCKET_STATE = "ww-ait-euw1-pre-prod-lambda-iac-tf.state-s3"
            // s3 bucket path to store the state
            // env.S3_BUCKET_KEY = "ait-lambda/preprod/terraform.tfstate"
            env.AWS_REGION = "eu-west-1"
            env.DYNAMODB_TABLE = "woolworths_ait_preprod_lambda_tf_state"
            break
        case 'main':
            // terraform state file
            env.S3_BUCKET_STATE = "ww-ait-euw1-prod-lambda-iac-tf.state-s3"
            // s3 bucket path to store the state
            // env.S3_BUCKET_KEY = "ait-lambda/prod/terraform.tfstate"
            env.AWS_REGION = "eu-west-1"
            env.DYNAMODB_TABLE = "woolworths_ait_prod_lambda_tf_state"
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
