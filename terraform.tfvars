# Required Parameters
aws_region = "ap-south-2"
vpc_id = "vpc-0004f7060daf74278"

private_subnets = [
  "subnet-0be8725878dcd88d9",
  "subnet-045af808a3bd2a1bd"
]

public_subnets = [
  "subnet-00bfa4d7558e0eb95",
  "subnet-041d88811d89ba2be"
]

agent_docker_image = "564186749702.dkr.ecr.ap-south-2.amazonaws.com/jenkins-agent:latest"

glue_agent_docker_image = "564186749702.dkr.ecr.ap-south-2.amazonaws.com/jenkins-agent:latest"

lambda_agent_docker_image = "564186749702.dkr.ecr.ap-south-2.amazonaws.com/jenkins-agent:latest"

kaniko_agent_docker_image = "564186749702.dkr.ecr.ap-south-2.amazonaws.com/jenkins-agent:latest"
ace_agent_docker_image = "564186749702.dkr.ecr.ap-south-2.amazonaws.com/jenkins-agent:latest"
terraform_agent_docker_image = "564186749702.dkr.ecr.ap-south-2.amazonaws.com/jenkins-agent:latest"
controller_docker_image = "564186749702.dkr.ecr.ap-south-2.amazonaws.com/jenkins-controller:2.541.3"

