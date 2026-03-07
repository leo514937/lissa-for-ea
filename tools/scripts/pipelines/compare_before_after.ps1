param(
    [Parameter(Mandatory = $true)]
    [string]$BeforeConfig,

    [Parameter(Mandatory = $true)]
    [string]$AfterConfig,

    [string]$JarPath = "",

    [string]$OutputReport = ""
)

$ErrorActionPreference = "Stop"

function Resolve-ConfigPath([string]$path) {
    if (-not (Test-Path $path)) {
        throw "Config not found: $path"
    }
    return (Resolve-Path $path).Path
}

function Resolve-JarPath([string]$jarPath) {
    if ($jarPath -and (Test-Path $jarPath)) {
        return (Resolve-Path $jarPath).Path
    }

    $candidates = Get-ChildItem -Path "target" -Filter "*jar-with-dependencies.jar" -File -ErrorAction SilentlyContinue |
        Sort-Object LastWriteTime -Descending
    if ($candidates.Count -eq 0) {
        throw "No runnable jar found. Please run 'mvn package' first or pass -JarPath."
    }
    return $candidates[0].FullName
}

function Get-ResultFiles() {
    if (-not (Test-Path "evaluations")) { return @() }
    $items = Get-ChildItem -Path "evaluations" -Filter "results*.md" -Recurse -File -ErrorAction SilentlyContinue
    if ($null -eq $items) {
        return @()
    }
    return @($items | Select-Object -ExpandProperty FullName)
}

function Parse-Metric([string]$content, [string]$name) {
    $pattern = "(?m)^\*\s+$([Regex]::Escape($name)):\s+(.+)\s*$"
    $match = [Regex]::Match($content, $pattern)
    if (-not $match.Success) {
        return $null
    }
    return $match.Groups[1].Value.Trim()
}

function Run-One([string]$label, [string]$jar, [string]$configPath) {
    Write-Host "=== Running $label ==="
    Write-Host "Config: $configPath"

    $beforeFiles = Get-ResultFiles
    $start = Get-Date

    & java -jar $jar eval -c $configPath
    if ($LASTEXITCODE -ne 0) {
        throw "Evaluation failed for $label with exit code $LASTEXITCODE"
    }

    $end = Get-Date
    $afterFiles = Get-ResultFiles
    $newFiles = Compare-Object -ReferenceObject $beforeFiles -DifferenceObject $afterFiles -PassThru |
        Where-Object { $_ -in $afterFiles }

    if (-not $newFiles -or $newFiles.Count -eq 0) {
        if (-not (Test-Path "evaluations")) { throw "No evaluations folder found." }
        $resultFile = Get-ChildItem -Path "evaluations" -Filter "results*.md" -Recurse -File -ErrorAction SilentlyContinue |
            Where-Object { $_.LastWriteTime -ge $start.AddSeconds(-1) } |
            Sort-Object LastWriteTime -Descending |
            Select-Object -First 1
        if ($null -eq $resultFile) {
            throw "No new or updated result markdown file detected for $label."
        }
    } else {
        $resultFile = Get-Item $newFiles | Sort-Object LastWriteTime -Descending | Select-Object -First 1
    }
    $content = Get-Content -Raw $resultFile.FullName

    return [PSCustomObject]@{
        Label = $label
        Config = $configPath
        ResultFile = $resultFile.FullName
        DurationSec = [Math]::Round(($end - $start).TotalSeconds, 2)
        TraceLinksGS = Parse-Metric $content "#TraceLinks (GS)"
        SourceArtifacts = Parse-Metric $content "#Source Artifacts"
        TargetArtifacts = Parse-Metric $content "#Target Artifacts"
        TruePositives = Parse-Metric $content "True Positives"
        FalsePositives = Parse-Metric $content "False Positives"
        FalseNegatives = Parse-Metric $content "False Negatives"
        Precision = Parse-Metric $content "Precision"
        Recall = Parse-Metric $content "Recall"
        F1 = Parse-Metric $content "F1"
    }
}

function To-DoubleOrNaN([string]$value) {
    if (-not $value) {
        return [double]::NaN
    }
    $parsed = 0.0
    if ([double]::TryParse($value, [System.Globalization.NumberStyles]::Float, [System.Globalization.CultureInfo]::InvariantCulture, [ref]$parsed)) {
        return $parsed
    }
    return [double]::NaN
}

$beforeConfigPath = Resolve-ConfigPath $BeforeConfig
$afterConfigPath = Resolve-ConfigPath $AfterConfig
$resolvedJar = Resolve-JarPath $JarPath

if (-not $OutputReport) {
    if (-not (Test-Path "evaluations")) {
        New-Item -ItemType Directory -Path "evaluations" | Out-Null
    }
    $OutputReport = Join-Path (Get-Location) ("evaluations\comparison-{0:yyyyMMdd-HHmmss}.md" -f (Get-Date))
}

$before = Run-One "Before" $resolvedJar $beforeConfigPath
$after = Run-One "After" $resolvedJar $afterConfigPath

$f1Before = To-DoubleOrNaN $before.F1
$f1After = To-DoubleOrNaN $after.F1
$precisionBefore = To-DoubleOrNaN $before.Precision
$precisionAfter = To-DoubleOrNaN $after.Precision
$recallBefore = To-DoubleOrNaN $before.Recall
$recallAfter = To-DoubleOrNaN $after.Recall

$deltaF1 = if ([double]::IsNaN($f1Before) -or [double]::IsNaN($f1After)) { "N/A" } else { "{0}" -f ($f1After - $f1Before) }
$deltaPrecision = if ([double]::IsNaN($precisionBefore) -or [double]::IsNaN($precisionAfter)) { "N/A" } else { "{0}" -f ($precisionAfter - $precisionBefore) }
$deltaRecall = if ([double]::IsNaN($recallBefore) -or [double]::IsNaN($recallAfter)) { "N/A" } else { "{0}" -f ($recallAfter - $recallBefore) }

$reportLines = @(
    "# Before/After Comparison",
    "",
    "- Jar: $resolvedJar",
    "- Before config: $beforeConfigPath",
    "- After config: $afterConfigPath",
    "",
    "| Metric | Before | After | Delta (After-Before) |",
    "|---|---:|---:|---:|",
    "| Precision | $($before.Precision) | $($after.Precision) | $deltaPrecision |",
    "| Recall | $($before.Recall) | $($after.Recall) | $deltaRecall |",
    "| F1 | $($before.F1) | $($after.F1) | $deltaF1 |",
    "| True Positives | $($before.TruePositives) | $($after.TruePositives) | N/A |",
    "| False Positives | $($before.FalsePositives) | $($after.FalsePositives) | N/A |",
    "| False Negatives | $($before.FalseNegatives) | $($after.FalseNegatives) | N/A |",
    "| #TraceLinks (GS) | $($before.TraceLinksGS) | $($after.TraceLinksGS) | N/A |",
    "| #Source Artifacts | $($before.SourceArtifacts) | $($after.SourceArtifacts) | N/A |",
    "| #Target Artifacts | $($before.TargetArtifacts) | $($after.TargetArtifacts) | N/A |",
    "| Duration (s) | $($before.DurationSec) | $($after.DurationSec) | N/A |",
    "",
    "## Raw Result Files",
    "",
    "- Before: $($before.ResultFile)",
    "- After: $($after.ResultFile)"
)

Set-Content -Path $OutputReport -Value ($reportLines -join "`n") -Encoding UTF8

Write-Host ""
Write-Host "Comparison complete."
Write-Host "Report: $OutputReport"



