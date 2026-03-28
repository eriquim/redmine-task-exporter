$excel = New-Object -ComObject Excel.Application
$excel.Visible = $false
$excel.DisplayAlerts = $false
$wb = $excel.Workbooks.Open("C:\ambiente\ambiente_tjce\spaces\workspace_legacy\github\redmine-task-exporter\xlx\Base_Calculo_CS2 1.xlsx")
$sheet = $wb.Sheets.Item(1)
$cols = ""
for ($i=1; $i -le 100; $i++) {
    $val = $sheet.Cells.Item(1, $i).Text
    if ([string]::IsNullOrWhiteSpace($val)) { break }
    $cols += $val + ";"
}
$wb.Close($false)
$excel.Quit()
[System.Runtime.Interopservices.Marshal]::ReleaseComObject($excel) | Out-Null
Write-Host "COLUMNS: $cols"
