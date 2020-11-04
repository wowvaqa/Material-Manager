; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

#define MyAppName "Material Manager"
#define MyAppVersion "0.0.6.5"
#define MyAppPublisher "�ukasz Wawrzyniak"
#define MyAppURL "none"
#define MyAppExeName "MaterialManager-0.042.exe"

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{F6A4F39A-984A-42D4-9DA5-6ED1AFA9BB36}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={pf}\{#MyAppName}
DisableProgramGroupPage=yes
OutputBaseFilename=MMv0.0.6.5setup
Compression=lzma
SolidCompression=yes

[Languages]
Name: "polish"; MessagesFile: "compiler:Languages\Polish.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "C:\Users\wakan\Dropbox\java\MaterialManager\exe\MaterialManager-0.042.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\wakan\Dropbox\java\MaterialManager\exe\data\*"; DestDir: "{app}\data\"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "C:\Users\wakan\Dropbox\java\MaterialManager\exe\lib\*"; DestDir: "{app}\lib\"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "C:\Users\wakan\Dropbox\java\MaterialManager\exe\pkd\*"; DestDir: "{app}\pkd\"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "C:\Users\wakan\Dropbox\java\MaterialManager\exe\raporty\*"; DestDir: "{app}\raporty\"; Flags: ignoreversion recursesubdirs createallsubdirs
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{commonprograms}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
Name: "{commondesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: nowait postinstall skipifsilent

