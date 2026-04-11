# VPC Creation Documentation

## Overview

This document describes the AWS VPC infrastructure created for the Jenkins ECS deployment in the **ap-south-2 (Hyderabad)** region using AWS CLI

---

## Architecture Diagram

```
                          ┌──────────────────────────────────────────────────────────┐
                          │              VPC: jenkins-ecs-vpc                        │
                          │              CIDR: 10.0.0.0/16                           │
                          │              ID: vpc-XXXXXXXXXXXXXXXXX                   │
                          │                                                          │
                          │   ┌────────────────────┐  ┌────────────────────┐         │
                          │   │  Public Subnet 1   │  │  Public Subnet 2   │         │
                          │   │  10.0.1.0/24       │  │  10.0.2.0/24       │         │
                          │   │  ap-south-2a       │  │  ap-south-2b       │         │
                          │   │  subnet-XXXXX4d..  │  │  subnet-YYYY8..    │         │
                          │   │                    │  │                    │         │
        Internet          │   │  ┌──────────────┐  │  │                    │         │
           │              │   │  │ NAT Gateway  │  │  │                    │         │
           ▼              │   │  │ nat-03272..  │  │  │                    │         │
   ┌───────────────┐      │   │  └──────┬───────┘  │  │                    │         │
   │    Internet   │──────┤   │         │          │  │                    │         │
   │    Gateway    │      │   └─────────┼──────────┘  └────────────────────┘         │
   │ igw-XXXXX..   │      │             │                                            │
   └───────────────┘      │             │  Route: 0.0.0.0/0 → NAT GW               │
                          │             ▼                                            │
                          │   ┌────────────────────┐  ┌────────────────────┐         │
                          │   │  Private Subnet 1  │  │  Private Subnet 2  │         │
                          │   │  10.0.10.0/24      │  │  10.0.20.0/24      │         │
                          │   │  ap-south-2a       │  │  ap-south-2b       │         │
                          │   │  subnet-ZZZZ7..    │  │  subnet-WWWWf..    │         │
                          │   └────────────────────┘  └────────────────────┘         │
                          │                                                          │
                          └──────────────────────────────────────────────────────────┘

    Route Tables:
    ┌─────────────────────────────────────┐    ┌─────────────────────────────────────┐
    │  Public RT: rtb-xxxxxxxxxxxxxxxxx   │    │  Private RT: rtb-0yyyyyyyyyyyyyyyy  │
    │  0.0.0.0/0 → igw-xxxxxxxxxxxxxxxxxx │    │  0.0.0.0/0 → nat-0yyyyyyyyyyyyyyyyy │
    │  10.0.0.0/16 → local                │    │  10.0.0.0/16 → local                │
    └─────────────────────────────────────┘    └─────────────────────────────────────┘
```

---

## Resource Summary

| Resource | Name | ID | Details |
|----------|------|----|---------|
| VPC | jenkins-ecs-vpc | `vpc-xxxxxxxxxxxxxxxxxx` | CIDR: `10.0.0.0/16` |
| Public Subnet 1 | jenkins-public-subnet-1 | `subnet-xxxxxxxxxxxxxxxxx` | `10.0.1.0/24` / ap-south-2a |
| Public Subnet 2 | jenkins-public-subnet-2 | `subnet-yyyyyyyyyyyyyyyyy` | `10.0.2.0/24` / ap-south-2b |
| Private Subnet 1 | jenkins-private-subnet-1 | `subnet-zzzzzzzzzzzzzzzzz` | `10.0.10.0/24` / ap-south-2a |
| Private Subnet 2 | jenkins-private-subnet-2 | `subnet-wwwwwwwwwwwwwwwww` | `10.0.20.0/24` / ap-south-2b |
| Internet Gateway | jenkins-igw | `igw-xxxxxxxxxxxxxxxxx` | Attached to VPC |
| NAT Gateway | jenkins-nat-gw | `nat-xxxxxxxxxxxxxxxxx` | In public subnet 1 |
| Elastic IP | jenkins-nat-eip | `eipalloc-xxxxxxxxxxxxxxxxx` | `x.x.x.x` |
| Public Route Table | jenkins-public-rt | `rtb-xxxxxxxxxxxxxxxxx` | 0.0.0.0/0 → IGW |
| Private Route Table | jenkins-private-rt | `rtb-yyyyyyyyyyyyyyyyy` | 0.0.0.0/0 → NAT GW |

