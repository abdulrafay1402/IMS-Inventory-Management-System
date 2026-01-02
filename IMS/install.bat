@echo off
title IMS Installer
color 0B

echo.
echo ========================================
echo    IMS Installation Wizard
echo    Inventory Management System
echo ========================================
echo.
echo Starting installer...
echo.

REM Launch PowerShell GUI Installer with admin privileges
powershell -ExecutionPolicy Bypass -Command "Start-Process powershell -ArgumentList '-ExecutionPolicy Bypass -File \"%~dp0installer.ps1\"' -Verb RunAs"

exit
