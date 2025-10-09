def call(environment) {
    echo "Setting AWS account number for glue-job.json role ARN depending on the environment Git branch"

    writeFile file: "s3-policy.json", text: libraryResource("getGlueS3Policy.json")
    writeFile file: "glue-policy.json", text: libraryResource("getGluePolicy.json")
    writeFile file: "glue-role.json", text: libraryResource("getGlueRolePolicy.json")

    if (environment == "non-prod") {
        env.AWS_ACCOUNT = "981134614696"
        echo "AWS_ACCOUNT = " + env.AWS_ACCOUNT

    }
    if (environment == "pre-prod") {
        env.AWS_ACCOUNT = "280751646874"
        echo "AWS_ACCOUNT = " + env.AWS_ACCOUNT

    }
    if (environment == "main") {
        env.AWS_ACCOUNT = "236237753407"
        echo "AWS_ACCOUNT = " + env.AWS_ACCOUNT
    }
}