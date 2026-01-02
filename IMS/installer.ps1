Add-Type -AssemblyName System.Windows.Forms
Add-Type -AssemblyName System.Drawing

# Get script directory (must be at top level, not inside event handlers)
$scriptPath = if ($PSScriptRoot) { $PSScriptRoot } else { Split-Path -Parent $MyInvocation.MyCommand.Path }

# Create main form
$form = New-Object System.Windows.Forms.Form
$form.Text = "IMS Installer - Inventory Management System"
$form.Size = New-Object System.Drawing.Size(700, 600)
$form.StartPosition = "CenterScreen"
$form.FormBorderStyle = "FixedDialog"
$form.MaximizeBox = $false
$form.BackColor = [System.Drawing.Color]::White

# Set form icon if available
if (Test-Path "$scriptPath\image\logo.ico") {
    $form.Icon = [System.Drawing.Icon]::ExtractAssociatedIcon("$scriptPath\image\logo.ico")
}

# Header Panel
$headerPanel = New-Object System.Windows.Forms.Panel
$headerPanel.Size = New-Object System.Drawing.Size(700, 100)
$headerPanel.Location = New-Object System.Drawing.Point(0, 0)
$headerPanel.BackColor = [System.Drawing.Color]::FromArgb(41, 128, 185)
$form.Controls.Add($headerPanel)

# Add logo to header if available
if (Test-Path "$scriptPath\image\logo.jpg") {
    try {
        $logoPicture = New-Object System.Windows.Forms.PictureBox
        $logoPicture.Size = New-Object System.Drawing.Size(70, 70)
        $logoPicture.Location = New-Object System.Drawing.Point(15, 15)
        $logoPicture.SizeMode = "Zoom"
        $logoPicture.Image = [System.Drawing.Image]::FromFile("$scriptPath\image\logo.jpg")
        $headerPanel.Controls.Add($logoPicture)
    } catch {
        # Silently continue if logo fails to load
    }
}

# Title Label
$titleLabel = New-Object System.Windows.Forms.Label
$titleLabel.Text = "IMS - Inventory Management System"
$titleLabel.Font = New-Object System.Drawing.Font("Segoe UI", 16, [System.Drawing.FontStyle]::Bold)
$titleLabel.ForeColor = [System.Drawing.Color]::White
$titleLabel.AutoSize = $true
$titleLabel.Location = New-Object System.Drawing.Point(100, 20)
$headerPanel.Controls.Add($titleLabel)

$subtitleLabel = New-Object System.Windows.Forms.Label
$subtitleLabel.Text = "Professional Installation Wizard"
$subtitleLabel.Font = New-Object System.Drawing.Font("Segoe UI", 10)
$subtitleLabel.ForeColor = [System.Drawing.Color]::FromArgb(220, 220, 220)
$subtitleLabel.AutoSize = $true
$subtitleLabel.Location = New-Object System.Drawing.Point(100, 50)
$headerPanel.Controls.Add($subtitleLabel)

$versionLabel = New-Object System.Windows.Forms.Label
$versionLabel.Text = "Version 1.0"
$versionLabel.Font = New-Object System.Drawing.Font("Segoe UI", 9)
$versionLabel.ForeColor = [System.Drawing.Color]::FromArgb(200, 200, 200)
$versionLabel.AutoSize = $true
$versionLabel.Location = New-Object System.Drawing.Point(100, 72)
$headerPanel.Controls.Add($versionLabel)

# Installation Options Panel
$optionsPanel = New-Object System.Windows.Forms.GroupBox
$optionsPanel.Text = "Installation Options"
$optionsPanel.Size = New-Object System.Drawing.Size(640, 150)
$optionsPanel.Location = New-Object System.Drawing.Point(30, 120)
$optionsPanel.Font = New-Object System.Drawing.Font("Segoe UI", 10, [System.Drawing.FontStyle]::Bold)
$form.Controls.Add($optionsPanel)

# Installation Directory Label
$dirLabel = New-Object System.Windows.Forms.Label
$dirLabel.Text = "Installation Directory:"
$dirLabel.Font = New-Object System.Drawing.Font("Segoe UI", 9)
$dirLabel.AutoSize = $true
$dirLabel.Location = New-Object System.Drawing.Point(15, 30)
$optionsPanel.Controls.Add($dirLabel)

# Installation Directory TextBox
$dirTextBox = New-Object System.Windows.Forms.TextBox
$dirTextBox.Size = New-Object System.Drawing.Size(450, 25)
$dirTextBox.Location = New-Object System.Drawing.Point(15, 55)
$dirTextBox.Font = New-Object System.Drawing.Font("Segoe UI", 9)
$dirTextBox.Text = "$env:ProgramFiles\IMS"
$optionsPanel.Controls.Add($dirTextBox)

