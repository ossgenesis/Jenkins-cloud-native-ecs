module "ecs_events" {
  count       = var.capture_ecs_events ? 1 : 0
  source      = "../ecs-events-capture"
  cluster_arn = aws_ecs_cluster.cluster.arn
}
