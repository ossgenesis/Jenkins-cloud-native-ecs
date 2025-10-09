# Jenkins Shared Library

## Purpose

1. `Reusable Code:` Allow the sharing and reuse of code across multiple Jenkins pipelines. This is useful for common operations or scripts that are used frequently in different projects. Instead of duplicating code in each pipeline, you can write the code once in a shared library and reference it in multiple pipelines.

2. `Maintainability:` By centralizing common code, a Shared Library makes it easier to maintain and update scripts. When a change is required, you only need to update the code in one place (the shared library), and all pipelines using this library will automatically use the updated code.

3. `Consistency:` Shared Libraries help in maintaining consistency across different Jenkins pipelines. Since the same code is used across various projects, it ensures that processes and standards are uniform, reducing the chance of errors that might occur due to inconsistencies.

4. `Simplifying Complex Pipelines:` Jenkins pipelines can become complex and hard to manage, especially in larger projects. Shared Libraries can encapsulate complex logic, making the pipelines themselves simpler, more readable, and easier to understand.

5. `Extending Pipeline Capabilities:` Shared Libraries allow you to extend the capabilities of Jenkins pipelines by enabling you to write custom steps and operations that can be easily incorporated into any pipeline.

6. `Version Control:` Shared Libraries are version-controlled in a source code repository, providing all the benefits of version control like change tracking, branching, and collaboration.

Jenkins Shared Libraries are a powerful feature for improving code reuse, maintainability, consistency, and pipeline management in Jenkins CI/CD processes.

## AWS Accounts
`np` = non-prod | ```981134614696```

`pp` = pre-prod | ```280751646874```

`p`  = prod     | ```23623775340```

## Neccessary reading for developing for the Cloud
- [12FactorApp](https://12factor.net/)

---

## Git config

- Configure your git config file first like this then only proceed to `clone`
```
[credential]
    username = <username>@bitbucket.org
[user]
    name = <first name> <surname>
    email = <user>@woolworths.co.za

[push]
	autoSetupRemote = true
```

## Process
- When creating `resources` or `vars` be mindful of naming
- Think about providing code snippets as generic as possible unless absolutely neccessary such as edge cases
- An example would be the `s3-policy`; this policy could be used for multiple AWS services, rather avoid creating extra S3 policies as this will become very cumbersome very quickly to manage and will also add to confusion
- Clone the repo to your local machine `git clone https://<username>@bitbucket.org/its-ait-confluent/ait-jenkins-shared-libraries.git `
- Create a branch with the `Jira ticket as your branch name` e.g. `APE-111`
- `resources` represent AWS Cloud resources such as `policies` or `roles` etc...
- `vars` represent Groovy code logic to be used by Jenkins
- Make changes
- `git add .` <--- a dot represents all changes
- `git commit -m "<JIRA TICKET> <reason for change>"`
- `git push`
- Raise PR
