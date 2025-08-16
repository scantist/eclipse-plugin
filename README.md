# SCA Eclipse Plugin v0.0.9

A Maven Tycho-based Eclipse plugin for Software Composition Analysis (SCA) with real-time scanning, live log display, and DevSecOps platform integration.

## ✨ Features

- 🔍 **Real-time SCA Scanning**: Live vulnerability detection with instant feedback
- 📊 **Interactive Log Display**: Professional dialog with real-time scan progress
- 🔒 **DevSecOps Integration**: Seamless token-based authentication and result upload
- 🎯 **Eclipse Native**: Full IDE integration with menu, toolbar, and preferences
- ⚡ **Background Processing**: Non-blocking scans that don't interrupt your workflow

## 🚀 Quick Start

### Build & Install
```bash
./local-deploy.sh
```

Then in Eclipse:
1. **Help** → **Install New Software...** → **Add** → **Local**
2. Browse to: `org.example.sca.updatesite/target/repository/`
3. Install **SCA Tools** and restart Eclipse

### Configure DevSecOps
1. **Window** → **Preferences** → **SCA Settings**
2. Set your **DevSecOps Token** and **Import URL**
3. **Apply and Close**

### Run SCA Scan
1. Open any project in Eclipse
2. Click **SCA Scan** (toolbar) or **SCA** menu
3. Monitor real-time scan progress in the dialog

## 🔄 Updates

To update the plugin to a newer version:

**Automatic Update (Recommended)**
- **Help** → **Check for Updates** → Install SCA updates → Restart

**Manual Update**
- Rebuild: `./local-deploy.sh`
- **Help** → **Install New Software...** → Select existing SCA site → Install newer version

## 🛠️ Technical Details

- **Version**: 0.0.9
- **Build System**: Maven Tycho 4.0.8
- **Java Version**: 11+
- **Eclipse Version**: 2023-12+
- **SCA Engine**: Automated detector with JSON reporting

## 📋 Architecture

```
├── org.example.sca.plugin/          # Main plugin with SCA logic
├── org.example.sca.feature/         # Feature packaging
└── org.example.sca.updatesite/      # P2 update site
```

## 🔧 Development

**Requirements**: Maven 3.9.9+, Java 11+

**Build**: `./local-deploy.sh` creates update site at:
`org.example.sca.updatesite/target/repository/`

**Components**:
- **SCAHelper**: Scan orchestration and DevSecOps integration
- **SCALogDialog**: Real-time UI with scan controls
- **SCAPreferences**: Eclipse preferences for configuration

