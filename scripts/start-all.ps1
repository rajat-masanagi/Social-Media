param([switch]$Rebuild)
$ErrorActionPreference = 'Stop'
Set-Location (Join-Path $PSScriptRoot '..')
if ($Rebuild) { docker compose up --build -d } else { docker compose up -d }
docker compose ps
