# 🤖 Robot Wireless Deployment Guide

Quick guide for connecting to and deploying code to the FTC robot wirelessly.

## Prerequisites

- **ADB installed** (comes with Android Studio)
- Robot and laptop on the same WiFi network (Robot Control Hub WiFi)
- Robot IP: `192.168.43.1:5555`

## 🚀 Quick Start

### For Mac/Linux Users

1. **First time only**: Make the script executable
   ```bash
   chmod +x push-to-robot.sh
   ```

2. **Connect to robot**:
   ```bash
   ./push-to-robot.sh
   ```

3. **Connect AND deploy code**:
   ```bash
   ./push-to-robot.sh deploy
   ```

### For Windows Users

1. **Connect to robot**:
   ```cmd
   push-to-robot.bat
   ```

2. **Connect AND deploy code**:
   ```cmd
   push-to-robot.bat deploy
   ```

## 📋 What Each Command Does

### Connection Only
```bash
./push-to-robot.sh          # Mac/Linux
push-to-robot.bat           # Windows
```
- Restarts ADB server
- Connects to robot at `192.168.43.1:5555`
- Verifies connection

### Connection + Deployment
```bash
./push-to-robot.sh deploy   # Mac/Linux
push-to-robot.bat deploy    # Windows
```
- Does everything above, PLUS:
- Builds the latest code (`./gradlew assembleDebug`)
- Installs APK on robot (`./gradlew installDebug`)

## 🔧 Troubleshooting

### "adb: command not found"

**Solution**: Add ADB to your PATH

**Mac/Linux**:
```bash
# Add to ~/.zshrc or ~/.bashrc
export PATH="$PATH:$HOME/Library/Android/sdk/platform-tools"
```

**Windows**:
1. Find ADB location: `C:\Users\YourName\AppData\Local\Android\Sdk\platform-tools`
2. Add to System Environment Variables PATH

### Connection Fails

1. **Check WiFi**: Make sure you're connected to the Robot Control Hub WiFi
2. **Check IP**: Verify robot IP is `192.168.43.1` (check in Robot Controller app)
3. **Restart Robot**: Power cycle the Control Hub
4. **Manual test**: Run commands manually:
   ```bash
   adb kill-server
   adb start-server
   adb connect 192.168.43.1:5555
   adb devices
   ```

### Different Robot IP

If your robot uses a different IP, edit the script:

**In `push-to-robot.sh`** (line 12):
```bash
ROBOT_IP="192.168.43.1:5555"  # Change this
```

**In `push-to-robot.bat`** (line 14):
```batch
SET ROBOT_IP=192.168.43.1:5555  REM Change this
```

## 📝 Manual Commands Reference

If you prefer to run commands manually:

```bash
# 1. Restart ADB
adb kill-server
adb start-server

# 2. Connect to robot
adb connect 192.168.43.1:5555

# 3. Verify connection
adb devices

# 4. Build APK (optional)
./gradlew assembleDebug       # Mac/Linux
gradlew.bat assembleDebug     # Windows

# 5. Install APK (optional)
./gradlew installDebug        # Mac/Linux
gradlew.bat installDebug      # Windows
```

## 🎯 Best Practices

1. **Before Competition**: Test the deployment script at home
2. **At Competition**: Keep robot and laptop close for stable WiFi connection
3. **Quick Deploy**: Use `deploy` option to push code changes fast
4. **Verify**: Always check the OpMode appears in Driver Station after deployment

## 💡 Tips

- **First connection of the day**: Takes a few seconds longer
- **Multiple laptops**: Each laptop needs to connect independently
- **USB vs Wireless**: USB is more reliable; wireless is more convenient
- **Battery life**: Wireless ADB uses minimal extra battery

## ⚡ Quick Reference

| What You Want | Mac/Linux | Windows |
|--------------|-----------|---------|
| Just connect | `./push-to-robot.sh` | `push-to-robot.bat` |
| Connect + Deploy | `./push-to-robot.sh deploy` | `push-to-robot.bat deploy` |
| Make executable (first time) | `chmod +x push-to-robot.sh` | _(not needed)_ |

---

**Need help?** Check if ADB is working: `adb --version`

