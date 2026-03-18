[CmdletBinding()]
param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$AppArgs = @()
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$RootDir = Split-Path -Path $MyInvocation.MyCommand.Path -Parent
$MainSourceDir = Join-Path $RootDir "src/main/java"
$TestSourceDir = Join-Path $RootDir "src/test/java"
$BuildDir = Join-Path $RootDir "build"
$MainClassesDir = Join-Path $BuildDir "classes/main"
$TestClassesDir = Join-Path $BuildDir "classes/test"
$MainClass = "io.github.parkpawapon.linkedlist.app.Main"
$TestClass = "io.github.parkpawapon.linkedlist.test.PolynomialTests"
$JavacFlags = @("--release", "17", "-encoding", "UTF-8", "-Xlint:all", "-Werror")

function Write-Step {
    param([string]$Message)
    Write-Host "[run.ps1] $Message"
}

function Get-JavaSources {
    param([string]$Path)

    if (-not (Test-Path -Path $Path -PathType Container)) {
        return @()
    }

    return Get-ChildItem -Path $Path -Recurse -Filter *.java -File |
        Sort-Object FullName |
        ForEach-Object { $_.FullName }
}

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    throw "[run.ps1] java is not installed or not in PATH."
}

if (-not (Get-Command javac -ErrorAction SilentlyContinue)) {
    throw "[run.ps1] javac is not installed or not in PATH."
}

$javaVersion = (& javac -version 2>&1 | Out-String).Trim()
if (-not $javaVersion.StartsWith("javac 17")) {
    throw "[run.ps1] JDK 17 is required. Current: $javaVersion"
}

if (-not (Test-Path -Path $MainSourceDir -PathType Container)) {
    throw "[run.ps1] Main source directory not found: $MainSourceDir"
}

if (-not (Test-Path -Path $TestSourceDir -PathType Container)) {
    throw "[run.ps1] Test source directory not found: $TestSourceDir"
}

if (Test-Path -Path $BuildDir) {
    Remove-Item -Path $BuildDir -Recurse -Force
}

New-Item -Path $MainClassesDir -ItemType Directory -Force | Out-Null
New-Item -Path $TestClassesDir -ItemType Directory -Force | Out-Null

$mainSources = @(Get-JavaSources -Path $MainSourceDir)
if ($mainSources.Count -eq 0) {
    throw "[run.ps1] No main Java sources found."
}

$testSources = @(Get-JavaSources -Path $TestSourceDir)
if ($testSources.Count -eq 0) {
    throw "[run.ps1] No test Java sources found."
}

Write-Step "Compiling main sources"
& javac @JavacFlags -d $MainClassesDir @mainSources

Write-Step "Compiling test sources"
& javac @JavacFlags -cp $MainClassesDir -d $TestClassesDir @testSources

Write-Step "Running tests"
& java -cp "$MainClassesDir;$TestClassesDir" $TestClass

Write-Step "Running console demo"
& java -cp $MainClassesDir $MainClass @AppArgs

Write-Step "Completed successfully."
