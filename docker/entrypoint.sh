#!/bin/sh
echo "10.40.15.153 oraggpoc01.woolworths.co.za" | sudo tee -a /etc/hosts
exec /usr/local/bin/jenkins-agent "$@"
