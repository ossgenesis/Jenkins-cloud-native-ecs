provider "aws" {
  region = var.aws_region
}

# ECS-Jenkins Module
module "ecs_jenkins" {
  source = "./modules/ecs-jenkins"
  
  # Required Parameters
  private_subnets                      = var.private_subnets
  public_subnets                       = var.public_subnets
  vpc_id                               = var.vpc_id
  
  # General Variables
  aws_region                           = var.aws_region
  route53_zone_name                    = var.route53_zone_name
  route53_subdomain                    = var.route53_subdomain
  fargate_platform_version             = var.fargate_platform_version
  default_tags                         = var.default_tags
  
  # Jenkins Configuration
  controller_cpu_memory                = var.controller_cpu_memory
  agents_cpu_memory                    = var.agents_cpu_memory
  target_groups_deregistration_delay   = var.target_groups_deregistration_delay
  controller_deployment_percentages    = var.controller_deployment_percentages
  controller_log_retention_days        = var.controller_log_retention_days
  agents_log_retention_days            = var.agents_log_retention_days
  controller_docker_image              = var.controller_docker_image
  agent_docker_image                   = var.agent_docker_image
  controller_listening_port            = var.controller_listening_port
  controller_jnlp_port                 = var.controller_jnlp_port
  controller_java_opts                 = var.controller_java_opts
  controller_num_executors             = var.controller_num_executors
  controller_docker_user_uid_gid       = var.controller_docker_user_uid_gid
  efs_performance_mode                 = var.efs_performance_mode
  efs_throughput_mode                  = var.efs_throughput_mode
  efs_provisioned_throughput_in_mibps  = var.efs_provisioned_throughput_in_mibps
  efs_burst_credit_balance_threshold   = var.efs_burst_credit_balance_threshold
  allowed_ip_addresses                 = var.allowed_ip_addresses
  soci                                 = var.soci
  capture_ecs_events                   = var.capture_ecs_events
  lambda_agent_docker_image            = var.lambda_agent_docker_image
  glue_agent_docker_image              = var.glue_agent_docker_image
  kaniko_agent_docker_image            = var.kaniko_agent_docker_image
  ace_agent_docker_image               = var.ace_agent_docker_image
  terraform_agent_docker_image               = var.terraform_agent_docker_image

}