# Browse Button
$browseButton = New-Object System.Windows.Forms.Button
$browseButton.Text = "Browse..."
$browseButton.Size = New-Object System.Drawing.Size(100, 25)
$browseButton.Location = New-Object System.Drawing.Point(475, 55)
$browseButton.Font = New-Object System.Drawing.Font("Segoe UI", 9)
$optionsPanel.Controls.Add($browseButton)

# Browse folder dialog
$browseButton.Add_Click({
    $folderBrowser = New-Object System.Windows.Forms.FolderBrowserDialog
    $folderBrowser.Description = "Select installation directory for IMS"
    $folderBrowser.RootFolder = "MyComputer"
    $folderBrowser.SelectedPath = $dirTextBox.Text
    
    if ($folderBrowser.ShowDialog() -eq [System.Windows.Forms.DialogResult]::OK) {
        $dirTextBox.Text = Join-Path $folderBrowser.SelectedPath "IMS"
    }
})

# Desktop Shortcut Checkbox
$desktopCheckbox = New-Object System.Windows.Forms.CheckBox
$desktopCheckbox.Text = "Create desktop shortcut"
$desktopCheckbox.AutoSize = $true
$desktopCheckbox.Location = New-Object System.Drawing.Point(15, 95)
$desktopCheckbox.Font = New-Object System.Drawing.Font("Segoe UI", 9)
$desktopCheckbox.Checked = $true
$optionsPanel.Controls.Add($desktopCheckbox)

# Start Menu Shortcut Checkbox
$startMenuCheckbox = New-Object System.Windows.Forms.CheckBox
$startMenuCheckbox.Text = "Create Start Menu shortcut"
$startMenuCheckbox.AutoSize = $true
$startMenuCheckbox.Location = New-Object System.Drawing.Point(250, 95)
$startMenuCheckbox.Font = New-Object System.Drawing.Font("Segoe UI", 9)
$startMenuCheckbox.Checked = $true
$optionsPanel.Controls.Add($startMenuCheckbox)

# Status Label
$statusLabel = New-Object System.Windows.Forms.Label
$statusLabel.Text = "Ready to install. Configure options above and click Install."
$statusLabel.Font = New-Object System.Drawing.Font("Segoe UI", 10)
$statusLabel.AutoSize = $false
$statusLabel.Size = New-Object System.Drawing.Size(640, 30)
$statusLabel.Location = New-Object System.Drawing.Point(30, 285)
$form.Controls.Add($statusLabel)

# Progress Bar
$progressBar = New-Object System.Windows.Forms.ProgressBar
$progressBar.Size = New-Object System.Drawing.Size(640, 25)
$progressBar.Location = New-Object System.Drawing.Point(30, 325)
$progressBar.Style = "Continuous"
$form.Controls.Add($progressBar)

# Details TextBox
$detailsBox = New-Object System.Windows.Forms.TextBox
$detailsBox.Multiline = $true
$detailsBox.ScrollBars = "Vertical"
$detailsBox.Size = New-Object System.Drawing.Size(640, 130)
$detailsBox.Location = New-Object System.Drawing.Point(30, 360)
$detailsBox.Font = New-Object System.Drawing.Font("Consolas", 9)
$detailsBox.ReadOnly = $true
$detailsBox.BackColor = [System.Drawing.Color]::FromArgb(250, 250, 250)
$form.Controls.Add($detailsBox)

# Install Button
$installButton = New-Object System.Windows.Forms.Button
$installButton.Text = "Install"
$installButton.Size = New-Object System.Drawing.Size(130, 40)
$installButton.Location = New-Object System.Drawing.Point(410, 510)
$installButton.BackColor = [System.Drawing.Color]::FromArgb(46, 204, 113)
$installButton.ForeColor = [System.Drawing.Color]::White
$installButton.FlatStyle = "Flat"
$installButton.Font = New-Object System.Drawing.Font("Segoe UI", 11, [System.Drawing.FontStyle]::Bold)
$installButton.Cursor = [System.Windows.Forms.Cursors]::Hand
$form.Controls.Add($installButton)

