// vars/LambdaassumeAwsRole.groovy
def call(String environment) {
    def awsRoleMappings = [
        'monitoring': "arn:aws:iam::139543702781:role/euw1-monitoring-iac-jenkins-ecs-role",
        'sg-dev'     : 'arn:aws:iam::654654503007:role/euw1-sg-nonprod-Jenkins-ec2-role',
        'dev'     : 'arn:aws:iam::981134614696:role/ait-glue-np-EUW-jenkins-ec2-role',
        'qa': 'arn:aws:iam::981134614696:role/ait-glue-np-EUW-jenkins-ec2-role',
        'pre-prod': 'arn:aws:iam::280751646874:role/ait-glue-pp-EUW-jenkins-ec2-role',
        'main':    'arn:aws:iam::236237753407:role/ait-glue-p-EUW-jenkins-ec2-role'
    ]

    def awsRoleToAssume = awsRoleMappings[environment]
    if (awsRoleToAssume == null) {
        error("Unsupported environment: $environment")
    }

    // Echo the assumed role for transparency
    echo "Assuming AWS IAM Role: $awsRoleToAssume for environment: $environment"

    // Assume the AWS IAM role | 900s aka 15min is the shortest session time limit
    def creds = sh(script: """
        aws sts assume-role --role-arn "${awsRoleToAssume}" --role-session-name AssumedRoleSession --duration-seconds 900 | jq -r '.Credentials | .AccessKeyId, .SecretAccessKey, .SessionToken'
    """, returnStdout: true).trim().split()

    if (creds.size() == 3) {
        // Set environment variables for use in later stages
        env.AWS_ACCESS_KEY_ID = creds[0]
        //echo "AWS_ACCESS_KEY_ID = " + env.AWS_ACCESS_KEY_ID

        env.AWS_SECRET_ACCESS_KEY = creds[1]
        //echo "AWS_SECRET_ACCESS_KEY = " + env.AWS_SECRET_ACCESS_KEY

        env.AWS_SESSION_TOKEN = creds[2]
        //echo "AWS_SESSION_TOKEN = " + env.AWS_SESSION_TOKEN

        echo "Successfully assumed role and set AWS credentials in environment variables"
    } else {
        error("Failed to assume AWS IAM Role")
    }
}