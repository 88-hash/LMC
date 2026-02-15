@echo off
echo ==========================================
echo      LeYi Snack System - Auto Launcher
echo ==========================================

echo [1/4] Killing old java/node processes...
taskkill /F /IM java.exe >nul 2>&1
taskkill /F /IM node.exe >nul 2>&1

echo [2/4] Launching Backend...
cd backend
start "LeYi Backend" cmd /k "set JAVA_HOME=C:\Users\lmc\.jdks\temurin-17&& echo Compiling... && call "C:\Users\lmc\Desktop\apache-maven-3.9.12\bin\mvn.cmd" clean compile && echo Starting... && call "C:\Users\lmc\Desktop\apache-maven-3.9.12\bin\mvn.cmd" spring-boot:run"
cd ..

echo [3/4] Launching Frontend Services...
echo   - Starting Client (Port 5173)...
cd client
start "LeYi Client" cmd /c "npm run dev"
cd ..

echo   - Starting Admin (Port 5174)...
cd admin
start "LeYi Admin" cmd /c "npm run dev"
cd ..

echo [4/4] Opening in default browser (wait 15s)...
timeout /t 15 >nul
set "CLIENT_URL=http://localhost:5173/login"
set "ADMIN_URL=http://localhost:5174/admin/login"

REM Use Windows URL handler so it always uses the system default browser.
rundll32 url.dll,FileProtocolHandler %CLIENT_URL%
timeout /t 2 >nul
rundll32 url.dll,FileProtocolHandler %ADMIN_URL%

echo.
echo ==========================================
echo      System Started Successfully!
echo ==========================================
echo.
echo Please do NOT close the black command windows.
echo To stop the system, close all windows.
pause