# Close Button
$closeButton = New-Object System.Windows.Forms.Button
$closeButton.Text = "Exit"
$closeButton.Size = New-Object System.Drawing.Size(110, 40)
$closeButton.Location = New-Object System.Drawing.Point(550, 510)
$closeButton.FlatStyle = "Flat"
$closeButton.Font = New-Object System.Drawing.Font("Segoe UI", 10)
$closeButton.BackColor = [System.Drawing.Color]::FromArgb(200, 200, 200)
$closeButton.Cursor = [System.Windows.Forms.Cursors]::Hand
$form.Controls.Add($closeButton)

# Function to add text to details
function Add-Details {
    param($text)
    $detailsBox.AppendText("$text`r`n")
}

# Function to update status
function Update-Status {
    param($text, $progress)
    $statusLabel.Text = $text
    $progressBar.Value = $progress
    $form.Refresh()
}

# Check Java
function Check-Java {
    Update-Status "Checking Java installation..." 10
    Add-Details "[1/6] Checking Java Runtime Environment..."
    
    try {
        $javaVersion = & java -version 2>&1 | Select-String "version"
        Add-Details "[OK] Java is installed"
        Add-Details "     $javaVersion"
        Start-Sleep -Milliseconds 500
        return $true
    } catch {
        Add-Details "[ERROR] Java is not installed!"
        Add-Details "Please install Java 17 or higher from:"
        Add-Details "https://adoptium.net/"
        [System.Windows.Forms.MessageBox]::Show(
            "Java Runtime Environment is not installed!`n`nPlease install Java 17 or higher from:`nhttps://adoptium.net/",
            "Java Not Found",
            [System.Windows.Forms.MessageBoxButtons]::OK,
            [System.Windows.Forms.MessageBoxIcon]::Error
        )
        return $false
    }
}

