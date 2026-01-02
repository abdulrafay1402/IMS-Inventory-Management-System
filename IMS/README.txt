═══════════════════════════════════════════════════════════════════════════════
                         IMS - INVENTORY MANAGEMENT SYSTEM
                              Installation Package v1.0
═══════════════════════════════════════════════════════════════════════════════

📦 PACKAGE CONTENTS:

   • IMS.jar             - Main application (all dependencies bundled)
   • install.bat         - Installer launcher (requests admin rights)
   • installer.ps1       - Professional GUI installation wizard with logo
   • uninstaller.ps1     - Professional GUI uninstaller
   • image/logo.ico      - Application icon for shortcuts and taskbar
   • image/logo.jpg      - Logo displayed in installer and application
   • README.txt          - This comprehensive guide

   NOTE: SQLite JDBC driver, database schemas, logo, and all resources are
         embedded inside IMS.jar. No external dependencies required!

═══════════════════════════════════════════════════════════════════════════════

📋 SYSTEM REQUIREMENTS:

   ✓ Operating System:   Windows 10 or Windows 11
   ✓ Java Runtime:       JRE 17 or higher
   ✓ Disk Space:         100 MB minimum
   ✓ Memory (RAM):       2 GB minimum (4 GB recommended)
   ✓ Permissions:        Administrator rights (for Program Files installation)

═══════════════════════════════════════════════════════════════════════════════

🚀 INSTALLATION STEPS:

   1. EXTRACT ZIP FILE
      • Right-click IMS-Installer.zip
      • Select "Extract All..."
      • Choose any location (e.g., Desktop)
      • A folder named "IMS-Installer" will appear

   2. INSTALL JAVA (if not installed)
      • Download from: https://adoptium.net/
      • Install Java 17 or higher
      • Restart computer after installation

   3. RUN INSTALLER
      • Open IMS-Installer folder
      • Double-click "install.bat"
      • Allow administrator permissions when UAC prompt appears
      • Professional GUI installer window opens displaying:
        - IMS logo in header
        - Modern blue-themed interface
        - Real-time installation progress
      
   4. CONFIGURE INSTALLATION OPTIONS
      • Installation Directory:
        - Default: C:\Program Files\IMS (professional standard)
        - Click "Browse..." to choose custom location
        - Can install on any drive (C:, D:, E:, etc.)
      
      • Shortcuts (checkboxes):
        ☑ Create desktop shortcut - Quick access from desktop
        ☑ Create Start Menu shortcut - Find in Windows Start Menu
        (Both recommended, but optional)
      
      • Click green "Install" button
      • Watch progress through 6 installation steps
      • Installation completes in 5-10 seconds

   5. LAUNCH APPLICATION
      • Option 1: Click "Yes" when prompted to launch immediately
      • Option 2: Double-click "IMS" desktop shortcut (if created)
      • Option 3: Press Windows key, type "IMS", click result
      • Option 4: Navigate to installation folder → Run-IMS.bat
      
      Application window displays IMS logo on taskbar!

═══════════════════════════════════════════════════════════════════════════════

📁 INSTALLATION LOCATION:

   Default: C:\Program Files\IMS\
   (Can be customized during installation)

   Directory structure:
   ├── IMS.jar                      Main application (14 MB)
   ├── Run-IMS.bat                  Quick launcher
   ├── Uninstall.bat                Uninstaller launcher
   ├── logo.ico                     Application icon
   ├── uninstaller.ps1              Uninstall script
   └── data/
       └── inventory_db.sqlite      Database (created on first run)

═══════════════════════════════════════════════════════════════════════════════

✨ PROFESSIONAL INSTALLER FEATURES:

   ✓ Logo Display          - IMS logo prominently shown in installer header
   ✓ Custom Directory      - Install anywhere: Program Files, custom drive, etc.
   ✓ Browse Button         - Easy folder selection with file browser
   ✓ Optional Shortcuts    - Choose desktop and/or Start Menu shortcuts
   ✓ Progress Tracking     - Real-time progress bar and detailed log
   ✓ Auto Java Check       - Validates Java installation before proceeding
   ✓ Admin Elevation       - Automatically requests necessary permissions
   ✓ Control Panel Entry   - Registers in Windows Apps & Features
   ✓ Professional UI       - Modern design with branded colors
   ✓ Error Handling        - Clear error messages and troubleshooting
   ✓ Launch Option         - Start application immediately after install
   ✓ Complete Uninstaller  - Professional removal with GUI interface

═══════════════════════════════════════════════════════════════════════════════

🔑 DEFAULT LOGIN CREDENTIALS:

   ┌─────────────────────────────┐
   │  Username:  ceo             │
   │  Password:  ceo123          │
   └─────────────────────────────┘

   ⚠️  IMPORTANT: Change password immediately after first login!

