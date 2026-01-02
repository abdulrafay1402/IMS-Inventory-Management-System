Add-Type -AssemblyName System.Windows.Forms
Add-Type -AssemblyName System.Drawing

# Get the installation directory from a parameter or registry/config
param(
    [string]$InstallDir = ""
)

# If no install dir provided, try to detect it
if ([string]::IsNullOrWhiteSpace($InstallDir)) {
    # Check common locations
    $possibleLocations = @(
        "$env:ProgramFiles\IMS",
        "$env:LOCALAPPDATA\IMS",
        "$env:USERPROFILE\IMS"
    )
    
    foreach ($loc in $possibleLocations) {
        if (Test-Path "$loc\IMS.jar") {
            $InstallDir = $loc
            break
        }
    }
}

# Create main form
$form = New-Object System.Windows.Forms.Form
$form.Text = "IMS Uninstaller"
$form.Size = New-Object System.Drawing.Size(500, 400)
$form.StartPosition = "CenterScreen"
$form.FormBorderStyle = "FixedDialog"
$form.MaximizeBox = $false
$form.BackColor = [System.Drawing.Color]::White

# Set form icon if available
if (Test-Path "$InstallDir\logo.ico") {
    try {
        $form.Icon = [System.Drawing.Icon]::ExtractAssociatedIcon("$InstallDir\logo.ico")
    } catch {}
}

# Header Panel
$headerPanel = New-Object System.Windows.Forms.Panel
$headerPanel.Size = New-Object System.Drawing.Size(500, 80)
$headerPanel.Location = New-Object System.Drawing.Point(0, 0)
$headerPanel.BackColor = [System.Drawing.Color]::FromArgb(231, 76, 60)
$form.Controls.Add($headerPanel)

# Title Label
$titleLabel = New-Object System.Windows.Forms.Label
$titleLabel.Text = "Uninstall IMS"
$titleLabel.Font = New-Object System.Drawing.Font("Segoe UI", 16, [System.Drawing.FontStyle]::Bold)
$titleLabel.ForeColor = [System.Drawing.Color]::White
$titleLabel.AutoSize = $true
$titleLabel.Location = New-Object System.Drawing.Point(20, 15)
$headerPanel.Controls.Add($titleLabel)

$subtitleLabel = New-Object System.Windows.Forms.Label
$subtitleLabel.Text = "Remove IMS from your computer"
$subtitleLabel.Font = New-Object System.Drawing.Font("Segoe UI", 10)
$subtitleLabel.ForeColor = [System.Drawing.Color]::White
$subtitleLabel.AutoSize = $true
$subtitleLabel.Location = New-Object System.Drawing.Point(20, 45)
$headerPanel.Controls.Add($subtitleLabel)

# Info Label
$infoLabel = New-Object System.Windows.Forms.Label
$infoLabel.Text = "The following will be removed:"
$infoLabel.Font = New-Object System.Drawing.Font("Segoe UI", 10, [System.Drawing.FontStyle]::Bold)
$infoLabel.AutoSize = $true
$infoLabel.Location = New-Object System.Drawing.Point(30, 100)
$form.Controls.Add($infoLabel)

# Details TextBox
$detailsBox = New-Object System.Windows.Forms.TextBox
$detailsBox.Multiline = $true
$detailsBox.ScrollBars = "Vertical"
$detailsBox.Size = New-Object System.Drawing.Size(440, 150)
$detailsBox.Location = New-Object System.Drawing.Point(30, 130)
$detailsBox.Font = New-Object System.Drawing.Font("Consolas", 9)
$detailsBox.ReadOnly = $true
$detailsBox.BackColor = [System.Drawing.Color]::FromArgb(250, 250, 250)
$form.Controls.Add($detailsBox)

# Populate details
if (![string]::IsNullOrWhiteSpace($InstallDir) -and (Test-Path $InstallDir)) {
    $detailsBox.AppendText("Installation Directory:`r`n")
    $detailsBox.AppendText("  $InstallDir`r`n`r`n")
    $detailsBox.AppendText("Files to be removed:`r`n")
    $detailsBox.AppendText("  • IMS.jar`r`n")
    $detailsBox.AppendText("  • Run-IMS.bat`r`n")
    $detailsBox.AppendText("  • logo.ico`r`n")
    $detailsBox.AppendText("  • Uninstall.bat`r`n")
    $detailsBox.AppendText("  • data\ folder (including database)`r`n`r`n")
    $detailsBox.AppendText("Shortcuts:`r`n")
    $detailsBox.AppendText("  • Desktop shortcut (if exists)`r`n")
    $detailsBox.AppendText("  • Start Menu shortcut (if exists)`r`n")
} else {
    $detailsBox.AppendText("ERROR: IMS installation not found!`r`n`r`n")
    $detailsBox.AppendText("Searched locations:`r`n")
    $detailsBox.AppendText("  • C:\Program Files\IMS`r`n")
    $detailsBox.AppendText("  • $env:LOCALAPPDATA\IMS`r`n")
    $detailsBox.AppendText("  • $env:USERPROFILE\IMS`r`n")
}

