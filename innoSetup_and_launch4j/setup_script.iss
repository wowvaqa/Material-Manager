; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

#define MyAppName "Material Manager"
#define MyAppVersion "0.042"
#define MyAppPublisher "?ukasz Wawrzyniak"
#define MyAppURL "none"
#define MyAppExeName "MaterialManager-0.042.exe"

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{4320D116-3610-47DB-B71D-0FB2FAC5F076}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={pf}\{#MyAppName}
DisableProgramGroupPage=yes
OutputBaseFilename=mmSetup
Compression=lzma
SolidCompression=yes

[Languages]
Name: "polish"; MessagesFile: "compiler:Languages\Polish.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "C:\Users\wakan\Desktop\mm v.0.042\MaterialManager-0.042.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\wakan\Desktop\mm v.0.042\Changes.txt"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\wakan\Desktop\mm v.0.042\data\*"; DestDir: "{app}\data"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "C:\Users\wakan\Desktop\mm v.0.042\lib\*"; DestDir: "{app}\lib"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "C:\Users\wakan\Desktop\mm v.0.042\pkd\*"; DestDir: "{app}\pkd"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "C:\Users\wakan\Desktop\mm v.0.042\raporty\*"; DestDir: "{app}\raporty"; Flags: ignoreversion recursesubdirs createallsubdirs
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{commonprograms}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
Name: "{commondesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: nowait postinstall skipifsilent