---

## Step-by-Step Creation Process

All commands use `--profile tcc` and target region `ap-south-2`.

### Step 1: Create the VPC

```bash
aws ec2 create-vpc \
  --cidr-block 10.0.0.0/16 \
  --tag-specifications 'ResourceType=vpc,Tags=[{Key=Name,Value=jenkins-ecs-vpc}]' \
  --profile tcc
```

Enable DNS support and hostnames:

```bash
aws ec2 modify-vpc-attribute \
  --vpc-id vpc-xxxxxxxxxxxxxxxxx \
  --enable-dns-hostnames '{"Value":true}' \
  --profile tcc

aws ec2 modify-vpc-attribute \
  --vpc-id vpc-xxxxxxxxxxxxxxxxx \
  --enable-dns-support '{"Value":true}' \
  --profile tcc
```

### Step 2: Create Subnets

**Public Subnet 1** (ap-south-2a):

```bash
aws ec2 create-subnet \
  --vpc-id vpc-xxxxxxxxxxxxxxxxx \
  --cidr-block 10.0.1.0/24 \
  --availability-zone ap-south-2a \
  --tag-specifications 'ResourceType=subnet,Tags=[{Key=Name,Value=jenkins-public-subnet-1}]' \
  --profile tcc
```

**Public Subnet 2** (ap-south-2b):

```bash
aws ec2 create-subnet \
  --vpc-id vpc-xxxxxxxxxxxxxxxxx \
  --cidr-block 10.0.2.0/24 \
  --availability-zone ap-south-2b \
  --tag-specifications 'ResourceType=subnet,Tags=[{Key=Name,Value=jenkins-public-subnet-2}]' \
  --profile tcc
```

**Private Subnet 1** (ap-south-2a):

```bash
aws ec2 create-subnet \
  --vpc-id vpc-xxxxxxxxxxxxxxxxx \
  --cidr-block 10.0.10.0/24 \
  --availability-zone ap-south-2a \
  --tag-specifications 'ResourceType=subnet,Tags=[{Key=Name,Value=jenkins-private-subnet-1}]' \
  --profile tcc
```

**Private Subnet 2** (ap-south-2b):

```bash
aws ec2 create-subnet \
  --vpc-id vpc-xxxxxxxxxxxxxxxxx \
  --cidr-block 10.0.20.0/24 \
  --availability-zone ap-south-2b \
  --tag-specifications 'ResourceType=subnet,Tags=[{Key=Name,Value=jenkins-private-subnet-2}]' \
  --profile tcc
```

### Step 3: Create and Attach Internet Gateway

```bash
aws ec2 create-internet-gateway \
  --tag-specifications 'ResourceType=internet-gateway,Tags=[{Key=Name,Value=jenkins-igw}]' \
  --profile tcc

aws ec2 attach-internet-gateway \
  --internet-gateway-id igw-xxxxxxxxxxxxxxxxx \
  --vpc-id vpc-xxxxxxxxxxxxxxxxx \
  --profile tcc
```

### Step 4: Create Public Route Table and Associate

```bash
aws ec2 create-route-table \
  --vpc-id vpc-xxxxxxxxxxxxxxxxx \
  --tag-specifications 'ResourceType=route-table,Tags=[{Key=Name,Value=jenkins-public-rt}]' \
  --profile tcc

aws ec2 create-route \
  --route-table-id rtb-xxxxxxxxxxxxxxxxx \
  --destination-cidr-block 0.0.0.0/0 \
  --gateway-id igw-xxxxxxxxxxxxxxxxx \
  --profile tcc

aws ec2 associate-route-table \
  --route-table-id rtb-xxxxxxxxxxxxxxxxx \
  --subnet-id subnet-xxxxxxxxxxxxxxxxx \
  --profile tcc

aws ec2 associate-route-table \
  --route-table-id rtb-xxxxxxxxxxxxxxxxx \
  --subnet-id subnet-yyyyyyyyyyyyyyyyy \
  --profile tcc
```

### Step 5: Enable Auto-Assign Public IPs on Public Subnets

```bash
aws ec2 modify-subnet-attribute \
  --subnet-id subnet-xxxxxxxxxxxxxxxxx \
  --map-public-ip-on-launch \
  --profile tcc

aws ec2 modify-subnet-attribute \
  --subnet-id subnet-yyyyyyyyyyyyyyyyy \
  --map-public-ip-on-launch \
  --profile tcc
```

