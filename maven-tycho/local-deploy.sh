#!/bin/bash
set -e

echo "🚀 Building Maven Tycho Eclipse Plugin..."

# Set Maven environment for latest version
export MAVEN_HOME=/opt/maven
export PATH=/opt/maven/bin:$PATH

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please run './bootstrap.sh' first."
    exit 1
fi

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please run './bootstrap.sh' first."
    exit 1
fi

# Print versions for debugging
echo "🔍 Build environment:"
mvn -version
echo ""

# Clean and build the project
echo "🧹 Cleaning previous build..."
mvn clean

echo "📦 Building Eclipse plugin with Tycho..."
mvn package

# Check if build was successful
if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Build completed successfully!"
    echo ""
    echo "📍 Update site location:"
    echo "   $(pwd)/org.example.sca.updatesite/target/repository/"
    echo ""
    echo "🚀 To install in Eclipse:"
    echo "   1. Open Eclipse IDE"
    echo "   2. Help → Install New Software..."
    echo "   3. Add → Local → Browse to the repository folder above"
    echo "   4. Select 'SCA Tools' and install"
    echo "   5. Restart Eclipse"
    echo ""
    echo "🎯 After installation, look for:"
    echo "   - 'SCA' menu in the menu bar"
    echo "   - 'SCA Scan' button in the toolbar"
    echo "   - Click either to see 'Hello World' dialog"
else
    echo "❌ Build failed. Check the error messages above."
    exit 1
fi
