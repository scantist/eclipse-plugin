#!/bin/bash
set -e

echo "🔧 Installing dependencies for Maven Tycho Eclipse Plugin..."

# Install Java first
echo "☕ Installing OpenJDK 11 (if not present)..."
if command -v apt-get &> /dev/null; then
    sudo apt-get update
    sudo apt-get install -y openjdk-11-jdk wget
elif command -v yum &> /dev/null; then
    sudo yum install -y java-11-openjdk-devel wget
else
    echo "❌ Unsupported package manager for Java installation."
    exit 1
fi

# Install latest Maven 3.9.x
echo "📦 Installing Maven 3.9.9 (latest)..."
MAVEN_VERSION="3.9.9"
MAVEN_URL="https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz"

# Download and install Maven
cd /tmp
wget -q ${MAVEN_URL}
tar -xzf apache-maven-${MAVEN_VERSION}-bin.tar.gz
sudo mv apache-maven-${MAVEN_VERSION} /opt/maven

# Create symlinks
sudo ln -sf /opt/maven/bin/mvn /usr/local/bin/mvn
sudo ln -sf /opt/maven/bin/mvnDebug /usr/local/bin/mvnDebug

# Set environment variables
echo 'export MAVEN_HOME=/opt/maven' | sudo tee /etc/environment
echo 'export PATH=${MAVEN_HOME}/bin:${PATH}' | sudo tee -a /etc/environment
export MAVEN_HOME=/opt/maven
export PATH=${MAVEN_HOME}/bin:${PATH}

# Clean up
rm -f apache-maven-${MAVEN_VERSION}-bin.tar.gz

echo "🔍 Verifying installations..."
mvn -version
java -version

echo "✅ Bootstrap completed successfully!"
echo "Maven ${MAVEN_VERSION} and Java have been installed."
echo "You can now run './local-deploy.sh' to build the plugin."
