output "jenkins_controller_url" {
  value = module.ecs_jenkins.controller_url
}

output "jenkins_credentials" {
  value = module.ecs_jenkins.jenkins_credentials
  sensitive   = true
}