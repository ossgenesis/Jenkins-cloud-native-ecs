# Contributing to Jenkins Cloud Native ECS

Thank you for your interest in contributing! This guide will help you get started.

## Code of Conduct

Please read and follow our [Code of Conduct](CODE_OF_CONDUCT.md) before contributing.

## How to Contribute

### Reporting Bugs

1. Check [existing issues](../../issues) to avoid duplicates.
2. Open a new issue with:
   - A clear, descriptive title
   - Steps to reproduce the problem
   - Expected vs. actual behavior
   - Terraform version, AWS provider version, and relevant configuration

### Suggesting Features

1. Open an issue with the **feature request** label.
2. Describe the use case and why it would be valuable.
3. If possible, outline a proposed implementation approach.

### Submitting Changes

1. **Fork** the repository.
2. **Create a branch** from `dev`:
   ```bash
   git checkout dev
   git pull origin dev
   git checkout -b feature/your-feature-name
   ```
3. **Make your changes** following the guidelines below.
4. **Test your changes** (see Testing section).
5. **Commit** with a clear message:
   ```bash
   git commit -m "Add brief description of change"
   ```
6. **Push** to your fork:
   ```bash
   git push origin feature/your-feature-name
   ```
7. **Open a Pull Request** against the `dev` branch.

## Development Guidelines

### Terraform

- Use Terraform >= 1.0.
- Follow the [Terraform Style Guide](https://developer.hashicorp.com/terraform/language/style).
- Use meaningful resource names and descriptions.
- Add appropriate variable descriptions and validation rules.
- Use `terraform fmt` to format your code before committing.
- Run `terraform validate` to check for syntax errors.

### Docker

- Base images should use specific version tags, not `latest` (except for agents where noted).
- Keep images minimal — only install required packages.
- Run as non-root user where possible.
- Document any new packages added to Dockerfiles.

### Documentation

- Update the README if your change affects usage, inputs, or outputs.
- Add inline comments for complex Terraform logic.
- Update architecture diagrams if infrastructure changes are significant.

## Testing

Before submitting a PR, please verify:

1. **Format check**:
   ```bash
   terraform fmt -check -recursive
   ```
2. **Validation**:
   ```bash
   terraform validate
   ```
3. **Plan** (with your own `terraform.tfvars`):
   ```bash
   terraform plan -var-file=terraform.tfvars
   ```
4. **Docker builds** (if modifying Dockerfiles):
   ```bash
   cd docker
   docker build -f Dockerfile -t jenkins-controller-test .
   docker build -f Dockerfile.agent -t jenkins-agent-test .
   ```

## Pull Request Guidelines

- Target the `dev` branch (not `main`).
- Keep PRs focused — one feature or fix per PR.
- Provide a clear description of what the PR does and why.
- Reference any related issues (e.g., "Fixes #123").
- Ensure all checks pass before requesting review.

## License

By contributing, you agree that your contributions will be licensed under the [Apache License 2.0](LICENSE).
