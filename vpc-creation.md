# VPC Creation Documentation

## Overview

This document describes the AWS VPC infrastructure created for the Jenkins ECS deployment in the **ap-south-2 (Hyderabad)** region using AWS CLI with the `tcc` profile.

---

## Architecture Diagram

```
                          ┌──────────────────────────────────────────────────────────┐
                          │              VPC: jenkins-ecs-vpc                        │
                          │              CIDR: 10.0.0.0/16                           │
                          │              ID: vpc-0004f7060daf74278                   │
                          │                                                          │
                          │   ┌────────────────────┐  ┌────────────────────┐         │
                          │   │  Public Subnet 1   │  │  Public Subnet 2   │         │
                          │   │  10.0.1.0/24       │  │  10.0.2.0/24       │         │
                          │   │  ap-south-2a       │  │  ap-south-2b       │         │
                          │   │  subnet-00bfa4d..  │  │  subnet-041d8..    │         │
                          │   │                    │  │                    │         │
        Internet          │   │  ┌──────────────┐  │  │                    │         │
           │              │   │  │ NAT Gateway  │  │  │                    │         │
           ▼              │   │  │ nat-03272..  │  │  │                    │         │
   ┌───────────────┐      │   │  └──────┬───────┘  │  │                    │         │
   │    Internet   │──────┤   │         │          │  │                    │         │
   │    Gateway    │      │   └─────────┼──────────┘  └────────────────────┘         │
   │ igw-03dce..   │      │             │                                            │
   └───────────────┘      │             │  Route: 0.0.0.0/0 → NAT GW               │
                          │             ▼                                            │
                          │   ┌────────────────────┐  ┌────────────────────┐         │
                          │   │  Private Subnet 1  │  │  Private Subnet 2  │         │
                          │   │  10.0.10.0/24      │  │  10.0.20.0/24      │         │
                          │   │  ap-south-2a       │  │  ap-south-2b       │         │
                          │   │  subnet-0be87..    │  │  subnet-045af..    │         │
                          │   └────────────────────┘  └────────────────────┘         │
                          │                                                          │
                          └──────────────────────────────────────────────────────────┘

    Route Tables:
    ┌─────────────────────────────────────┐    ┌─────────────────────────────────────┐
    │  Public RT: rtb-077ba96714d5ea29f   │    │  Private RT: rtb-08841fecdd73e22e4  │
    │  0.0.0.0/0 → igw-03dce62f87aa59109 │    │  0.0.0.0/0 → nat-0327243d2ad009186 │
    │  10.0.0.0/16 → local               │    │  10.0.0.0/16 → local               │
    └─────────────────────────────────────┘    └─────────────────────────────────────┘
```

---

## Resource Summary

| Resource | Name | ID | Details |
|----------|------|----|---------|
| VPC | jenkins-ecs-vpc | `vpc-0004f7060daf74278` | CIDR: `10.0.0.0/16` |
| Public Subnet 1 | jenkins-public-subnet-1 | `subnet-00bfa4d7558e0eb95` | `10.0.1.0/24` / ap-south-2a |
| Public Subnet 2 | jenkins-public-subnet-2 | `subnet-041d88811d89ba2be` | `10.0.2.0/24` / ap-south-2b |
| Private Subnet 1 | jenkins-private-subnet-1 | `subnet-0be8725878dcd88d9` | `10.0.10.0/24` / ap-south-2a |
| Private Subnet 2 | jenkins-private-subnet-2 | `subnet-045af808a3bd2a1bd` | `10.0.20.0/24` / ap-south-2b |
| Internet Gateway | jenkins-igw | `igw-03dce62f87aa59109` | Attached to VPC |
| NAT Gateway | jenkins-nat-gw | `nat-0327243d2ad009186` | In public subnet 1 |
| Elastic IP | jenkins-nat-eip | `eipalloc-09f6575d0275ec0e2` | `16.112.195.6` |
| Public Route Table | jenkins-public-rt | `rtb-077ba96714d5ea29f` | 0.0.0.0/0 → IGW |
| Private Route Table | jenkins-private-rt | `rtb-08841fecdd73e22e4` | 0.0.0.0/0 → NAT GW |

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
  --vpc-id vpc-0004f7060daf74278 \
  --enable-dns-hostnames '{"Value":true}' \
  --profile tcc

aws ec2 modify-vpc-attribute \
  --vpc-id vpc-0004f7060daf74278 \
  --enable-dns-support '{"Value":true}' \
  --profile tcc
```

### Step 2: Create Subnets

**Public Subnet 1** (ap-south-2a):

```bash
aws ec2 create-subnet \
  --vpc-id vpc-0004f7060daf74278 \
  --cidr-block 10.0.1.0/24 \
  --availability-zone ap-south-2a \
  --tag-specifications 'ResourceType=subnet,Tags=[{Key=Name,Value=jenkins-public-subnet-1}]' \
  --profile tcc
```

**Public Subnet 2** (ap-south-2b):

```bash
aws ec2 create-subnet \
  --vpc-id vpc-0004f7060daf74278 \
  --cidr-block 10.0.2.0/24 \
  --availability-zone ap-south-2b \
  --tag-specifications 'ResourceType=subnet,Tags=[{Key=Name,Value=jenkins-public-subnet-2}]' \
  --profile tcc
