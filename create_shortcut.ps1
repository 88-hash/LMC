$TargetFile = "$PSScriptRoot\start_system.bat"
$ShortcutFile = "$Home\Desktop\LeYi_Start.lnk"
$WScriptShell = New-Object -ComObject WScript.Shell
$Shortcut = $WScriptShell.CreateShortcut($ShortcutFile)
$Shortcut.TargetPath = $TargetFile
$Shortcut.WorkingDirectory = $PSScriptRoot
$Shortcut.Save()
Write-Host "Shortcut created at $ShortcutFile"
