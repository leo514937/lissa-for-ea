param(
    [int]$MaxRetries = 3,
    [int]$BaseBackoffSeconds = 60,
    [int]$BetweenConfigsSeconds = 30
)

$ErrorActionPreference = 'Stop'

$root = Resolve-Path -Path (Join-Path $PSScriptRoot '..\..\..')
$jar = Join-Path $root 'target\lissa-0.2.0-SNAPSHOT-jar-with-dependencies.jar'

$configs = @(
    (Join-Path $PSScriptRoot 'configs\simple-config.json'),
    (Join-Path $PSScriptRoot 'configs\simple-config-scheme2-voting.json'),
    (Join-Path $PSScriptRoot 'configs\simple-config-scheme2-layered.json')
)

if (-not (Test-Path $jar)) {
    Write-Host "Jar not found. Building with mvn -q -DskipTests package..."
    Push-Location $root
    try {
        mvn -q -DskipTests "-Dmaven.javadoc.skip=true" package
    } finally {
        Pop-Location
    }
}

foreach ($config in $configs) {
    if (-not (Test-Path $config)) {
        throw "Config not found: $config"
    }

    $attempt = 1
    while ($true) {
        Write-Host "Running config: $config (attempt $attempt/$MaxRetries)"
        Push-Location $root
        try {
            & java -jar $jar eval -c $config
            break
        } catch {
            if ($attempt -ge $MaxRetries) {
                throw
            }
            $sleep = $BaseBackoffSeconds * [math]::Pow(2, ($attempt - 1))
            Write-Host "Run failed. Sleeping $sleep seconds to reduce rate-limit pressure..."
            Start-Sleep -Seconds $sleep
            $attempt++
        } finally {
            Pop-Location
        }
    }

    Write-Host "Finished $config. Cooling down for $BetweenConfigsSeconds seconds..."
    Start-Sleep -Seconds $BetweenConfigsSeconds
}

Write-Host "All configs finished."
