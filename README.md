# GameEducator — Educação Continuada Gamificada

Projeto da AC1 de DevOps e QA. Aplicação Spring Boot para gestão de alunos, cursos e matrículas, com a regra de negócio de **cursos adicionais por desempenho** construída via TDD (RED/GREEN/BLUE) no pacote `domain`.

## Grupo 1

| Integrante | E-mail / GitHub |
|---|---|
| Luiza Bottesi | luizabottesi3@gmail.com |
| Eduardo Bressan Paixão | — |
| João | — |
| Felipe Rondello A. Lopes | — |

## Descrição do case

**Educação Continuada Gamificada**: uma plataforma de ensino que recompensa o desempenho do aluno. Ao concluir um curso com média acima de 7,0, o aluno ganha o direito de realizar mais 3 cursos gratuitamente — um mecanismo de gamificação para incentivar a continuidade dos estudos e fidelizar o aluno à plataforma.

## User Stories

> **US escolhida para implementação** (autoria: Felipe Rondello A. Lopes):
>
> **COMO** administrador da plataforma,
> **QUERO** que o aluno, ao terminar um curso com média acima de 7,0, tenha direito à realização de mais 3 cursos,
> **PARA** fidelizar alunos a longo prazo e garantir qualidade de ensino.

<!-- TODO: colar aqui a US de cada integrante, identificada por autor (ver planilha "Grupo1 - US e BDD.xlsx") -->

- **Felipe Rondello A. Lopes:** *(US acima — escolhida pelo grupo)*
- **Luiza Bottesi:** _[colar a US da Luiza aqui]_
- **Eduardo Bressan Paixão:** _[colar a US do Eduardo aqui]_
- **João:** _[colar a US do João aqui]_

O grupo optou por implementar a US do Felipe como núcleo do domínio; os BDDs de cada integrante (abaixo) foram construídos como cenários e casos de borda dessa mesma regra, e todos estão conectados na API real.

## BDD por integrante

Cada cenário abaixo tem um teste correspondente em `src/test/.../domainTest`, identificado por classe.

**Luiza Bottesi** — `PlataformaEnsinoTest`
```
DADO QUE o aluno está cursando um curso na plataforma,
QUANDO ele finalizar o curso com média acima de 7,0,
ENTÃO ele deve ter direito a realizar mais 3 cursos.
```

**Eduardo Bressan Paixão** — `CursosAdicionaisAposUsoTest`
```
DADO QUE o aluno já utilizou os 3 cursos adicionais concedidos anteriormente,
QUANDO ele finalizar um novo curso com média acima de 7,0,
ENTÃO o sistema deve conceder novamente mais 3 cursos adicionais.
```

**João** — `ElegibilidadeDeCursosAdicionaisTest`
```
DADO QUE o aluno finalize o curso com média exatamente 7,0,
QUANDO o sistema avaliar a elegibilidade dele,
ENTÃO ele não deve ficar elegível aos 3 cursos adicionais.
```

**Felipe Rondello A. Lopes** — `ResgateDeCursosRelacionadosTest`
```
DADO QUE o aluno finalize o curso com média maior que 7,0,
QUANDO for resgatar seus 3 cursos,
ENTÃO tenha a escolha de resgatar 3 entre 10 cursos relacionados ao que concluiu.
```

## TDD — RED, GREEN e BLUE

O ciclo foi feito sobre o pacote `domain` (`Aluno`, `Curso`, `Matricula`, `PlataformaEnsino`, `Resgate`, `StatusMatricula` e os Value Objects em `domain/vo`), testado em `domainTest`.

- **RED:** testes escritos antes da implementação, falhando.
- **GREEN:** implementação mínima para os testes passarem.
- **BLUE:** refatoração (extração de Value Objects, remoção de estado indevido em `PlataformaEnsino`, alinhamento com o modelo de domínio da disciplina) mantendo os testes verdes.

A cobertura do JaCoCo é restrita ao pacote `domain/**` (configurado em `pom.xml`), porque a exigência de 100% vale apenas para o exercício de TDD, não para o projeto inteiro.

> **Como preencher:** salve os prints com exatamente esses nomes dentro da pasta `docs/evidencias/tdd/` (crie a pasta se não existir). As imagens abaixo aparecem sozinhas no README assim que os arquivos existirem — não precisa mexer em mais nada.

### RED — testes escritos antes da implementação, falhando

| IntelliJ | Terminal |
|---|---|
| ![RED IntelliJ](docs/evidencias/tdd/red-intellij.png) | ![RED terminal](docs/evidencias/tdd/red-terminal.png) |

### GREEN — implementação mínima, testes passando

| IntelliJ | Terminal |
|---|---|
| ![GREEN IntelliJ](docs/evidencias/tdd/green-intellij.png) | ![GREEN terminal](docs/evidencias/tdd/green-terminal.png) |

### BLUE — refatorado, 100% de cobertura no domínio, sem vermelho/amarelo

| IntelliJ | Terminal | Relatório JaCoCo (`target/site/jacoco/index.html`) |
|---|---|---|
| ![BLUE IntelliJ](docs/evidencias/tdd/blue-intellij.png) | ![BLUE terminal](docs/evidencias/tdd/blue-terminal.png) | ![Cobertura 100%](docs/evidencias/tdd/blue-cobertura.png) |

> As fotos antigas em `docs/evidencias/fotos-luiza/` são da primeira rodada, anterior à extração dos Value Objects — não refletem mais o código atual. Pode apagar essa pasta depois que as novas evidências acima estiverem no lugar.

## Arquitetura

```
domain/          regras de negócio (TDD), também é a camada JPA (@Entity)
domain/vo/       Value Objects (@Embeddable): NomeAluno, EmailAluno, TituloCurso, DescricaoCurso, AreaCurso
repository/      Spring Data JPA
service/         orquestração, chama as regras do domain
dto/             entrada/saída da API
controller/      REST controllers + Swagger
```

## Endpoints principais

| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/alunos` | Lista alunos |
| GET | `/api/alunos/{id}` | Busca aluno por id |
| POST | `/api/alunos` | Cria aluno |
| GET | `/api/cursos` | Lista cursos |
| POST | `/api/cursos` | Cria curso |
| POST | `/api/matriculas` | Matricula aluno em curso |
| PUT | `/api/matriculas/{id}/concluir` | Conclui matrícula com média final |
| GET | `/api/matriculas/{id}/resgate` | Resgata cursos relacionados (cenário do Felipe) |
| GET | `/api/matriculas/aluno/{alunoId}` | Lista matrículas de um aluno |

Documentação interativa: `/swagger-ui/index.html`.

## Como rodar

### Local, com H2 (banco em memória)

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=h2"
```

- Aplicação: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui/index.html
- Console H2: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:gameeducatordb`, usuário `sa`, senha em branco)

### Via Docker, com Postgres + pgAdmin

```powershell
docker compose down -v
docker compose up --build
```

- Aplicação: http://localhost:8080
- pgAdmin: http://localhost:5050 (login `admin@admin.com` / senha `admin`)
- Postgres (dentro da rede do compose): host `postgres`, porta `5432`, banco `gameeducator_db`, usuário/senha `postgres`

> Se a porta 5432 já estiver em uso por um Postgres local, altere o mapeamento de portas do serviço `postgres` no `docker-compose.yml` para `"5433:5432"` — a comunicação interna entre os containers continua em `5432`.

## Testes

```powershell
mvn clean verify
start target\site\jacoco\index.html
```

## Planilha

_[Adicionar link/nome do arquivo "Grupo1 - US e BDD.xlsx" aqui, se enviado junto com a entrega]_
