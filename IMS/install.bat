@echo off
REM ============================================
REM IMS Professional Installation Script
REM ============================================

title IMS Installation Wizard
color 0A

echo.
echo ============================================================
echo.
echo              IMS - INVENTORY MANAGEMENT SYSTEM
echo                    Installation Wizard v1.0
echo.
echo ============================================================
echo.
timeout /t 2 /nobreak >nul

REM Check for admin rights
echo Checking administrator privileges...
net session >nul 2>&1
if %errorlevel% neq 0 (
    color 0C
    echo.
    echo [ERROR] This installer requires Administrator privileges.
    echo.
    echo Please follow these steps:
    echo   1. Close this window
    echo   2. Right-click on "install.bat"
    echo   3. Select "Run as Administrator"
    echo.
    pause
    exit /b 1
)
echo [OK] Running with administrator privileges
echo.
timeout /t 1 /nobreak >nul

REM Check Java
echo ============================================================
echo STEP 1 of 6: Checking System Requirements
echo ============================================================
echo.
echo Checking Java installation...
java -version >nul 2>&1
if errorlevel 1 (
    color 0C
    echo.
    echo [ERROR] Java Runtime Environment not found!
    echo.
    echo IMS requires Java JRE 17 or higher to run.
    echo.
    echo Please install Java:
    echo   1. Visit: https://adoptium.net/
    echo   2. Download Java 17 or higher
    echo   3. Install Java
    echo   4. Restart your computer
    echo   5. Run this installer again
    echo.
    pause
    exit /b 1
)
echo [OK] Java is installed
java -version 2>&1 | findstr /i "version"
echo.
timeout /t 2 /nobreak >nul

REM Set installation directory
set "INSTALL_DIR=%ProgramFiles%\IMS"
set "APP_DATA_DIR=%APPDATA%\IMS"

echo ============================================================
echo STEP 2 of 6: Preparing Installation
echo ============================================================
echo.
echo Installation directory: %INSTALL_DIR%
echo Application data:       %APP_DATA_DIR%
echo.
echo Creating directories...
if not exist "%INSTALL_DIR%" mkdir "%INSTALL_DIR%"
if not exist "%APP_DATA_DIR%" mkdir "%APP_DATA_DIR%"
if not exist "%APP_DATA_DIR%\data" mkdir "%APP_DATA_DIR%\data"
echo [OK] Directories created
echo.
timeout /t 1 /nobreak >nul

echo ============================================================
echo STEP 3 of 6: Installing Application Files
echo ============================================================
echo.
echo Copying IMS application...
copy /Y "IMS.jar" "%INSTALL_DIR%\" >nul
if errorlevel 1 (
    color 0C
    echo [ERROR] Failed to copy IMS.jar
    pause
    exit /b 1
)
echo [OK] Application copied

echo Copying resources...
if exist "image\logo.ico" copy /Y "image\logo.ico" "%INSTALL_DIR%\" >nul
if exist "image\logo.jpg" copy /Y "image\logo.jpg" "%INSTALL_DIR%\" >nul
echo [OK] Resources copied

echo Copying database files...
if exist "data\init_db.sql" copy /Y "data\init_db.sql" "%APP_DATA_DIR%\data\" >nul
if exist "data\schema.sql" copy /Y "data\schema.sql" "%APP_DATA_DIR%\data\" >nul
echo [OK] Database files copied
echo.
timeout /t 1 /nobreak >nul

echo ============================================================
echo STEP 4 of 6: Initializing Database
echo ============================================================
echo.
echo Setting up database with default users...
echo This may take a moment...
cd /d "%INSTALL_DIR%"
java -cp IMS.jar utils.InitializeDatabase >nul 2>&1
if errorlevel 1 (
    echo [WARNING] Database initialization encountered an issue
    echo [INFO] Database will be created on first run
) else (
    echo [OK] Database initialized successfully
)
echo.
timeout /t 1 /nobreak >nul

echo ============================================================
echo STEP 5 of 6: Creating Launcher and Shortcuts
echo ============================================================
echo.
echo Creating application launcher...
(
echo @echo off
echo cd /d "%INSTALL_DIR%"
echo start javaw -jar "%INSTALL_DIR%\IMS.jar"
) > "%INSTALL_DIR%\IMS.bat"
echo [OK] Launcher created

echo Creating desktop shortcut...
set "DESKTOP=%USERPROFILE%\Desktop"
powershell -Command "$WS = New-Object -ComObject WScript.Shell; $SC = $WS.CreateShortcut('%DESKTOP%\IMS.lnk'); $SC.TargetPath = '%INSTALL_DIR%\IMS.bat'; $SC.WorkingDirectory = '%INSTALL_DIR%'; $SC.IconLocation = '%INSTALL_DIR%\logo.ico'; $SC.Description = 'IMS - Inventory Management System'; $SC.Save()" >nul 2>&1
echo [OK] Desktop shortcut created