```

**Private Subnet 1** (ap-south-2a):

```bash
aws ec2 create-subnet \
  --vpc-id vpc-0004f7060daf74278 \
  --cidr-block 10.0.10.0/24 \
  --availability-zone ap-south-2a \
  --tag-specifications 'ResourceType=subnet,Tags=[{Key=Name,Value=jenkins-private-subnet-1}]' \
  --profile tcc
```

**Private Subnet 2** (ap-south-2b):

```bash
aws ec2 create-subnet \
  --vpc-id vpc-0004f7060daf74278 \
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
  --internet-gateway-id igw-03dce62f87aa59109 \
  --vpc-id vpc-0004f7060daf74278 \
  --profile tcc
```

### Step 4: Create Public Route Table and Associate

```bash
aws ec2 create-route-table \
  --vpc-id vpc-0004f7060daf74278 \
  --tag-specifications 'ResourceType=route-table,Tags=[{Key=Name,Value=jenkins-public-rt}]' \
  --profile tcc

aws ec2 create-route \
  --route-table-id rtb-077ba96714d5ea29f \
  --destination-cidr-block 0.0.0.0/0 \
  --gateway-id igw-03dce62f87aa59109 \
  --profile tcc

aws ec2 associate-route-table \
  --route-table-id rtb-077ba96714d5ea29f \
  --subnet-id subnet-00bfa4d7558e0eb95 \
  --profile tcc

aws ec2 associate-route-table \
  --route-table-id rtb-077ba96714d5ea29f \
  --subnet-id subnet-041d88811d89ba2be \
  --profile tcc
```

### Step 5: Enable Auto-Assign Public IPs on Public Subnets

```bash
aws ec2 modify-subnet-attribute \
  --subnet-id subnet-00bfa4d7558e0eb95 \
  --map-public-ip-on-launch \
  --profile tcc

aws ec2 modify-subnet-attribute \
  --subnet-id subnet-041d88811d89ba2be \
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
  --subnet-id subnet-00bfa4d7558e0eb95 \
  --allocation-id eipalloc-09f6575d0275ec0e2 \
  --tag-specifications 'ResourceType=natgateway,Tags=[{Key=Name,Value=jenkins-nat-gw}]' \
  --profile tcc
```

Wait for NAT Gateway to become available:

```bash
aws ec2 wait nat-gateway-available \
  --nat-gateway-ids nat-0327243d2ad009186 \
  --profile tcc
```

### Step 7: Create Private Route Table and Associate

```bash
aws ec2 create-route-table \
  --vpc-id vpc-0004f7060daf74278 \
  --tag-specifications 'ResourceType=route-table,Tags=[{Key=Name,Value=jenkins-private-rt}]' \
  --profile tcc

aws ec2 create-route \
  --route-table-id rtb-08841fecdd73e22e4 \
  --destination-cidr-block 0.0.0.0/0 \
  --nat-gateway-id nat-0327243d2ad009186 \
  --profile tcc

aws ec2 associate-route-table \
  --route-table-id rtb-08841fecdd73e22e4 \
  --subnet-id subnet-0be8725878dcd88d9 \
  --profile tcc

aws ec2 associate-route-table \
  --route-table-id rtb-08841fecdd73e22e4 \
  --subnet-id subnet-045af808a3bd2a1bd \
  --profile tcc
```

---

## Terraform tfvars Configuration

After creation, the following values were populated in `terraform.tfvars`:

```hcl
vpc_id = "vpc-0004f7060daf74278"

private_subnets = [
  "subnet-0be8725878dcd88d9",
  "subnet-045af808a3bd2a1bd"
]

public_subnets = [
  "subnet-00bfa4d7558e0eb95",
  "subnet-041d88811d89ba2be"
]
```

---

## Cleanup Commands

To tear down all resources (in reverse order):

```bash
# Delete NAT Gateway
aws ec2 delete-nat-gateway --nat-gateway-id nat-0327243d2ad009186 --profile tcc

# Wait for NAT Gateway deletion
aws ec2 wait nat-gateway-deleted --nat-gateway-ids nat-0327243d2ad009186 --profile tcc

# Release Elastic IP
aws ec2 release-address --allocation-id eipalloc-09f6575d0275ec0e2 --profile tcc

# Delete subnets
aws ec2 delete-subnet --subnet-id subnet-00bfa4d7558e0eb95 --profile tcc
aws ec2 delete-subnet --subnet-id subnet-041d88811d89ba2be --profile tcc
aws ec2 delete-subnet --subnet-id subnet-0be8725878dcd88d9 --profile tcc
aws ec2 delete-subnet --subnet-id subnet-045af808a3bd2a1bd --profile tcc

# Delete route tables (disassociate first if needed)
aws ec2 delete-route-table --route-table-id rtb-077ba96714d5ea29f --profile tcc
aws ec2 delete-route-table --route-table-id rtb-08841fecdd73e22e4 --profile tcc

# Detach and delete Internet Gateway
aws ec2 detach-internet-gateway --internet-gateway-id igw-03dce62f87aa59109 --vpc-id vpc-0004f7060daf74278 --profile tcc
aws ec2 delete-internet-gateway --internet-gateway-id igw-03dce62f87aa59109 --profile tcc

# Delete VPC
aws ec2 delete-vpc --vpc-id vpc-0004f7060daf74278 --profile tcc
```

---

*Created on: 2026-04-09 | Region: ap-south-2 | Profile: tcc*
