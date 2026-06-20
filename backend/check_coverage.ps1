$services = @("auth-service", "inventory-service", "request-service")
foreach ($svc in $services) {
    $csvPath = "C:\Users\yashm\.gemini\antigravity\scratch\stationery-management-system\backend\$svc\target\site\jacoco\jacoco.csv"
    if (Test-Path $csvPath) {
        $lines = Get-Content $csvPath | Select-Object -Skip 1
        $missedInst = 0
        $coveredInst = 0
        foreach ($line in $lines) {
            $cols = $line -split ","
            if ($cols.Length -gt 4) {
                $missedInst += [int]$cols[3]
                $coveredInst += [int]$cols[4]
            }
        }
        $totalInst = $missedInst + $coveredInst
        if ($totalInst -gt 0) {
            $cov = [math]::Round(($coveredInst / $totalInst) * 100, 2)
            Write-Host "$svc Coverage: $cov % ($coveredInst / $totalInst)"
        } else {
            Write-Host "$svc Coverage: No instructions found"
        }
    } else {
        Write-Host "$svc Coverage: Report not found"
    }
}
