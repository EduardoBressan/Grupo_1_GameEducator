# Desafio 3 (ADR-003) - mini-lab: derrubar o servico Python e observar o Core.
#
# Rodar na raiz do repositorio, com o Docker Desktop aberto:
#   powershell -ExecutionPolicy Bypass -File docs/arquitetura/evidencias/desafio3/roteiro-falha.ps1
#
# O resultado fica em docs/arquitetura/evidencias/desafio3/resultado-roteiro.txt

$ErrorActionPreference = "Stop"
$api = "http://localhost:8080/api"
$saida = "docs/arquitetura/evidencias/desafio3/resultado-roteiro.txt"

function Registrar([string] $texto) {
    Write-Host $texto
    Add-Content -Path $saida -Value $texto -Encoding utf8
}

function Chamar([string] $metodo, [string] $url, $corpo = $null) {
    $cronometro = [Diagnostics.Stopwatch]::StartNew()
    if ($null -eq $corpo) {
        $resposta = Invoke-RestMethod -Method $metodo -Uri $url
    } else {
        $resposta = Invoke-RestMethod -Method $metodo -Uri $url -ContentType "application/json" `
            -Body ($corpo | ConvertTo-Json -Compress)
    }
    $cronometro.Stop()
    return @{ resposta = $resposta; ms = $cronometro.ElapsedMilliseconds }
}

function EsperarCore {
    for ($i = 0; $i -lt 60; $i++) {
        try { Invoke-RestMethod -Uri "$api/cursos" | Out-Null; return } catch { Start-Sleep -Seconds 2 }
    }
    throw "O Core nao subiu em 2 minutos."
}

function RegistrarRecomendacao([string] $cenario, [long] $alunoId) {
    $r = Chamar "GET" "$api/alunos/$alunoId/recomendacoes"
    Registrar ("[{0}] GET /recomendacoes -> origem={1}, cursos={2}, tempo={3} ms" -f `
        $cenario, $r.resposta.origem, @($r.resposta.cursos).Count, $r.ms)
}

Set-Content -Path $saida -Value ("Desafio 3 - roteiro de falha da IA ({0})" -f (Get-Date -Format s)) -Encoding utf8

Registrar "`n== Passo 1: subir Core + PostgreSQL + IA"
docker compose up -d --build
EsperarCore
Registrar (docker compose ps --format "{{.Service}}: {{.State}}" | Out-String)

$sufixo = Get-Random
$aluno = (Chamar "POST" "$api/alunos" @{ nome = "Aluno Lab Tres"; email = "lab3.$sufixo@teste.com" }).resposta
$curso = (Chamar "POST" "$api/cursos" @{ titulo = "Logica Lab $sufixo"; descricao = "Curso base"; area = "Programacao" }).resposta
Registrar ("Aluno criado: id={0}; curso criado: id={1}" -f $aluno.id, $curso.id)

Registrar "`n== Passo 2: IA no ar"
RegistrarRecomendacao "IA no ar" $aluno.id

Registrar "`n== Passo 3: derrubar a IA (docker compose stop ia)"
docker compose stop ia
RegistrarRecomendacao "IA fora do ar" $aluno.id

$matricula = Chamar "POST" "$api/matriculas" @{ alunoId = $aluno.id; cursoId = $curso.id; cursoAdicional = $false }
Registrar ("[IA fora do ar] POST /matriculas -> status={0}, tempo={1} ms" -f $matricula.resposta.status, $matricula.ms)
$conclusao = Chamar "PUT" ("$api/matriculas/{0}/concluir" -f $matricula.resposta.id) @{ mediaFinal = 9.0 }
Registrar ("[IA fora do ar] PUT /concluir -> status={0}, cursosAdicionaisDisponiveis={1}, tempo={2} ms" -f `
    $conclusao.resposta.status, $conclusao.resposta.cursosAdicionaisDisponiveis, $conclusao.ms)

Registrar "`n== Passo 4: religar a IA (docker compose start ia)"
docker compose start ia
Start-Sleep -Seconds 3
RegistrarRecomendacao "IA religada" $aluno.id

Registrar "`n== Logs do Core sobre a IA"
Registrar ((docker compose logs app | Select-String "Servico de IA indisponivel" | Select-Object -Last 3) -join "`n")

Registrar "`nFim. Resultado salvo em $saida"
