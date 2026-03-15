@echo off
if /I " "%%~1==run_admin goto run_admin
echo main
exit /b 0
:run_admin
echo ok
