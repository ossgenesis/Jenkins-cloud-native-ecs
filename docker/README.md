# Docker images

The folder contains the docker images used in Terraform: the Jenkins Controller and the agent. Both images are based on
the official images with some customization.

## Jenkins Controller: Dockerfile

In this image, we install some required plugins. See the [plugins](./plugins.txt) file.

[Link to the official image.](https://github.com/jenkinsci/docker/blob/master/README.md)

The entrypoint is also overridden to fetch the configuration from S3 if the variable `JENKINS_CONF_S3_URL` is defined.
This configuration will be read by the Jenkins configuration as code plugin.

To build the image:

```shell
docker build -f Dockerfile -t 136474465872.dkr.ecr.eu-west-1.amazonaws.com/ww/jenkins-aws-fargate:2.464.2 .
```

You can pull it from Docker Hub: `docker pull 136474465872.dkr.ecr.eu-west-1.amazonaws.com/ww/jenkins-aws-fargate:2.464.2`.

## Jenkins agents: Dockerfile.agent

Image for Jenkins agents. We install some packages as example. Note that all Jenkins agents images must derive
from `jenkins/inbound-agent`.

To build the image:

```shell
docker build  -f Dockerfile.agent -t public.ecr.aws/j4w9x2o6/ww/jenkins-alpine-agent-aws:latest .
```

You can pull it from Docker Hub: `docker pull public.ecr.aws/j4w9x2o6/ww/jenkins-alpine-agent-aws:latest`.

## Containerized Index Builder

To build a SOCI index inside a container, you can use the [containerized-index-builder](./containerized-index-builder/)
tool.

The image is available on Docker Hub as `public.ecr.aws/j4w9x2o6/ww/soci-index-builder:latest`.
