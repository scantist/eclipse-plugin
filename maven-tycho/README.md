# Scantist SCA Eclipse Plugin - Maven Tycho Build

A complete Maven Tycho-based Eclipse plugin with SCA scanning functionality, real-time log display, and DevSecOps integration.

## Features
- **SCA Scanning**: Real Software Composition Analysis with live log display
- **DevSecOps Integration**: Configure tokens and URLs via Eclipse preferences
- **Professional UI**: Real-time scan logs in dedicated dialog
- **Eclipse Integration**: Seamless menu and toolbar integration

## Building the Plugin

This project should be built using Maven Tycho from CLI:

```bash
./local-deploy.sh
```

## Installation & Updates

### First-Time Installation
1. Build the plugin using `./local-deploy.sh`
2. Open Eclipse IDE
3. Go to **Help** → **Install New Software...**
4. Click **Add** → **Local**
5. Browse to: `org.example.sca.updatesite/target/repository/`
6. Select 'SCA Tools' and install
7. Restart Eclipse when prompted

### Updating Plugin (Without Uninstall)

**Option 1: Automatic Update (Recommended)**
1. Rebuild plugin: `./local-deploy.sh`
2. In Eclipse: **Help** → **Check for Updates**
3. Select SCA plugin updates and install
4. Restart Eclipse

**Option 2: Manual Update via Update Site**
1. Rebuild plugin: `./local-deploy.sh`
2. In Eclipse: **Help** → **Install New Software...**
3. Select the existing SCA update site from dropdown
4. Check **"Contact all update sites during install"**
5. Select newer version and install
6. Restart Eclipse

**Option 3: Force Update via Installed Software**
1. Rebuild plugin: `./local-deploy.sh`
2. In Eclipse: **Help** → **About Eclipse IDE** → **Installation Details**
3. Select SCA plugin → **Update...**
4. Follow wizard to update
5. Restart Eclipse

### Configuration
After installation, configure DevSecOps settings:
1. **Window** → **Preferences** → **SCA Settings**
2. Set **DevSecOps Token** (your authentication token)
3. Set **DevSecOps Import URL** (API endpoint)
4. Click **Apply and Close**

### Usage
1. Open any project in Eclipse
2. Click **SCA Scan** button in toolbar (or **SCA** menu)
3. View real-time scan logs in the dialog
4. Configure DevSecOps integration via preferences