# Install Function
$installButton.Add_Click({
    $installButton.Enabled = $false
    $browseButton.Enabled = $false
    $dirTextBox.Enabled = $false
    $desktopCheckbox.Enabled = $false
    $startMenuCheckbox.Enabled = $false
    
    # Check Java first
    if (-not (Check-Java)) {
        $installButton.Enabled = $true
        $browseButton.Enabled = $true
        $dirTextBox.Enabled = $true
        $desktopCheckbox.Enabled = $true
        $startMenuCheckbox.Enabled = $true
        return
    }
    
    # Get installation directory from textbox
    $installDir = $dirTextBox.Text.Trim()
    
    # Validate directory
    if ([string]::IsNullOrWhiteSpace($installDir)) {
        [System.Windows.Forms.MessageBox]::Show(
            "Please specify a valid installation directory.",
            "Invalid Directory",
            [System.Windows.Forms.MessageBoxButtons]::OK,
            [System.Windows.Forms.MessageBoxIcon]::Warning
        )
        $installButton.Enabled = $true
        $browseButton.Enabled = $true
        $dirTextBox.Enabled = $true
        $desktopCheckbox.Enabled = $true
        $startMenuCheckbox.Enabled = $true
        return
    }
    
    Update-Status "Preparing installation directories..." 20
    Add-Details "`n[2/6] Creating installation directories..."
    Add-Details "     Installation path: $installDir"
    
    # Create directories
    try {
        if (-not (Test-Path $installDir)) {
            New-Item -ItemType Directory -Path $installDir -Force | Out-Null
        }
        # Only create data folder - app will create database file here
        New-Item -ItemType Directory -Path "$installDir\data" -Force -ErrorAction SilentlyContinue | Out-Null
        Add-Details "[OK] Installation directory created"
        Start-Sleep -Milliseconds 500
    } catch {
        Add-Details "[ERROR] Failed to create directories: $_"
        [System.Windows.Forms.MessageBox]::Show(
            "Failed to create installation directory. Please check permissions.`n`nError: $_",
            "Installation Error",
            [System.Windows.Forms.MessageBoxButtons]::OK,
            [System.Windows.Forms.MessageBoxIcon]::Error
        )
        $installButton.Enabled = $true
        $browseButton.Enabled = $true
        $dirTextBox.Enabled = $true
        $desktopCheckbox.Enabled = $true
        $startMenuCheckbox.Enabled = $true
        return
    }
    
    Update-Status "Copying application files..." 40
    Add-Details "`n[3/6] Copying application files..."
    
    # Copy files
    try {
        # Use global $scriptPath variable set at script start
        
        # Copy main JAR (contains all dependencies and resources)
        Copy-Item "$scriptPath\IMS.jar" -Destination "$installDir\IMS.jar" -Force
        Add-Details "[OK] IMS.jar copied (includes SQLite driver and resources)"
        Start-Sleep -Milliseconds 300
        
        # Copy icon files for shortcuts
        if (Test-Path "$scriptPath\image\logo.ico") {
            Copy-Item "$scriptPath\image\logo.ico" -Destination "$installDir\logo.ico" -Force
            Add-Details "[OK] Icon file copied for shortcuts"
            Start-Sleep -Milliseconds 300
        }
        
        # Copy uninstaller
        if (Test-Path "$scriptPath\uninstaller.ps1") {
            Copy-Item "$scriptPath\uninstaller.ps1" -Destination "$installDir\uninstaller.ps1" -Force
            Add-Details "[OK] Uninstaller copied"
            Start-Sleep -Milliseconds 300
        }
        
        Add-Details "[INFO] All dependencies are bundled in IMS.jar"
        Add-Details "[INFO] Database will be created automatically on first run"
        Start-Sleep -Milliseconds 300
    } catch {
        Add-Details "[ERROR] Failed to copy files: $_"
        [System.Windows.Forms.MessageBox]::Show(
            "Failed to copy application files.`n`nError: $_",
            "Installation Error",
            [System.Windows.Forms.MessageBoxButtons]::OK,
            [System.Windows.Forms.MessageBoxIcon]::Error
        )
        $installButton.Enabled = $true
        $browseButton.Enabled = $true
        $dirTextBox.Enabled = $true
        $desktopCheckbox.Enabled = $true
        $startMenuCheckbox.Enabled = $true
        return
    }
    
    Update-Status "Creating launcher..." 60
    Add-Details "`n[4/6] Creating application launcher..."
    
    # Create launcher
    $launcherContent = @"
@echo off
cd /d "%~dp0"
start javaw -jar "%~dp0IMS.jar"
"@
    Set-Content -Path "$installDir\Run-IMS.bat" -Value $launcherContent
    Add-Details "[OK] Launcher created: Run-IMS.bat"
    Start-Sleep -Milliseconds 300
    
    # Create uninstaller launcher
    $uninstallerContent = @"
@echo off
powershell -ExecutionPolicy Bypass -Command "Start-Process powershell -ArgumentList '-ExecutionPolicy Bypass -File \"%~dp0uninstaller.ps1\" -InstallDir \"%~dp0\"' -Verb RunAs"
"@
    Set-Content -Path "$installDir\Uninstall.bat" -Value $uninstallerContent
    Add-Details "[OK] Uninstaller created: Uninstall.bat"
    Start-Sleep -Milliseconds 500
    
    Update-Status "Creating shortcuts..." 80
    Add-Details "`n[5/6] Creating shortcuts..."
    
    # Create desktop shortcut if checkbox is checked
    if ($desktopCheckbox.Checked) {
        try {
            $WshShell = New-Object -ComObject WScript.Shell
            $Shortcut = $WshShell.CreateShortcut("$env:USERPROFILE\Desktop\IMS.lnk")
            $Shortcut.TargetPath = "$installDir\Run-IMS.bat"
            $Shortcut.WorkingDirectory = $installDir
            if (Test-Path "$installDir\logo.ico") {
                $Shortcut.IconLocation = "$installDir\logo.ico"
            }
            $Shortcut.Description = "IMS - Inventory Management System"
            $Shortcut.Save()
            Add-Details "[OK] Desktop shortcut created"
            Start-Sleep -Milliseconds 300
        } catch {
            Add-Details "[WARNING] Could not create desktop shortcut: $_"
        }
    } else {
        Add-Details "[SKIP] Desktop shortcut creation skipped"
    }
    
    # Create Start Menu shortcut if checkbox is checked
    if ($startMenuCheckbox.Checked) {
        try {
            $startMenuPath = "$env:APPDATA\Microsoft\Windows\Start Menu\Programs"
            if (-not (Test-Path $startMenuPath)) {
                New-Item -ItemType Directory -Path $startMenuPath -Force | Out-Null
            }
            
            $WshShell = New-Object -ComObject WScript.Shell
            $Shortcut = $WshShell.CreateShortcut("$startMenuPath\IMS.lnk")
            $Shortcut.TargetPath = "$installDir\Run-IMS.bat"
            $Shortcut.WorkingDirectory = $installDir
            if (Test-Path "$installDir\logo.ico") {
                $Shortcut.IconLocation = "$installDir\logo.ico"
            }
            $Shortcut.Description = "IMS - Inventory Management System"
            $Shortcut.Save()
            Add-Details "[OK] Start Menu shortcut created"
            Start-Sleep -Milliseconds 300
        } catch {
            Add-Details "[WARNING] Could not create Start Menu shortcut: $_"
        }
    } else {
        Add-Details "[SKIP] Start Menu shortcut creation skipped"
    }
    
    Update-Status "Registering uninstaller..." 90
    Add-Details "`n[6/6] Registering with Windows..."
    
    # Add to Windows Programs and Features (Control Panel)
    try {
        $uninstallKey = "HKLM:\SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\IMS"
        
        # Create registry key
        if (!(Test-Path $uninstallKey)) {
            New-Item -Path $uninstallKey -Force | Out-Null
        }
        
        # Set registry values
        Set-ItemProperty -Path $uninstallKey -Name "DisplayName" -Value "IMS - Inventory Management System"
        Set-ItemProperty -Path $uninstallKey -Name "DisplayVersion" -Value "1.0"
        Set-ItemProperty -Path $uninstallKey -Name "Publisher" -Value "IMS Development Team"
        Set-ItemProperty -Path $uninstallKey -Name "InstallLocation" -Value $installDir
        Set-ItemProperty -Path $uninstallKey -Name "UninstallString" -Value "`"$installDir\Uninstall.bat`""
        Set-ItemProperty -Path $uninstallKey -Name "DisplayIcon" -Value "$installDir\logo.ico"
        Set-ItemProperty -Path $uninstallKey -Name "NoModify" -Value 1 -Type DWord
        Set-ItemProperty -Path $uninstallKey -Name "NoRepair" -Value 1 -Type DWord
        
        # Calculate estimated size (in KB)
        $size = (Get-ChildItem -Path $installDir -Recurse -ErrorAction SilentlyContinue | Measure-Object -Property Length -Sum).Sum / 1KB
        Set-ItemProperty -Path $uninstallKey -Name "EstimatedSize" -Value ([int]$size) -Type DWord
        
        Add-Details "[OK] Registered in Windows Control Panel"
        Add-Details "[INFO] Uninstall via: Settings > Apps > IMS"
        Start-Sleep -Milliseconds 500
    } catch {
        Add-Details "[WARNING] Could not register uninstaller: $_"
        Add-Details "[INFO] You can still use: $installDir\Uninstall.bat"
    }
    
    Update-Status "Installation complete!" 100
    Add-Details "`n=========================================="
    Add-Details "Installation completed successfully!"
    Add-Details "=========================================="
    Add-Details "Installation Details:"
    Add-Details "  Location: $installDir"
    if ($desktopCheckbox.Checked) {
        Add-Details "  Desktop shortcut: Created"
    }
    if ($startMenuCheckbox.Checked) {
        Add-Details "  Start Menu shortcut: Created"
    }
    Add-Details "`nUninstall Options:"
    Add-Details "  • Windows Settings > Apps > IMS"
    Add-Details "  • Or run: $installDir\Uninstall.bat"
    Add-Details "`nDefault Login:"
    Add-Details "  Username: ceo"
    Add-Details "  Password: ceo123"
    Add-Details "=========================================="
    
    $statusLabel.Text = "Installation completed successfully!"
    $statusLabel.ForeColor = [System.Drawing.Color]::Green
    $installButton.Text = "Done"
    $installButton.BackColor = [System.Drawing.Color]::FromArgb(150, 150, 150)
    
    # Show success message
    $result = [System.Windows.Forms.MessageBox]::Show(
        "IMS has been installed successfully!`n`nInstallation Location: $installDir`n`nWould you like to launch IMS now?",
        "Installation Complete",
        [System.Windows.Forms.MessageBoxButtons]::YesNo,
        [System.Windows.Forms.MessageBoxIcon]::Information
    )
    
    if ($result -eq [System.Windows.Forms.DialogResult]::Yes) {
        Start-Process "$installDir\Run-IMS.bat"
        $form.Close()
    }
})

# Close button action
$closeButton.Add_Click({
    $form.Close()
})

# Initial Java check on load
$form.Add_Shown({
    Add-Details "=========================================="
    Add-Details "   IMS Installation Wizard"
    Add-Details "   Version 1.0"
    Add-Details "=========================================="
    Add-Details ""
    
    if (Check-Java) {
        $statusLabel.Text = "Ready to install. Configure options above and click Install."
        $statusLabel.ForeColor = [System.Drawing.Color]::FromArgb(46, 204, 113)
    } else {
        $statusLabel.Text = "Java not found. Please install Java 17+ first."
        $statusLabel.ForeColor = [System.Drawing.Color]::Red
        $installButton.Enabled = $false
        $installButton.BackColor = [System.Drawing.Color]::FromArgb(150, 150, 150)
    }
    $progressBar.Value = 0
})

# Show form
[void]$form.ShowDialog()
