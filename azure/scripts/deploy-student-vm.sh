#!/bin/bash
# ==============================================================================
# CineMesh - Microsoft Azure (Student Tier) Single VM Deployment Script
# ==============================================================================
# This script configures a fresh Azure Ubuntu Server VM (e.g., Standard_B1s or B2s)
# to run the entire CineMesh Distributed Ticketing Platform as a single process.
#
# Prerequisites:
# 1. Create an Azure VM (Ubuntu 22.04 LTS) in your Azure for Students subscription.
# 2. Allow inbound traffic on Port 80 (HTTP) and Port 22 (SSH) in the VM's NSG.
# 3. SSH into the VM and run this script.
#
# Usage:
#   chmod +x deploy-student-vm.sh
#   sudo ./deploy-student-vm.sh
# ==============================================================================

set -e

echo "🚀 Starting CineMesh Deployment for Azure Student VM..."

# 1. System Update
echo "📦 Updating package repositories..."
apt-get update && apt-get upgrade -y

# 2. Install Java 17, Maven, and MySQL Server
echo "☕ Installing OpenJDK 17, Maven, and MySQL..."
apt-get install -y openjdk-17-jdk maven git mysql-server

# 3. Configure MySQL Database
echo "🗄️ Setting up MySQL database 'cinemeshdb'..."
# Start mysql service if not started
systemctl start mysql
# Create database and user (assuming root with no password for script execution, standard on fresh ubuntu mysql)
mysql -e "CREATE DATABASE IF NOT EXISTS cinemeshdb;"
mysql -e "CREATE USER IF NOT EXISTS 'root'@'localhost' IDENTIFIED BY 'password';"
mysql -e "ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'password';"
mysql -e "GRANT ALL PRIVILEGES ON cinemeshdb.* TO 'root'@'localhost';"
mysql -e "FLUSH PRIVILEGES;"

# 3. Clone / Prepare the Application
APP_DIR="/opt/cinemesh"
if [ ! -d "$APP_DIR" ]; then
    echo "📂 Creating application directory at $APP_DIR..."
    mkdir -p $APP_DIR
    # Note: In a real environment, you would git clone here:
    # git clone https://github.com/your-repo/cinemesh.git $APP_DIR
    echo "⚠️ Please ensure the CineMesh source code is copied to $APP_DIR."
fi

# 4. Port Forwarding (80 -> 8080)
# Since Spring Boot runs on 8080 by default and shouldn't run as root,
# we map port 80 to 8080 using iptables.
echo "🔗 Setting up port forwarding (80 -> 8080)..."
iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 8080

# To make iptables persistent across reboots:
apt-get install -y iptables-persistent
netfilter-persistent save

# 5. Build the Project
echo "🔨 Building the application..."
cd $APP_DIR
# If code exists, build it:
if [ -f "pom.xml" ]; then
    mvn clean package -DskipTests
    echo "✅ Build complete."
else
    echo "⚠️ Source code not found in $APP_DIR. Skipping build."
fi

# 6. Create Systemd Service
echo "⚙️ Creating Systemd service for CineMesh..."
cat <<EOF > /etc/systemd/system/cinemesh.service
[Unit]
Description=CineMesh Distributed Application
After=network.target

[Service]
User=root
WorkingDirectory=$APP_DIR
ExecStart=/usr/bin/java -jar $APP_DIR/target/cinemesh-0.0.1-SNAPSHOT.jar
SuccessExitStatus=143
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

# 7. Start the Service
echo "▶️ Starting CineMesh service..."
systemctl daemon-reload
systemctl enable cinemesh
# systemctl start cinemesh  <-- Uncomment to start immediately if code is built

echo "======================================================================"
echo "🎉 Deployment Configuration Complete!"
echo "If the code is built and the service is started, CineMesh will be available at:"
echo "http://<YOUR_VM_PUBLIC_IP>/"
echo ""
echo "To check logs: sudo journalctl -u cinemesh -f"
echo "======================================================================"
