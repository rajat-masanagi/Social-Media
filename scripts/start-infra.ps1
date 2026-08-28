$ErrorActionPreference = 'Stop'
docker compose up -d
docker compose ps
Write-Host 'Infrastructure is starting. Wait until mysql, kafka, cassandra, and elasticsearch are healthy.'