# Warning Label
$warningLabel = New-Object System.Windows.Forms.Label
$warningLabel.Text = "⚠ Warning: This will permanently delete all IMS files and data!"
$warningLabel.Font = New-Object System.Drawing.Font("Segoe UI", 9, [System.Drawing.FontStyle]::Bold)
$warningLabel.ForeColor = [System.Drawing.Color]::FromArgb(231, 76, 60)
$warningLabel.AutoSize = $true
$warningLabel.Location = New-Object System.Drawing.Point(30, 295)
$form.Controls.Add($warningLabel)

# Uninstall Button
$uninstallButton = New-Object System.Windows.Forms.Button
$uninstallButton.Text = "Uninstall"
$uninstallButton.Size = New-Object System.Drawing.Size(120, 35)
$uninstallButton.Location = New-Object System.Drawing.Point(240, 320)
$uninstallButton.BackColor = [System.Drawing.Color]::FromArgb(231, 76, 60)
$uninstallButton.ForeColor = [System.Drawing.Color]::White
$uninstallButton.FlatStyle = "Flat"
$uninstallButton.Font = New-Object System.Drawing.Font("Segoe UI", 10, [System.Drawing.FontStyle]::Bold)
$form.Controls.Add($uninstallButton)

# Cancel Button
$cancelButton = New-Object System.Windows.Forms.Button
$cancelButton.Text = "Cancel"
$cancelButton.Size = New-Object System.Drawing.Size(100, 35)
$cancelButton.Location = New-Object System.Drawing.Point(370, 320)
$cancelButton.FlatStyle = "Flat"
$cancelButton.Font = New-Object System.Drawing.Font("Segoe UI", 10)
$form.Controls.Add($cancelButton)

# Disable uninstall if no installation found
if ([string]::IsNullOrWhiteSpace($InstallDir) -or !(Test-Path $InstallDir)) {
    $uninstallButton.Enabled = $false
    $uninstallButton.BackColor = [System.Drawing.Color]::Gray
}

# Uninstall action
$uninstallButton.Add_Click({
    $result = [System.Windows.Forms.MessageBox]::Show(
        "Are you sure you want to uninstall IMS?`n`nThis will delete all application files and data.`n`nThis action cannot be undone!",
        "Confirm Uninstall",
        [System.Windows.Forms.MessageBoxButtons]::YesNo,
        [System.Windows.Forms.MessageBoxIcon]::Warning
    )
    
    if ($result -eq [System.Windows.Forms.DialogResult]::Yes) {
        $uninstallButton.Enabled = $false
        $cancelButton.Enabled = $false
        
        $detailsBox.Clear()
        $detailsBox.AppendText("Starting uninstallation...`r`n`r`n")
        
        try {
            # Remove desktop shortcut
            $desktopShortcut = "$env:USERPROFILE\Desktop\IMS.lnk"
            if (Test-Path $desktopShortcut) {
                Remove-Item $desktopShortcut -Force
                $detailsBox.AppendText("[OK] Desktop shortcut removed`r`n")
            }
            
            # Remove Start Menu shortcut
            $startMenuShortcut = "$env:APPDATA\Microsoft\Windows\Start Menu\Programs\IMS.lnk"
            if (Test-Path $startMenuShortcut) {
                Remove-Item $startMenuShortcut -Force
                $detailsBox.AppendText("[OK] Start Menu shortcut removed`r`n")
            }
            
            # Remove installation directory
            if (Test-Path $InstallDir) {
                $detailsBox.AppendText("[INFO] Removing installation directory...`r`n")
                Remove-Item -Path $InstallDir -Recurse -Force
                $detailsBox.AppendText("[OK] Installation directory removed`r`n")
            }
            
            # Remove registry entry
            try {
                $uninstallKey = "HKLM:\SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\IMS"
                if (Test-Path $uninstallKey) {
                    Remove-Item -Path $uninstallKey -Recurse -Force
                    $detailsBox.AppendText("[OK] Registry entry removed`r`n")
                }
            } catch {
                $detailsBox.AppendText("[WARNING] Could not remove registry entry`r`n")
            }
            
            $detailsBox.AppendText("`r`n========================================`r`n")
            $detailsBox.AppendText("Uninstallation completed successfully!`r`n")
            $detailsBox.AppendText("========================================`r`n")
            
            [System.Windows.Forms.MessageBox]::Show(
                "IMS has been successfully uninstalled from your computer.",
                "Uninstallation Complete",
                [System.Windows.Forms.MessageBoxButtons]::OK,
                [System.Windows.Forms.MessageBoxIcon]::Information
            )
            
            $form.Close()
            
        } catch {
            $detailsBox.AppendText("`r`n[ERROR] Uninstallation failed: $_`r`n")
            [System.Windows.Forms.MessageBox]::Show(
                "Failed to uninstall IMS.`n`nError: $_`n`nYou may need to manually delete the installation folder.",
                "Uninstallation Error",
                [System.Windows.Forms.MessageBoxButtons]::OK,
                [System.Windows.Forms.MessageBoxIcon]::Error
            )
            $cancelButton.Enabled = $true
        }
    }
})

# Cancel button action
$cancelButton.Add_Click({
    $form.Close()
})

# Show form
[void]$form.ShowDialog()
