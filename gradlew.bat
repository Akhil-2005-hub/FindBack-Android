@echo off
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0bootstrap-gradle.ps1" %*
exit /b %ERRORLEVEL%
