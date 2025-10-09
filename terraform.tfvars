# Required Parameters
vpc_id = "vpc-0ca8568d095164407"

private_subnets = [
  "subnet-01c3d35a07ce39e34",
  "subnet-0902f9a6293151c8f"
]

public_subnets = [
  "subnet-01c3d35a07ce39e34",
  "subnet-0902f9a6293151c8f"
]

agent_docker_image = "public.ecr.aws/j4w9x2o6/ww/jenkins-alpine-agent-aws:latest"

glue_agent_docker_image = "136474465872.dkr.ecr.eu-west-1.amazonaws.com/ww/jenkins-alpine-agent-aws:latest"

lambda_agent_docker_image = "136474465872.dkr.ecr.eu-west-1.amazonaws.com/ww/jenkins-alpine-agent-aws:latest"

kaniko_agent_docker_image = "136474465872.dkr.ecr.eu-west-1.amazonaws.com/ww/jenkins-aws-fargate-kaniko:v2"
ace_agent_docker_image = "136474465872.dkr.ecr.eu-west-1.amazonaws.com/ww/jenkins-alpine-ace-agent-aws:latest"
terraform_agent_docker_image = "136474465872.dkr.ecr.eu-west-1.amazonaws.com/ww/jenkins-alpine-terraform-agent-aws:latest"
controller_docker_image = "136474465872.dkr.ecr.eu-west-1.amazonaws.com/ww/jenkins-aws-fargate:2.516.3"

