@echo off
setlocal EnableExtensions

set "ROOT=%~dp0"
if "%ROOT:~-1%"=="\" set "ROOT=%ROOT:~0,-1%"

set "BACKEND_DIR=%ROOT%\backend"
set "CLIENT_DIR=%ROOT%\client"
set "ADMIN_DIR=%ROOT%\admin"
set "JAVA_TMP=%ROOT%\.tmp\java"

if /I "%~1"=="run_backend" goto run_backend
if /I "%~1"=="run_client" goto run_client
if /I "%~1"=="run_admin" goto run_admin_start
set "NO_BROWSER=0"
if /I "%~1"=="--no-browser" set "NO_BROWSER=1"

echo [INFO] Project root: %ROOT%
echo.

if not exist "%BACKEND_DIR%\pom.xml" (
  echo [ERROR] backend\pom.xml not found. Abort.
  pause
  exit /b 1
)
if not exist "%CLIENT_DIR%\package.json" (
  echo [ERROR] client\package.json not found. Abort.
  pause
  exit /b 1
)
if not exist "%ADMIN_DIR%\package.json" (
  echo [ERROR] admin\package.json not found. Abort.
  pause
  exit /b 1
)

where npm >nul 2>&1
if errorlevel 1 (
  echo [ERROR] npm not found in PATH. Please install Node.js and retry.
  pause
  exit /b 1
)

echo [STEP] Start backend terminal...
start "LeYi Backend (8080)" "%ComSpec%" /k call "%~f0" run_backend

echo [STEP] Start client terminal...
start "LeYi Client (5173)" "%ComSpec%" /k call "%~f0" run_client

echo [STEP] Start admin terminal...
start "LeYi Admin (5174)" "%ComSpec%" /k call "%~f0" run_admin

echo [STEP] Waiting for frontend ports...
call :wait_port 5173 90
call :wait_port 5174 90
if "%NO_BROWSER%"=="1" (
  echo [INFO] --no-browser enabled, skip opening URLs.
) else (
  start "" "http://127.0.0.1:5173/login"
  start "" "http://127.0.0.1:5174/admin/login"
)

echo [DONE] Start commands sent.
exit /b 0

:run_backend
setlocal EnableExtensions
cd /d "%BACKEND_DIR%"

if not exist "%JAVA_TMP%" mkdir "%JAVA_TMP%" >nul 2>&1
set "TMP=%JAVA_TMP%"
set "TEMP=%JAVA_TMP%"

call :resolve_java_home
if not defined JAVA_HOME (
  echo [ERROR] JAVA_HOME not resolved. Set JAVA_HOME and retry.
  pause
  exit /b 1
)
set "PATH=%JAVA_HOME%\bin;%PATH%"

set "MAVEN_CMD=C:\Users\lmc\Desktop\apache-maven-3.9.12\bin\mvn.cmd"
if not exist "%MAVEN_CMD%" (
  set "MAVEN_CMD=mvn"
)

echo [INFO] JAVA_HOME=%JAVA_HOME%
echo [INFO] TMP=%TMP%
echo [INFO] Running backend...
call "%MAVEN_CMD%" -s settings.xml spring-boot:run
if errorlevel 1 (
  echo.
  echo [ERROR] Backend stopped with non-zero exit code.
)
pause
exit /b %errorlevel%

:wait_port
setlocal EnableExtensions
set "PORT=%~1"
set "MAX_RETRY=%~2"
if "%MAX_RETRY%"=="" set "MAX_RETRY=60"

for /L %%I in (1,1,%MAX_RETRY%) do (
  netstat -ano | findstr /R /C:":%PORT% .*LISTENING" >nul 2>&1
  if not errorlevel 1 (
    echo [INFO] Port %PORT% is listening.
    endlocal & exit /b 0
  )
  ping 127.0.0.1 -n 2 >nul
)

echo [WARN] Port %PORT% not listening within timeout.
endlocal & exit /b 1

:run_admin_start
setlocal EnableExtensions
cd /d "%ADMIN_DIR%"
if not exist "%ROOT%\.tmp\npm-cache" mkdir "%ROOT%\.tmp\npm-cache" >nul 2>&1
set "NPM_CONFIG_CACHE=%ROOT%\.tmp\npm-cache"
if not exist node_modules (
  echo [INFO] Installing admin dependencies...
  call npm install
  if errorlevel 1 (
    echo [ERROR] npm install failed in admin.
    pause
    exit /b 1
  )
)
echo [INFO] Running admin dev server on 5174...
call npm run dev -- --host 127.0.0.1 --port 5174 --strictPort
if errorlevel 1 (
  echo [WARN] Admin dev failed. Rebuilding esbuild and retrying once...
  call npm rebuild esbuild
  call npm run dev -- --host 127.0.0.1 --port 5174 --strictPort
)
if errorlevel 1 (
  echo [ERROR] Admin dev server exited with error after retry.
)
pause
exit /b %errorlevel%

:run_client
setlocal EnableExtensions
cd /d "%CLIENT_DIR%"
if not exist "%ROOT%\.tmp\npm-cache" mkdir "%ROOT%\.tmp\npm-cache" >nul 2>&1
set "NPM_CONFIG_CACHE=%ROOT%\.tmp\npm-cache"
if not exist node_modules (
  echo [INFO] Installing client dependencies...
  call npm install
  if errorlevel 1 (
    echo [ERROR] npm install failed in client.
    pause
    exit /b 1
  )
)
echo [INFO] Running client dev server on 5173...
call npm run dev -- --host 127.0.0.1 --port 5173 --strictPort
if errorlevel 1 (
  echo [WARN] Client dev failed. Rebuilding esbuild and retrying once...
  call npm rebuild esbuild
  call npm run dev -- --host 127.0.0.1 --port 5173 --strictPort
)
if errorlevel 1 (
  echo [ERROR] Client dev server exited with error after retry.
)
pause
exit /b %errorlevel%

:resolve_java_home
if exist "C:\Users\lmc\.jdks\temurin-17\bin\java.exe" (
  set "JAVA_HOME=C:\Users\lmc\.jdks\temurin-17"
  goto :eof
)
if exist "D:\program tool\java\jdk\bin\java.exe" (
  set "JAVA_HOME=D:\program tool\java\jdk"
  goto :eof
)
for /f "delims=" %%I in ('where java 2^>nul') do (
  set "JAVA_EXE=%%~fI"
  goto found_java
)
goto :eof

:found_java
for %%I in ("%JAVA_EXE%") do set "JAVA_BIN=%%~dpI"
for %%I in ("%JAVA_BIN%..") do set "JAVA_HOME=%%~fI"
goto :eof