═══════════════════════════════════════════════════════════════════════════════

🎯 APPLICATION FEATURES:

   ✓ Role-Based Access Control
     • CEO Dashboard       - Full system control
     • Manager Dashboard   - Inventory & staff management
     • Cashier Dashboard   - Sales & billing

   ✓ Inventory Management
     • Add, update, and transfer stock
     • Real-time inventory tracking
     • Product categorization
     • Low stock alerts

   ✓ Salary Management
     • Individual payment processing
     • Base salary + bonus + adjustments
     • Payment history tracking
     • Detailed payment breakdown

   ✓ Expense Tracking
     • Record business expenses
     • Categorized tracking
     • Date range filtering
     • Expense reports

   ✓ Bill Generation
     • Multi-item customer bills
     • Automatic inventory updates
     • Receipt printing
     • Past bill viewing

   ✓ Financial Reports
     • Profit/Loss analysis
     • Revenue tracking
     • Expense breakdowns
     • Date range filtering

   ✓ User Management
     • Add managers with salaries
     • Approve cashier requests
     • View all employees
     • Role-based permissions

   ✓ Technical Features
     • SQLite embedded database
     • No server required
     • Automatic database initialization
     • Data persistence

═══════════════════════════════════════════════════════════════════════════════

🛠️ TECHNICAL DETAILS:

   • Framework:        Java Swing (Desktop GUI)
   • Database:         SQLite 3.x (embedded)
   • JDBC Driver:      org.xerial.sqlite-jdbc (bundled in JAR)
   • Architecture:     Standalone desktop application
   • Data Storage:     Local SQLite file (data/inventory_db.sqlite)
   • Dependencies:     All bundled - no external JARs needed

═══════════════════════════════════════════════════════════════════════════════

❓ TROUBLESHOOTING:

   Problem: "Java is not installed" error
   Solution: Install Java 17+ from https://adoptium.net/

   Problem: Installer won't run
   Solution: Right-click install.bat → "Run as administrator"

   Problem: Application won't launch
   Solution: Open Command Prompt and run:
             cd C:\Program Files\IMS
             java -jar IMS.jar
             Check for error messages

   Problem: Database errors on startup
   Solution: Delete data/inventory_db.sqlite
             Restart IMS - database recreates automatically

   Problem: Desktop shortcut missing
   Solution: Navigate to installation folder
             Right-click Run-IMS.bat → Send to → Desktop

═══════════════════════════════════════════════════════════════════════════════

🗑️ UNINSTALLATION:

   Method 1 - Windows Settings (Recommended):
   • Open Windows Settings (Windows key + I)
   • Navigate to Apps > Installed Apps (or Programs and Features)
   • Search for "IMS - Inventory Management System"
   • Click "..." menu → Uninstall
   • Professional uninstaller GUI opens automatically
   • Review what will be removed
   • Confirm uninstallation

   Method 2 - Direct Uninstaller:
   • Navigate to installation folder (e.g., C:\Program Files\IMS)
   • Double-click "Uninstall.bat"
   • Allow admin permissions when prompted
   • Professional GUI uninstaller opens
   • Follow on-screen instructions

   What Gets Removed:
   ✓ All application files (IMS.jar, launchers, etc.)
   ✓ Desktop shortcut (if exists)
   ✓ Start Menu shortcut (if exists)
   ✓ Database file (data/inventory_db.sqlite)
   ✓ Windows registry entry (Control Panel)
   ✓ Entire installation directory

   ⚠️  IMPORTANT: Uninstalling permanently deletes all data!
       BACKUP data/inventory_db.sqlite before uninstalling if you want to
       keep your inventory, bills, expenses, and user data.

═══════════════════════════════════════════════════════════════════════════════

📞 SUPPORT:

   • GitHub Repository:
     https://github.com/abdulrafay1402/IMS--Inventory-Management-System-

   • Report Issues:
     https://github.com/abdulrafay1402/IMS--Inventory-Management-System-/issues

   • Documentation:
     Available in website/ folder

═══════════════════════════════════════════════════════════════════════════════

💡 FIRST TIME SETUP GUIDE:

   1. Login with ceo/ceo123
   2. Change CEO password (Profile → Change Password)
   3. Add managers (CEO Dashboard → Add Manager)
   4. Add initial inventory (CEO Dashboard → Master Inventory)
   5. Set up expenses (CEO Dashboard → Add Expense)
   6. Managers can request cashiers (Manager Dashboard → Request Cashier)
   7. CEO approves cashiers (CEO Dashboard → Approve Cashiers)
   8. Ready for operation!

═══════════════════════════════════════════════════════════════════════════════

📜 LICENSE:

   Open Source - Free to use for commercial and personal purposes

═══════════════════════════════════════════════════════════════════════════════
