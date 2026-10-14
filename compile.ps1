# compile.ps1 - compile this tree and report the UNIQUE javac error count.
# Gradle prints every diagnostic twice, so the raw 'error:' count is double the truth.
param([string]$LogName = "ponder")
$ErrorActionPreference = 'Continue'
$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$logDir = Join-Path $root 'build'
if (-not (Test-Path $logDir)) { New-Item -ItemType Directory -Path $logDir | Out-Null }
$log = Join-Path $logDir "compile-$LogName.log"
$previousJavaToolOptions = $env:JAVA_TOOL_OPTIONS
$env:JAVA_TOOL_OPTIONS = "-Djdk.net.unixdomain.tmpdir=$env:TEMP\gradle-unix-fallback-$([guid]::NewGuid())"
try {
    & (Join-Path $root 'gradlew.bat') --no-daemon -I ..\gradle-mirror.init.gradle compileJava compileClientJava *> $log
    $exit = $LASTEXITCODE
} finally {
    $env:JAVA_TOOL_OPTIONS = $previousJavaToolOptions
}
$errors = Get-Content $log | Select-String -Pattern 'error:' | ForEach-Object { $_.Line.Trim() } | Sort-Object -Unique
Write-Output "exit=$exit  unique errors=$($errors.Count)  log=$log"
if ($errors.Count -gt 0) {
    Write-Output '--- top files ---'
    $errors | ForEach-Object { ($_ -split '\.java:')[0] } | Group-Object | Sort-Object Count -Descending |
        Select-Object -First 25 | ForEach-Object { "{0,4}  {1}" -f $_.Count, $_.Name }
    Write-Output '--- first 40 unique messages ---'
    $errors | Select-Object -First 40 | ForEach-Object { $_ }
}
