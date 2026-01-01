@echo off
REM ============================================
REM Build IMS.exe - Ready to Run!
REM ============================================

echo ========================================
echo Building IMS Application...
echo ========================================
echo.

REM Check Java
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java not found! Install Java JDK 14 or higher.
    pause
    exit /b 1
)

REM Create folders
if not exist "lib" mkdir lib
if not exist "out" mkdir out
if not exist "data" mkdir data

REM Check SQLite driver
if not exist "lib\sqlite-jdbc.jar" (
    if exist "lib\sqlite-jdbc-*.jar" (
        for %%f in (lib\sqlite-jdbc-*.jar) do copy "%%f" "lib\sqlite-jdbc.jar" >nul
    ) else (
        echo ERROR: sqlite-jdbc.jar not found!
        echo Download from: https://github.com/xerial/sqlite-jdbc/releases
        pause
        exit /b 1
    )
)

REM Clean old files
echo Cleaning...
if exist "out" rmdir /s /q out
if exist "IMS.jar" del /q IMS.jar
if exist "IMS.exe" del /q IMS.exe
mkdir out

REM Compile
echo Compiling Java files...
echo This may take a moment...
javac -cp "lib\sqlite-jdbc.jar" -d out -encoding UTF-8 -sourcepath src src\main\Main.java src\database\*.java src\models\*.java src\ui\*.java src\utils\*.java
if errorlevel 1 (
    echo.
    echo ERROR: Compilation failed!
    echo Check the error messages above.
    pause
    exit /b 1
)

echo Compilation successful!
echo Verifying output...
if not exist "out\main\Main.class" (
    echo.
    echo ERROR: Main.class not found in out\main\
    echo Compilation may have failed silently.
    pause
    exit /b 1
)

REM Copy image folder to out directory
echo Copying image resources...
if exist "image" (
    if not exist "out\image" mkdir out\image
    xcopy /E /I /Y "image" "out\image" >nul
    echo Image resources copied!
) else (
    echo WARNING: image folder not found!
)

REM Copy data folder to out directory (for database files)
echo Copying data resources...
if exist "data" (
    if not exist "out\data" mkdir out\data
    xcopy /E /I /Y "data" "out\data" >nul
    echo Data resources copied!
)

REM Extract SQLite JDBC jar into out folder for fat jar
echo Extracting SQLite JDBC library...
cd out
jar xf ..\lib\sqlite-jdbc.jar
if errorlevel 1 (
    echo WARNING: Could not extract SQLite JDBC jar!
)
del /q META-INF\*.SF 2>nul
del /q META-INF\*.RSA 2>nul
del /q META-INF\*.DSA 2>nul
cd ..

REM Create JAR with all resources
echo Creating JAR file with resources...
cd out
jar cvfe ..\IMS.jar main.Main .
if errorlevel 1 (
    echo.
    echo ERROR: JAR creation failed!
    cd ..
    pause
    exit /b 1
)
cd ..

if not exist "IMS.jar" (
    echo.
    echo ERROR: IMS.jar was not created!
    pause
    exit /b 1
)

echo JAR created successfully!
echo.

REM Create executable
echo Creating IMS.exe...
echo This may take a few minutes...
echo.

REM Check if jpackage exists
where jpackage >nul 2>&1
if errorlevel 1 (
    echo.
    echo WARNING: jpackage not found in PATH!
    echo Trying to find jpackage in Java installation...
    
    REM Try to find jpackage in common Java locations
    set JPACKAGE_FOUND=0
    if exist "C:\Program Files\Eclipse Adoptium\jdk-17\bin\jpackage.exe" (
        set "JPACKAGE_PATH=C:\Program Files\Eclipse Adoptium\jdk-17\bin\jpackage.exe"
        set JPACKAGE_FOUND=1
    )
    if exist "C:\Program Files\Java\jdk-17\bin\jpackage.exe" (
        set "JPACKAGE_PATH=C:\Program Files\Java\jdk-17\bin\jpackage.exe"
        set JPACKAGE_FOUND=1
    )
    if exist "C:\Program Files\Eclipse Adoptium\jdk-21\bin\jpackage.exe" (
        set "JPACKAGE_PATH=C:\Program Files\Eclipse Adoptium\jdk-21\bin\jpackage.exe"
        set JPACKAGE_FOUND=1
    )
    if exist "C:\Program Files\Java\jdk-21\bin\jpackage.exe" (
        set "JPACKAGE_PATH=C:\Program Files\Java\jdk-21\bin\jpackage.exe"
        set JPACKAGE_FOUND=1
    )
    
    REM Try to find any JDK with jpackage
    for /d %%d in ("C:\Program Files\Eclipse Adoptium\jdk-*") do (
        if exist "%%d\bin\jpackage.exe" (
            set "JPACKAGE_PATH=%%d\bin\jpackage.exe"
            set JPACKAGE_FOUND=1
            goto :found_jpackage
        )
    )
    
    :found_jpackage
    if %JPACKAGE_FOUND%==0 (
        echo.
        echo ERROR: jpackage not found! Need Java JDK 14+ for .exe creation.
        echo.
        echo You can still run the application using:
        echo   java -jar IMS.jar
        echo.
        echo To create .exe, install Java JDK 14 or higher from:
        echo   https://adoptium.net/
        echo.
        pause
        exit /b 1
    ) else (
        echo Found jpackage at: %JPACKAGE_PATH%
        if exist "image\logo.ico" (
            "%JPACKAGE_PATH%" --input . --name "IMS" --main-jar IMS.jar --main-class main.Main --type exe --win-dir-chooser --win-menu --win-shortcut --app-version 1.0 --dest . --icon "image\logo.ico"
        ) else (
            "%JPACKAGE_PATH%" --input . --name "IMS" --main-jar IMS.jar --main-class main.Main --type exe --win-dir-chooser --win-menu --win-shortcut --app-version 1.0 --dest .
        )
    )
) else (
    echo jpackage found in PATH, creating executable...
    if exist "image\logo.ico" (
        jpackage --input . --name "IMS" --main-jar IMS.jar --main-class main.Main --type exe --win-dir-chooser --win-menu --win-shortcut --app-version 1.0 --dest . --icon "image\logo.ico"
    ) else (
        jpackage --input . --name "IMS" --main-jar IMS.jar --main-class main.Main --type exe --win-dir-chooser --win-menu --win-shortcut --app-version 1.0 --dest .
    )
)

if exist "IMS.exe" (
    echo.
    echo ========================================
    echo SUCCESS! IMS.exe Created!
    echo ========================================
    echo.
    echo Just double-click IMS.exe to run!
    echo.
    echo Location: %CD%\IMS.exe
    echo.
) else (
    echo.
    echo ========================================
    echo WARNING: IMS.exe creation failed!
    echo ========================================
    echo.
    echo But IMS.jar was created successfully!
    echo You can still run the application using:
    echo   java -jar IMS.jar
    echo.
    echo To create .exe, you need:
    echo - Java JDK 14 or higher (not just JRE)
    echo - jpackage tool (comes with JDK 14+)
    echo.
    echo Download from: https://adoptium.net/
    echo.
)

pause
