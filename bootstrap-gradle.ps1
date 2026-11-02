# Bootstrap Gradle 8.13, then run it. Android Studio's bundled JDK 17 is preferred.
param([Parameter(ValueFromRemainingArguments=$true)][string[]]$GradleArgs)
$ErrorActionPreference='Stop'
$version='8.13'
$root=Join-Path $env:USERPROFILE '.gradle\findback-bootstrap'
$install=Join-Path $root "gradle-$version"
if(-not $env:JAVA_HOME){
  $candidates=@("$env:ProgramFiles\Android\Android Studio\jbr", "$env:ProgramFiles\Android Studio\jbr", "$env:LOCALAPPDATA\Programs\Android Studio\jbr")
  foreach($candidate in $candidates){if(Test-Path (Join-Path $candidate 'bin\java.exe')){$env:JAVA_HOME=$candidate;break}}
}
if(-not $env:JAVA_HOME -or -not (Test-Path (Join-Path $env:JAVA_HOME 'bin\java.exe'))){throw 'Install Android Studio (JDK 17) or set JAVA_HOME to a JDK 17 folder.'}
if(-not (Test-Path (Join-Path $install 'bin\gradle.bat'))){
  New-Item -ItemType Directory -Path $root -Force | Out-Null
  $zip=Join-Path $root "gradle-$version-bin.zip"
  Invoke-WebRequest "https://services.gradle.org/distributions/gradle-$version-bin.zip" -OutFile $zip
  Expand-Archive -LiteralPath $zip -DestinationPath $root -Force
}
& (Join-Path $install 'bin\gradle.bat') @GradleArgs
exit $LASTEXITCODE
