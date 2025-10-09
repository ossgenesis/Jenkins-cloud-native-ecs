def call(String branchName) {
    def result = []  

    switch (branchName) {
        case 'monitoring':
            result.add("ww-ait-euw1-monitoring-iac-tf.state-s3")  
            result.add("eu-west-1")  
            result.add(null)
            break
        case 'dev':
            result.add("ww-ait-euw1-devops-non-prod-dev-lambda-iac-tf.state-s3")
            result.add("eu-west-1")
            result.add("woolworths_ait_devops_nonprod_lambda_tf_state") 
            break
        case 'qa':
            result.add("ww-ait-euw1-devops-non-prod-dev-lambda-iac-tf.state-s3") 
            result.add("eu-west-1") 
            result.add("woolworths_ait_devops_nonprod_lambda_tf_state") 
            break
        case 'pre-prod':
            result.add("ww-ait-euw1-pre-prod-lambda-iac-tf.state-s3") 
            result.add("eu-west-1") 
            result.add("woolworths_ait_preprod_lambda_tf_state") 
            break
        case 'main':
            result.add("ww-ait-euw1-prod-lambda-iac-tf.state-s3") 
            result.add("eu-west-1") 
            result.add("woolworths_ait_prod_lambda_tf_state")
            break
        default:
            println "No specific configuration for branch: $branchName"
            result.add(null)  
            result.add(null)  
            result.add(null) 
    }

    return result
}
