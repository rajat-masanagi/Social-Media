$ErrorActionPreference = 'Stop'
Write-Host 'Installing all reactor modules so standalone service runs can resolve event-contracts...'
mvn install -DskipTests
Write-Host 'Backend modules installed successfully.'

