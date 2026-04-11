# Security Policy

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| latest  | :white_check_mark: |

## Reporting a Vulnerability

We take security seriously. If you discover a security vulnerability in this project, please report it responsibly.

### How to Report

1. **Do NOT open a public GitHub issue** for security vulnerabilities.
2. Instead, please email us at **[INSERT CONTACT EMAIL]** with the following details:
   - Description of the vulnerability
   - Steps to reproduce the issue
   - Potential impact
   - Suggested fix (if any)

### What to Expect

- **Acknowledgement**: We will acknowledge receipt of your report within **48 hours**.
- **Assessment**: We will investigate and validate the vulnerability within **5 business days**.
- **Resolution**: We aim to release a fix within **30 days** of confirmation, depending on complexity.
- **Disclosure**: We will coordinate with you on public disclosure timing.

## Security Best Practices for Users

When deploying this Jenkins ECS Fargate stack, please follow these guidelines:

### AWS Credentials
- Never commit AWS credentials or secrets to the repository.
- Use IAM roles and policies with least-privilege access.
- Rotate credentials regularly.

### Terraform State
- Store Terraform state in an encrypted S3 bucket with versioning enabled.
- Enable state locking using DynamoDB.
- Restrict access to the state bucket using IAM policies.

### Jenkins Configuration
- Change the default admin password immediately after deployment.
- Enable HTTPS using a valid SSL/TLS certificate via ACM.
- Restrict access to Jenkins using the `allowed_ip_addresses` variable.
- Keep Jenkins and its plugins up to date.

### Network Security
- Deploy Jenkins in private subnets behind the ALB.
- Use security groups to restrict inbound/outbound traffic.
- Enable VPC Flow Logs for network monitoring.

### Container Security
- Regularly update the Jenkins controller and agent Docker images.
- Scan Docker images for vulnerabilities before deploying.
- Do not run containers as root in production (configure `controller_docker_user_uid_gid`).

## Dependencies

This project depends on the following key components:
- [Jenkins Official Docker Image](https://github.com/jenkinsci/docker)
- [Jenkins Inbound Agent Image](https://github.com/jenkinsci/docker-inbound-agent)
- AWS services (ECS, EFS, ALB, S3, IAM, Route53, ACM)
- Terraform AWS Provider

Please monitor security advisories for these dependencies.
