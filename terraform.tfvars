# Required Parameters
aws_region = "ap-south-2"
vpc_id = "vpc-XXXXXXXXXXXXX"

private_subnets = [
  "subnet-XXXXXXXXXXXXXXXX",
  "subnet-XXXXXXXXXXXXXXXX"
]

public_subnets = [
  "subnet-XXXXXXXXXXXXXX",
  "subnet-XXXXXXXXXXXXXX"
]

agent_docker_image = "xxxxxxxxx.dkr.ecr.ap-south-2.amazonaws.com/jenkins-agent:latest"

glue_agent_docker_image = "xxxxxxxxx.dkr.ecr.ap-south-2.amazonaws.com/jenkins-agent:latest"

lambda_agent_docker_image = "xxxxxxxxx.dkr.ecr.ap-south-2.amazonaws.com/jenkins-agent:latest"

kaniko_agent_docker_image = "xxxxxxxxx.dkr.ecr.ap-south-2.amazonaws.com/jenkins-agent:latest"
ace_agent_docker_image = "xxxxxxxxx.dkr.ecr.ap-south-2.amazonaws.com/jenkins-agent:latest"
terraform_agent_docker_image = "xxxxxxxxx.dkr.ecr.ap-south-2.amazonaws.com/jenkins-agent:latest"
controller_docker_image = "xxxxxxxxx.dkr.ecr.ap-south-2.amazonaws.com/jenkins-controller:2.555.2"

