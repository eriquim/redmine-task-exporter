# Caminho da pasta/projeto
$repoPath = "C:\ambiente\ambiente_tjce\spaces\workspace_fluxos\git\PJE"
Set-Location $repoPath

# Pasta ou arquivo alvo no repositório
$targetPath = "Scripts/2025/"

# Data limite
$sinceDate = "2025-08-08"

# Arquivo de saída
$outputFile = "commits_relatorio.csv"

# Lista de arquivos únicos alterados após a data
$files = git log --since=$sinceDate --name-only --pretty=format: -- $targetPath | Select-Object -Unique

# Array para armazenar resultados
$resultados = @()

foreach ($file in $files) {
    # Último commit do arquivo
    $log = git log -1 --pretty=format:"%h;%an;%ad;%s" -- $file
    
    if ($log) {
        $parts = $log -split ";",4
        $obj = [PSCustomObject]@{
            Arquivo = $file
            Commit  = $parts[0]
            Autor   = $parts[1]
            Data    = $parts[2]
            Mensagem= $parts[3]
        }
        $resultados += $obj
    }
}

# Exporta para CSV
$resultados | Export-Csv -Path $outputFile -NoTypeInformation -Encoding UTF8

Write-Host "Relatório gerado em $outputFile"