echo Creating Start Menu entries...
set "START_MENU=%ProgramData%\Microsoft\Windows\Start Menu\Programs"
if not exist "%START_MENU%\IMS" mkdir "%START_MENU%\IMS"
powershell -Command "$WS = New-Object -ComObject WScript.Shell; $SC = $WS.CreateShortcut('%START_MENU%\IMS\IMS.lnk'); $SC.TargetPath = '%INSTALL_DIR%\IMS.bat'; $SC.WorkingDirectory = '%INSTALL_DIR%'; $SC.IconLocation = '%INSTALL_DIR%\logo.ico'; $SC.Description = 'IMS - Inventory Management System'; $SC.Save()" >nul 2>&1
echo [OK] Start Menu entry created
echo.
timeout /t 1 /nobreak >nul

REM Create Desktop Shortcut
set "DESKTOP=%USERPROFILE%\Desktop"
powershell -Command "$WS = New-Object -ComObject WScript.Shell; $SC = $WS.CreateShortcut('%DESKTOP%\IMS.lnk'); $SC.TargetPath = '%INSTALL_DIR%\IMS.bat'; $SC.WorkingDirectory = '%INSTALL_DIR%'; $SC.IconLocation = '%INSTALL_DIR%\logo.ico'; $SC.Description = 'IMS - Inventory Management System'; $SC.Save()"

REM Create Start Menu Shortcut
set "START_MENU=%ProgramData%\Microsoft\Windows\Start Menu\Programs"
if not exist "%START_MENU%\IMS" mkdir "%START_MENU%\IMS"
powershell -Command "$WS = New-Object -ComObject WScript.Shell; $SC = $WS.CreateShortcut('%START_MENU%\IMS\IMS.lnk'); $SC.TargetPath = '%INSTALL_DIR%\IMS.bat'; $SC.WorkingDirectory = '%INSTALL_DIR%'; $SC.IconLocation = '%INSTALL_DIR%\logo.ico'; $SC.Description = 'IMS - Inventory Management System'; $SC.Save()"

echo ============================================================
echo STEP 6 of 6: Creating Uninstaller
echo ============================================================
echo.
echo Creating uninstaller...
(
echo @echo off
echo title IMS Uninstaller
echo color 0C
echo echo.
echo echo ============================================================
echo echo   Uninstalling IMS - Inventory Management System
echo echo ============================================================
echo echo.
echo echo This will remove:
echo echo   - Program files from %INSTALL_DIR%
echo echo   - Application data from %APP_DATA_DIR%
echo echo   - Desktop shortcut
echo echo   - Start Menu entries
echo echo.
echo set /p confirm="Are you sure you want to uninstall? (Y/N): "
echo if /i "%%confirm%%" NEQ "Y" goto :cancel
echo echo.
echo echo Removing files...
echo if exist "%INSTALL_DIR%" rmdir /s /q "%INSTALL_DIR%"
echo if exist "%APP_DATA_DIR%" rmdir /s /q "%APP_DATA_DIR%"
echo if exist "%DESKTOP%\IMS.lnk" del "%DESKTOP%\IMS.lnk"
echo if exist "%START_MENU%\IMS" rmdir /s /q "%START_MENU%\IMS"
echo echo.
echo echo [OK] IMS has been uninstalled successfully.
echo timeout /t 3
echo exit /b 0
echo :cancel
echo echo.
echo echo Uninstallation cancelled.
echo timeout /t 2
echo exit /b 0
) > "%INSTALL_DIR%\Uninstall.bat"
echo [OK] Uninstaller created

echo Creating uninstaller shortcut...
powershell -Command "$WS = New-Object -ComObject WScript.Shell; $SC = $WS.CreateShortcut('%START_MENU%\IMS\Uninstall IMS.lnk'); $SC.TargetPath = '%INSTALL_DIR%\Uninstall.bat'; $SC.WorkingDirectory = '%INSTALL_DIR%'; $SC.Description = 'Uninstall IMS'; $SC.Save()" >nul 2>&1
echo [OK] Uninstaller shortcut created
echo.
timeout /t 1 /nobreak >nul

color 0A
echo ============================================================
echo.
echo              INSTALLATION COMPLETED SUCCESSFULLY!
echo.
echo ============================================================
echo.
echo IMS has been installed to: %INSTALL_DIR%
echo.
echo You can now launch IMS from:
echo   - Desktop shortcut (IMS icon)
echo   - Start Menu ^> IMS
echo.
echo ============================================================
echo                  DEFAULT LOGIN CREDENTIALS
echo ============================================================
echo.
echo  Role: CEO (Chief Executive Officer)
echo    Username: ceo
echo    Password: ceo123
echo    Access: Full system control
echo.
echo  NOTE: The CEO can create Manager and Cashier accounts
echo        from within the application after logging in.
echo.
echo ============================================================
echo.
echo IMPORTANT: Please note these credentials for first login!
echo.
echo To uninstall: Start Menu ^> IMS ^> Uninstall IMS
echo.
echo ============================================================
echo.
echo Press any key to launch IMS now...
pause >nul

REM Launch IMS
start "" "%INSTALL_DIR%\IMS.bat"

echo.
echo IMS is starting...
timeout /t 2 /nobreak >nul
exit /b 0