### Step 6: Create NAT Gateway

Allocate an Elastic IP:

```bash
aws ec2 allocate-address \
  --domain vpc \
  --tag-specifications 'ResourceType=elastic-ip,Tags=[{Key=Name,Value=jenkins-nat-eip}]' \
  --profile tcc
```

Create NAT Gateway in public subnet 1:

```bash
aws ec2 create-nat-gateway \
  --subnet-id subnet-xxxxxxxxxxxxxxxxx \
  --allocation-id eipalloc-xxxxxxxxxxxxxxxxx \
  --tag-specifications 'ResourceType=natgateway,Tags=[{Key=Name,Value=jenkins-nat-gw}]' \
  --profile tcc
```

Wait for NAT Gateway to become available:

```bash
aws ec2 wait nat-gateway-available \
  --nat-gateway-ids nat-xxxxxxxxxxxxxxxxx \
  --profile tcc
```

### Step 7: Create Private Route Table and Associate

```bash
aws ec2 create-route-table \
  --vpc-id vpc-xxxxxxxxxxxxxxxxx \
  --tag-specifications 'ResourceType=route-table,Tags=[{Key=Name,Value=jenkins-private-rt}]' \
  --profile tcc

aws ec2 create-route \
  --route-table-id rtb-yyyyyyyyyyyyyyyyy \
  --destination-cidr-block 0.0.0.0/0 \
  --nat-gateway-id nat-xxxxxxxxxxxxxxxxx \
  --profile tcc

aws ec2 associate-route-table \
  --route-table-id rtb-yyyyyyyyyyyyyyyyy \
  --subnet-id subnet-zzzzzzzzzzzzzzzzz \
  --profile tcc

aws ec2 associate-route-table \
  --route-table-id rtb-yyyyyyyyyyyyyyyyy \
  --subnet-id subnet-wwwwwwwwwwwwwwwww \
  --profile tcc
```

---

## Terraform tfvars Configuration

After creation, the following values were populated in `terraform.tfvars`:

```hcl
vpc_id = "vpc-xxxxxxxxxxxxxxxxx"

private_subnets = [
  "subnet-zzzzzzzzzzzzzzzzz",
  "subnet-wwwwwwwwwwwwwwwww"
]

public_subnets = [
  "subnet-xxxxxxxxxxxxxxxxx",
  "subnet-yyyyyyyyyyyyyyyyy"
]
```

---

## Cleanup Commands

To tear down all resources (in reverse order):

```bash
# Delete NAT Gateway
aws ec2 delete-nat-gateway --nat-gateway-id nat-xxxxxxxxxxxxxxxxx --profile tcc

# Wait for NAT Gateway deletion
aws ec2 wait nat-gateway-deleted --nat-gateway-ids nat-xxxxxxxxxxxxxxxxx --profile tcc

# Release Elastic IP
aws ec2 release-address --allocation-id eipalloc-xxxxxxxxxxxxxxxxx --profile tcc

# Delete subnets
aws ec2 delete-subnet --subnet-id subnet-xxxxxxxxxxxxxxxxx --profile tcc
aws ec2 delete-subnet --subnet-id subnet-yyyyyyyyyyyyyyyyy --profile tcc
aws ec2 delete-subnet --subnet-id subnet-zzzzzzzzzzzzzzzzz --profile tcc
aws ec2 delete-subnet --subnet-id subnet-wwwwwwwwwwwwwwwww --profile tcc

# Delete route tables (disassociate first if needed)
aws ec2 delete-route-table --route-table-id rtb-xxxxxxxxxxxxxxxxx --profile tcc
aws ec2 delete-route-table --route-table-id rtb-yyyyyyyyyyyyyyyyy --profile tcc

# Detach and delete Internet Gateway
aws ec2 detach-internet-gateway --internet-gateway-id igw-xxxxxxxxxxxxxxxxx --vpc-id vpc-xxxxxxxxxxxxxxxxx --profile tcc
aws ec2 delete-internet-gateway --internet-gateway-id igw-xxxxxxxxxxxxxxxxx --profile tcc

# Delete VPC
aws ec2 delete-vpc --vpc-id vpc-xxxxxxxxxxxxxxxxx --profile tcc
```

---

*Created on: 2026-04-09 | Region: ap-south-2 | Profile: tcc*
