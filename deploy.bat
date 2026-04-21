@echo off
title SafeInbox JDK17 Deployer
color 0b

:: --- 1. FIXED PATHS FOR YOUR SYSTEM ---
set "JAVA_HOME=C:\Program Files\Java\jdk-17"
set "TOMCAT_DIR=C:\Program Files\Apache Software Foundation\Tomcat 10.1"
set "PROJ_DIR=C:\SpamDetectionSystem"

:: --- 2. UPDATE SYSTEM PATH FOR THIS SESSION ---
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo ====================================================
echo  STEP 1: BUILDING WITH MAVEN (JDK 17)
echo ====================================================
cd /d "%PROJ_DIR%"

:: Verify Java exists before building
if not exist "%JAVA_HOME%\bin\java.exe" (
    echo [ERROR] Java not found at %JAVA_HOME%
    pause
    exit /b
)

call mvn clean package
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Maven Build Failed.
    pause
    exit /b
)

echo.
echo ====================================================
echo  STEP 2: DEPLOYING WAR TO TOMCAT
echo ====================================================
copy /y "%PROJ_DIR%\target\SpamEmailDetector.war" "%TOMCAT_DIR%\webapps\"

echo.
echo ====================================================
echo  STEP 3: STARTING TOMCAT SERVER
echo ====================================================
cd /d "%TOMCAT_DIR%\bin"

:: Clear JRE_HOME and force JAVA_HOME for Tomcat
set JRE_HOME=
start "Tomcat Server" cmd /k "startup.bat"

echo.
echo ----------------------------------------------------
echo  SUCCESS! Your app is deploying.
echo  URL: http://localhost:8080/SpamEmailDetector/
echo ----------------------------------------------------
pause