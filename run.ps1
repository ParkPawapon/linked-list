[CmdletBinding()]
param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$AppArgs = @()
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$RootDir = Split-Path -Path $MyInvocation.MyCommand.Path -Parent
$SolutionPath = Join-Path $RootDir "LinkedList.Polynomial.sln"
$AppProject = Join-Path $RootDir "src/LinkedList.Polynomial.App/LinkedList.Polynomial.App.csproj"
$Configuration = "Release"

function Write-Step {
    param([string]$Message)
    Write-Host "[run.ps1] $Message"
}

if (-not (Get-Command dotnet -ErrorAction SilentlyContinue)) {
    throw "[run.ps1] dotnet is not installed or not in PATH."
}

$dotnetVersion = (dotnet --version).Trim()
if (-not $dotnetVersion.StartsWith("9.0.")) {
    throw "[run.ps1] .NET SDK 9.0.x is required. Current: $dotnetVersion"
}

if (-not (Test-Path -Path $SolutionPath -PathType Leaf)) {
    throw "[run.ps1] Solution file not found: $SolutionPath"
}

Write-Step "dotnet restore"
& dotnet restore $SolutionPath

Write-Step "dotnet build ($Configuration)"
& dotnet build $SolutionPath --configuration $Configuration --no-restore

Write-Step "dotnet test ($Configuration)"
& dotnet test $SolutionPath --configuration $Configuration --no-build

Write-Step "dotnet run ($Configuration)"
$runArgs = @(
    "run",
    "--project", $AppProject,
    "--configuration", $Configuration,
    "--no-build",
    "--"
)
$runArgs += $AppArgs
& dotnet @runArgs

Write-Step "Completed successfully."
