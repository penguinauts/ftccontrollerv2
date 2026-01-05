@echo off
REM ============================================
REM FTC Robot Wireless Deployment Script
REM ============================================
REM This script connects to the robot wirelessly
REM and optionally deploys the latest build.
REM
REM Usage:
REM   push-to-robot.bat          - Connect only
REM   push-to-robot.bat deploy   - Connect and install APK
REM ============================================

SETLOCAL EnableDelayedExpansion

REM Robot IP address (change if needed)
SET ROBOT_IP=192.168.43.1:5555

echo ========================================
echo FTC Robot Wireless Connection
echo ========================================
echo.

REM Check if ADB is installed
adb version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ========================================
    echo [ERROR] ADB not found
    echo ========================================
    echo.
    echo ADB ^(Android Debug Bridge^) is not installed or not in PATH.
    echo.
    echo Please install Android Studio and add ADB to your PATH:
    echo   Example: C:\Users\YourName\AppData\Local\Android\Sdk\platform-tools
    echo.
    pause
    exit /b 1
)

REM Step 1: Kill existing ADB server
echo [1/4] Stopping ADB server...
adb kill-server 2>nul
timeout /t 1 /nobreak >nul

REM Step 2: Start fresh ADB server
echo [2/4] Starting ADB server...
adb start-server 2>nul
timeout /t 2 /nobreak >nul

REM Step 3: Connect to robot
echo [3/4] Connecting to robot at %ROBOT_IP%...

REM Run adb connect with timeout using start command
start /B cmd /c "adb connect %ROBOT_IP% > connect_output.tmp 2>&1"

REM Wait max 10 seconds for connection (check every second)
set TIMEOUT_COUNTER=0
:WAIT_CONNECT
timeout /t 1 /nobreak >nul
set /a TIMEOUT_COUNTER+=1
if %TIMEOUT_COUNTER% GEQ 10 goto CONNECT_TIMEOUT

REM Check if connect_output.tmp file exists and has content
if not exist connect_output.tmp goto WAIT_CONNECT
findstr /R "." connect_output.tmp >nul 2>&1
if %ERRORLEVEL% NEQ 0 goto WAIT_CONNECT

goto CONNECT_DONE

:CONNECT_TIMEOUT
echo [WARNING] Connection attempt timed out after 10 seconds
REM Kill any hanging adb processes
taskkill /F /IM adb.exe >nul 2>&1

:CONNECT_DONE
REM Display the connection output if file exists
if exist connect_output.tmp (
    type connect_output.tmp
) else (
    echo Failed to connect - no response from robot
)

REM Give it a moment to settle
timeout /t 1 /nobreak >nul

REM Step 4: Verify connection
echo [4/4] Verifying connection...
adb devices > devices_output.tmp
type devices_output.tmp
echo.

REM Check if connection failed
findstr /C:"cannot connect" /C:"failed to connect" /C:"Connection refused" connect_output.tmp >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo ========================================
    echo [FAILED] CONNECTION FAILED
    echo ========================================
    echo.
    echo Possible reasons:
    echo   1. NOT CONNECTED TO ROBOT WIFI
    echo      ^> Connect to the Robot Control Hub WiFi network
    echo      ^> WiFi name usually starts with: FTC-XXXX or similar
    echo.
    echo   2. WRONG IP ADDRESS
    echo      ^> Check robot IP in Robot Controller app
    echo      ^> Current IP in script: %ROBOT_IP%
    echo.
    echo   3. ROBOT IS NOT POWERED ON
    echo      ^> Make sure Robot Control Hub is on
    echo.
    echo   4. WIRELESS ADB NOT ENABLED
    echo      ^> Connect via USB first, then enable wireless debugging
    echo.
    del connect_output.tmp devices_output.tmp 2>nul
    pause
    exit /b 1
)

REM Check if device is actually connected
findstr /C:"192.168.43.1:5555" devices_output.tmp | findstr /C:"device" >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ========================================
    echo [WARNING] Connection may be unstable
    echo ========================================
    echo.
    echo The robot appears in the list but may not be fully connected.
    echo Try running the script again, or connect via USB if issues persist.
    echo.
)

REM Clean up temporary files
del connect_output.tmp devices_output.tmp 2>nul

REM Check if we should deploy
if /I "%~1"=="deploy" goto :deploy
if /I "%~1"=="install" goto :deploy
goto :connect_only

:deploy
echo ========================================
echo Building and Installing APK
echo ========================================
echo.

REM Build the project
echo Building APK...
call gradlew.bat assembleDebug

if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Build successful!
    echo.
    
    REM Install the APK
    echo Installing APK to robot...
    call gradlew.bat installDebug
    
    if !ERRORLEVEL! EQU 0 (
        echo.
        echo ========================================
        echo [SUCCESS] APK installed on robot successfully!
        echo ========================================
    ) else (
        echo.
        echo ========================================
        echo [FAILED] Installation failed!
        echo ========================================
        exit /b 1
    )
) else (
    echo.
    echo ========================================
    echo [FAILED] Build failed!
    echo ========================================
    exit /b 1
)
goto :end

:connect_only
echo ========================================
echo Connection Complete!
echo ========================================
echo.
echo To also install APK, run:
echo push-to-robot.bat deploy
echo.

:end
pause

