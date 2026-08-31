param(
    [int]$Users = 10,
    [int]$DurationSeconds = 120,
    [string]$BaseUrl = 'http://localhost:8080',
    [string]$RunId = (Get-Date -Format 'yyyyMMdd-HHmmss')
)
$ErrorActionPreference = 'Stop'
if (-not $env:JMETER_HOME) { throw 'Set JMETER_HOME to your Apache JMeter 5.6.3 directory.' }
$jmeter = Join-Path $env:JMETER_HOME 'bin\jmeter.bat'
if (-not (Test-Path -LiteralPath $jmeter)) { throw "JMeter was not found at $jmeter" }
$uri = [Uri]$BaseUrl
$output = Join-Path $PSScriptRoot ("..\load-tests\runs\" + $RunId)
New-Item -ItemType Directory -Force -Path $output | Out-Null
$report = Join-Path $output 'report'
$results = Join-Path $output 'results.jtl'
& $jmeter -n -t (Join-Path $PSScriptRoot '..\load-tests\social-media.jmx') `
    -Jusers=$Users -Jduration=$DurationSeconds -Jprotocol=$($uri.Scheme) `
    -Jhost=$($uri.Host) -Jport=$($uri.Port) -l $results -e -o $report
