# Exercício B1 — Architecture Lab

Registro arquitetural cumulativo do GameEducator (Grupo 1). Cada desafio segue a cadeia RF → RNF → ASR + RPC → alternativas → ADR → C4 → evidência.

| Desafio | Decisão | ADR | Status |
|---|---|---|---|
| 1 — Drivers e evolução do Core | Monólito Modular | [ADR-001](#adr-001--evolução-do-core-para-monólito-modular) | Concluído |
| 2 — Organização interna do Core | Ports & Adapters seletivo | [ADR-002](#adr-002--ports--adapters-seletivo-nos-módulos-de-gamificação) | Concluído |
| 3 — Serviço Python e IA | Serviço Python (FastAPI) fora do Core, com degradação | [ADR-003](#adr-003--serviço-python-de-ia-fora-do-core-com-degradação) | Concluído |
| 4 — Comunicação e eventos | — | — | A fazer |
| 5 — IoT e MQTT | — | — | A fazer |
| 6 — CI/CD e observabilidade | — | — | A fazer |

## Matriz cumulativa de ASRs

| ASR | Qualidade | Desafio 1 | Desafio 2 | Desafio 3 | Desafio 4 | Desafio 5 | Desafio 6 |
|---|---|---|---|---|---|---|---|
| ASR01 | Manutenibilidade | Dirige a decisão (módulos com fronteira) | Dirige (fronteira passa a isolar a regra também da infraestrutura) | Não pesa (o módulo `recomendacoes` segue o padrão já definido) | | | |
| ASR02 | Escalabilidade do ranking | Dirige, ainda sem métrica (gatilho de extração) | Não pesa (decisão interna, sem efeito em carga) | Não pesa (ranking fora do escopo) | | | |
| ASR03 | Complexidade operacional | Dirige a decisão (manter 1 deploy e 1 banco) | Não pesa (sem novo deploy nem componente) | Restringe: +1 container só porque 3 ASRs justificam; ML e RAG juntos | | | |
| ASR04 | Testabilidade | — | Dirige (caso de uso testável sem Spring, banco ou mock) | Reforça: caso de uso testado sem HTTP; adapter testado contra servidor falso | | | |
| ASR05 | Modificabilidade | — | Dirige (trocar a tecnologia de dados só no adapter) | Reforça: motor de recomendação atrás de porta; trocar o modelo não muda o Core | | | |
| ASR06 | Tolerância a falhas | — | — | Dirige (timeout + resposta degradada; fluxo essencial sem IA) | | | |
| ASR07 | Deploy independente | — | — | Dirige (IA em container próprio) | | | |
| ASR08 | Escalabilidade da IA | — | — | Dirige, ainda sem métrica (medir no Desafio 6) | | | |

## Desafio 1 — Drivers e evolução do Core

*Decidir a arquitetura macro antes de decompor o sistema.*

**Cenário / problema:** o MVP cresceu de 1.000 para um cenário de até 100.000 usuários. O ranking cresce mais que outras funcionalidades, enquanto cursos, conquistas e recompensas continuam evoluindo. A equipe precisa decidir se mantém o Monólito em Camadas, evolui para Monólito Modular ou separa capacidades em serviços.

### 1. RF — o que o sistema precisa fazer

| ID | Requisito | Descrição |
|---|---|---|
| RF01 | Ranking | Classificar e exibir os alunos por desempenho. |
| RF02 | Conquistas | Ao concluir um curso com média acima de 7,0, o aluno ganha direito a +3 cursos adicionais e pode resgatá-los entre até 10 cursos da mesma área. |

### 2. RNF — qualidades a analisar

| ID | Qualidade | FURPS+ | Descrição |
|---|---|---|---|
| RNF01 | Escalabilidade | Supportability | Suportar o crescimento de 1.000 para 100.000 usuários, sendo o ranking a parte que cresce mais. |
| RNF02 | Desempenho | Performance | Consultas de ranking com tempo de resposta aceitável mesmo com volume alto de leituras. |
| RNF03 | Complexidade operacional | + (Operação) | Custo de implantar, monitorar e manter a solução com uma equipe de 6 devs. |
| RNF04 | Manutenibilidade | Supportability | Alterar uma regra de gamificação sem mexer em várias classes. |

### 3. ASR — seleção obrigatória

| ASR | RNF de origem | Como altera a arquitetura |
|---|---|---|
| ASR01 | RNF04 — Manutenibilidade | Exige fronteiras explícitas por funcionalidade, o que muda a estrutura do Core. |
| ASR02 | RNF01 — Escalabilidade (ranking) | O ranking pode exigir cache, modelo de leitura ou escala própria, o que muda dados e implantação. |
| ASR03 | RNF03 — Complexidade operacional | Cada serviço separado adiciona deploy, rede e monitoramento, o que muda a operação. |

### 4. RPC — restrições e premissas do desafio

| ID | Tipo | Restrição / premissa |
|---|---|---|
| RPC01 | Tecnologia (legado) | O Core já existe em Java 21/Spring Boot 3.5 (JPA) e deve continuar nessa stack; alternativas que exijam reescrevê-lo em outra tecnologia ficam fora. |
| RPC02 | Equipe (premissa do case) | O case considera uma equipe de 6 desenvolvedores, que também cuidam de implantação e operação. Isso limita quantos deploys, pipelines e serviços dá para manter. |
| RPC03 | Dados / infraestrutura | Há um único banco relacional (PostgreSQL 16 no `docker-compose`; H2 no perfil local) compartilhado por alunos, cursos e matrículas. Separar serviços exigiria dividir o banco ou aceitar banco compartilhado entre serviços. |
| RPC04 | Legado da AC1 | As regras de gamificação foram construídas por TDD no pacote `domain` (`PlataformaEnsino`, `Aluno`, `Matricula`), com testes; devem ser reaproveitadas, não reescritas. |
| P01 | Premissa | O crescimento até 100.000 usuários é uma projeção. O ranking (RF01) ainda não está implementado no repositório da AC1, então não há medição de gargalo real. |

### 5. Decisão do Tech Lead

**Quais RNFs tornam-se ASRs neste cenário?**
Manutenibilidade (RNF04), escalabilidade do ranking (RNF01) e complexidade operacional (RNF03). O desempenho (RNF02) depende da escalabilidade do ranking e é tratado junto com ela.

**Há necessidade comprovada de escala/deploy independente?**
Não. O ranking é a funcionalidade que mais cresce, mas o problema dele é volume de leitura, que se resolve dentro do monólito com cache ou com uma tabela/visão pré-calculada de ranking, sem separar o deploy. Nenhuma funcionalidade tem ciclo de entrega ou stack diferente do restante do Core (a IA em Python só entra no Desafio 3).

**A complexidade distribuída é justificável?**
Não neste momento. Com 6 devs e um banco único (RPC02 e RPC03), cada serviço novo traria pipeline, rede, monitoramento e consistência entre bancos sem resolver um problema medido. O problema real encontrado no código é de organização interna: na AC1, a regra "média > 7,0 libera +3 cursos" está duplicada — existe em `PlataformaEnsino` (testada) e foi reimplementada em `MatriculaService` (que é o que a API executa). Ou seja, os testes de domínio não protegem o fluxo real, e alterar a regra exige mexer em mais de uma classe.

**Decisão:** evoluir para **Monólito Modular**, com módulos por capacidade (Alunos, Cursos, Matrículas, Conquistas e Ranking) e fronteiras explícitas: cada módulo expõe uma interface pública e mantém a implementação interna. O ranking ganha um modelo de leitura próprio (cache/tabela de ranking) dentro do monólito. A extração para serviço fica condicionada a gatilhos medidos (ver ADR-001).

### 6. Alternativas e trade-offs

| Critério | (A) Monólito em Camadas | (B) Monólito Modular | (C) Decomposição seletiva (Ranking como serviço) |
|---|---|---|---|
| ASR01 — Manutenibilidade | Fraca: pacotes por tipo técnico (controller/service/repository) e regra espalhada entre classes | Forte: cada regra fica dentro do seu módulo, atrás de uma interface | Forte no serviço extraído; o restante continua com o mesmo problema |
| ASR02 — Escalabilidade do ranking | Escala o app inteiro; cache possível, mas sem fronteira clara | Escala o app inteiro; cache/modelo de leitura isolado no módulo Ranking | Escala o ranking de forma independente (melhor neste critério) |
| ASR03 — Complexidade operacional | Baixa: 1 deploy, 1 banco | Baixa: 1 deploy, 1 banco | Alta: +1 deploy, rede, monitoramento distribuído, consistência entre Core e Ranking |
| Aderência às RPCs | Atende | Atende e reaproveita o domínio da AC1 | Conflita com RPC02 (equipe) e RPC03 (banco único) |
| Custo de migração | Zero | Baixo/médio: reorganização de pacotes | Alto |

> **Trade-off aceito:** com a opção B, abrimos mão da escala independente do ranking em troca de simplicidade operacional e de regras de domínio bem localizadas. Como os módulos já terão fronteira definida, uma extração futura do Ranking (opção C) fica mais barata.

### 7. Mini-lab / evidência

**Hipótese testada:** organizar a funcionalidade de Conquistas como um módulo com fronteira explícita reduz o acoplamento da regra de gamificação ao fluxo de matrícula, sem precisar separar serviços.

Tudo o que está nesta seção está versionado no repositório e pode ser reproduzido:

| Evidência | Onde está |
|---|---|
| Módulo Conquistas | [`src/main/java/.../conquistas/`](../../src/main/java/com/example/grupo_1_gameeducator/conquistas) — `ConquistasApi` (pública) e `ConquistasService` (package-private) |
| Fluxo de matrícula refatorado | [`MatriculaService`](../../src/main/java/com/example/grupo_1_gameeducator/service/MatriculaService.java) depende só de `ConquistasApi` |
| Teste de fronteira | [`FronteiraConquistasTest`](../../src/test/java/com/example/grupo_1_gameeducator/conquistas/FronteiraConquistasTest.java) |
| Testes do módulo (sem Spring/banco) | [`ConquistasServiceTest`](../../src/test/java/com/example/grupo_1_gameeducator/conquistas/ConquistasServiceTest.java) |
| Teste do fluxo real da API (Spring + H2) | [`ConquistasIntegracaoTest`](../../src/test/java/com/example/grupo_1_gameeducator/conquistas/ConquistasIntegracaoTest.java) |
| Script de medição + saídas antes/depois | [`evidencias/desafio1/`](evidencias/desafio1) |

#### 7.1 Diagnóstico do estado anterior (commit `cc06434`)

O Core estava organizado por camada técnica (`controller`, `service`, `repository`, `domain`, `dto`). Os métodos `PlataformaEnsino.finalizarCurso` e `PlataformaEnsino.iniciarResgate`, cobertos pelos testes de `domainTest`, não eram chamados pela API: o `MatriculaService` reimplementava a mesma regra (5 referências à média mínima, aos +3 cursos e ao limite de 10 opções). Alterar a regra exigia mexer em 2 classes, e os testes de domínio não protegiam o fluxo real.

#### 7.2 Reorganização modular

- Criado o pacote `conquistas` com a interface pública `ConquistasApi` e a implementação package-private `ConquistasService`, que **delega para o domínio já testado** (`PlataformaEnsino.finalizarCurso` e `PlataformaEnsino.iniciarResgate`). Nenhuma regra foi reescrita (RPC04).
- O `MatriculaService` passou a receber `ConquistasApi` no construtor e não usa mais `PlataformaEnsino` nem `StatusMatricula`.
- O comportamento da API foi mantido: mesmas rotas, mesmos DTOs e mesmas mensagens de erro.

#### 7.3 Resultado — antes × depois

Medido com `python docs/arquitetura/evidencias/desafio1/medir_dependencias.py` ([antes](evidencias/desafio1/dependencias-antes.txt) · [depois](evidencias/desafio1/dependencias-depois.txt)):

| Métrica | Antes (`cc06434`) | Depois (módulo Conquistas) |
|---|---|---|
| Referências à regra de gamificação no `MatriculaService` | 5 | 0 |
| `MatriculaService` depende de `PlataformaEnsino` e `StatusMatricula` | Sim | Não (só de `ConquistasApi`) |
| A API executa a regra coberta pelos testes de domínio? | Não (regra duplicada) | Sim (comprovado por `ConquistasIntegracaoTest`) |
| Classes a alterar para mudar a regra de +3 cursos | 2 (`PlataformaEnsino` e `MatriculaService`) | 1 (`PlataformaEnsino`, atrás do módulo) |
| Imports do projeto no `MatriculaService` | 10 | 10 |
| Imports entre classes de pacotes diferentes (total) | 37 | 45 |
| Dependências entre pacotes (pares distintos) | 7 | 10 |

**Leitura:** o total de dependências sobe (37 → 45 imports; 7 → 10 pares de pacotes), porque o módulo novo é mais um pacote, com interface e implementação. O ganho não está na quantidade, e sim na direção e na concentração: a regra deixou de estar duplicada, o fluxo de matrícula depende só do contrato do módulo e alterar a regra afeta um único lugar. Isso sustenta o ASR01 (manutenibilidade) sem custo operacional novo (ASR03): continua 1 deploy e 1 banco.

**O que ainda não foi comprovado:** a escalabilidade do ranking (ASR02). O ranking não existe no código da AC1, então não há métrica de carga; a extração continua condicionada aos gatilhos do ADR-001.

#### 7.4 Execução dos testes

`./mvnw test` na raiz do projeto. Print da execução: [`evidencias/desafio1/mvn-test.png`](evidencias/desafio1/mvn-test.png).

### 8. ADR + C4

#### ADR-001 — Evolução do Core para Monólito Modular

| Campo | Conteúdo |
|---|---|
| Status | Aceito |
| Contexto | O produto vai de 1.000 para até 100.000 usuários, e o ranking (ainda não implementado) é a parte que mais cresce. As regras de gamificação mudam com frequência. Na AC1, o Core é um monólito em camadas técnicas e a regra de cursos adicionais está duplicada entre `PlataformaEnsino` e `MatriculaService`. |
| ASRs | ASR01 Manutenibilidade · ASR02 Escalabilidade do ranking · ASR03 Complexidade operacional |
| RPCs | Core em Java 21/Spring Boot · equipe de 6 devs (premissa do case) · banco relacional único · reaproveitar o domínio da AC1 |
| Alternativas | (A) Monólito em Camadas · (B) Monólito Modular · (C) Decomposição seletiva do Ranking em serviço |
| Decisão | Adotar (B): módulos por capacidade (Alunos, Cursos, Matrículas, Conquistas e Ranking) que se comunicam apenas por interfaces públicas, com 1 deploy e 1 banco. O ranking terá modelo de leitura/cache próprio dentro do módulo. |
| Consequências positivas | Regras em um único lugar; testes de domínio passam a proteger o fluxo real; operação continua simples; extração futura mais barata. |
| Consequências negativas / trade-offs | O ranking não escala separado do restante; a disciplina de fronteiras precisa ser garantida por convenção e teste (o compilador não impede tudo); o banco continua compartilhado entre módulos. |
| Gatilhos para revisitar (rumo à opção C) | Ranking com tempo de resposta fora da meta mesmo com cache; necessidade de deploy do ranking em ritmo diferente do Core; carga do ranking exigindo recursos que encarecem a escala do monólito inteiro. |

#### C4 — Container (nível afetado)

A decisão é interna ao Core: a implantação continua com um container de aplicação (`gameeducator-api`) e um banco PostgreSQL, como no `docker-compose` atual. O front web que roda hoje é HTML/CSS/JS estático em `src/main/resources/static`, servido pelo próprio Core. O arquivo `frontend-vue/index.html` é um cliente Vue avulso que chama `localhost:8080` e não faz parte do `docker-compose`. O pgAdmin do `docker-compose` é ferramenta de apoio e não aparece no diagrama.

![C4 Container do GameEducator — estado-alvo após o ADR-001](c4-container-desafio1.png)

*Figura 1 — C4 Container do GameEducator: estado-alvo após o ADR-001. Elementos tracejados são planejados.*

## Desafio 2 — Organização interna do Core

*Proteger o domínio contra dependências técnicas.*

**Cenário / problema:** as regras de ranking, moedas, badges e recompensas mudam com frequência. A equipe precisa evitar que domínio, banco, framework e integrações externas fiquem fortemente acoplados.

### 1. RF — o que o sistema precisa fazer

| ID | Requisito | Descrição |
|---|---|---|
| RF01 | Ranking | Classificar e exibir os alunos por desempenho (ainda não implementado; entra como módulo planejado). |
| RF02 | Conquistas e regras de gamificação | Média acima de 7,0 libera +3 cursos adicionais; o resgate oferece até 10 cursos da mesma área para o aluno escolher 3. |

### 2. RNF — qualidades a analisar

| ID | Qualidade | FURPS+ | Descrição |
|---|---|---|---|
| RNF04 | Manutenibilidade | Supportability | Alterar uma regra de gamificação mexendo em um só lugar (vem do Desafio 1). |
| RNF05 | Testabilidade | Supportability | Testar as regras e o caso de uso sem subir Spring, sem banco e sem mock de framework. |
| RNF06 | Modificabilidade | Supportability | Trocar a tecnologia que fornece os dados (JPA, memória, outro serviço) sem alterar a regra. |
| RNF07 | Facilidade de evolução | Supportability | Novas regras (moedas, badges, ranking) entram seguindo o mesmo padrão, sem reorganizar o Core. |

### 3. ASR — seleção obrigatória

| ASR | RNF de origem | ASR? | Como altera a arquitetura |
|---|---|---|---|
| ASR04 | RNF05 — Testabilidade | Sim | Exige que o caso de uso receba as dependências de infraestrutura por interfaces (portas), o que muda a estrutura interna do módulo. |
| ASR05 | RNF06 — Modificabilidade | Sim | Exige um ponto único (adapter) onde a tecnologia de dados é conectada, o que muda quem depende de quem dentro do Core. |
| ASR01 | RNF04 — Manutenibilidade | Sim (continua do D1) | A fronteira do módulo, criada no D1, passa a valer também para a infraestrutura: a regra fica isolada do banco. |
| — | RNF07 — Facilidade de evolução | Não, neste momento | É consequência das três acima; o ranking ainda não existe para medir esse ganho. |

### 4. RPC — restrições e premissas do desafio

| ID | Tipo | Restrição / premissa |
|---|---|---|
| RPC01 | Tecnologia (legado) | O Core continua em Java 21/Spring Boot 3.5 com Spring Data JPA (vem do D1). |
| RPC04 | Legado da AC1 | As regras de `PlataformaEnsino`, `Aluno` e `Matricula`, construídas por TDD, devem ser reaproveitadas, não reescritas (vem do D1). |
| RPC05 | Escopo | Não é permitido reescrever o produto inteiro só para aplicar um padrão: a mudança precisa ser proporcional ao problema. |
| RPC06 | Legado da AC1 | As classes de domínio também são as entidades JPA (`@Entity`, `@Embeddable`). Separar modelo de domínio e modelo de persistência exigiria reescrever todas elas e criar mapeadores. |
| P02 | Premissa | O ranking (RF01) ainda não está implementado; o padrão é validado no módulo Conquistas e replicado no Ranking quando ele for construído. |

### 5. Decisão do Tech Lead

**Qual dependência realmente precisa ser invertida?**
A do caso de uso de gamificação para o repositório JPA. Depois do Desafio 1, o `ConquistasService` recebia o `CursoRepository` (uma interface do Spring Data) no construtor. Por isso, o teste do módulo precisava de um mock do Mockito de uma interface do Spring Data, e trocar a origem dos cursos exigia mexer no caso de uso. Essa é a dependência que liga a regra à tecnologia.

As anotações JPA nas classes de domínio **não** foram invertidas: elas são metadados, não chamam o banco, e os testes de domínio já rodam sem JPA (`new Aluno(...)`, `new Curso(...)`). Separá-las custaria reescrever todas as entidades (RPC05 e RPC06) sem resolver um problema medido.

**O custo estrutural é proporcional ao problema?**
Sim, com a aplicação seletiva: 1 interface (porta de saída `CatalogoDeCursos`), 1 adapter de produção (`CatalogoDeCursosJpa`, 25 linhas) e 1 adapter de teste (`CatalogoDeCursosEmMemoria`, 27 linhas). Nenhuma regra foi reescrita, nenhuma rota mudou e o deploy continua igual. Aplicar Hexagonal completa em todo o Core (cadastro de aluno e de curso, que são CRUD sem regra de gamificação) não seria proporcional.

**DDD, Hexagonal, Clean e Onion são concorrentes ou podem ser combinados?**
Podem ser combinados, porque respondem a perguntas diferentes:

- **DDD (tático)** trata de *como modelar* o domínio. O projeto já usa entidades e Value Objects desde a AC1 (`NomeAluno`, `TituloCurso`, `AreaCurso`...); isso continua.
- **Hexagonal (Ports & Adapters)** trata de *como o domínio conversa com o mundo de fora*: portas de entrada e de saída, com adapters plugáveis. É o que resolve o problema deste desafio.
- **Clean e Onion** seguem a mesma regra de dependência da Hexagonal (as dependências apontam para o domínio), com mais camadas nomeadas (entidades, casos de uso, interface adapters, frameworks). Adotamos a regra de dependência, mas não as camadas extras: elas acrescentariam classes sem resolver nenhum problema do case.

**Decisão:** aplicar **Ports & Adapters (Hexagonal) de forma seletiva** dentro dos módulos de regra de gamificação do Monólito Modular (ADR-001). Cada módulo tem uma porta de entrada (a `*Api` pública do D1), um caso de uso interno e portas de saída para tudo o que for infraestrutura. Os adapters ficam em subpacotes `adapter.*`. O domínio da AC1 é mantido como está, incluindo as anotações JPA (trade-off registrado no ADR-002). A regra é protegida por teste de arquitetura.

### 6. Alternativas e trade-offs

| Critério | (A) Estrutura atual (módulo do D1 chamando o JPA) | (B) Ports & Adapters seletivo | (C) Hexagonal/Clean completa em todo o Core |
|---|---|---|---|
| ASR04 — Testabilidade | Média: o caso de uso só é testado com mock do repositório ou com Spring + H2 | Alta: o caso de uso roda com um adapter em memória, sem Spring, banco ou mock | Alta |
| ASR05 — Modificabilidade | Baixa: trocar a origem dos dados muda o caso de uso | Alta no módulo: trocar a origem só muda o adapter | Alta em todo o Core |
| ASR01 — Manutenibilidade | Regra isolada dos outros módulos (D1), mas presa ao JPA | Regra isolada dos outros módulos e da infraestrutura | Igual a (B), com muito mais classes para manter |
| RPC05 — Proporcionalidade | Nenhuma mudança | 3 classes novas, 1 alterada | Reescrever 3 entidades, 5 VOs, criar modelos de persistência e mapeadores |
| RPC04/RPC06 — Legado | Atende | Atende (domínio intacto) | Conflita: as entidades JPA da AC1 teriam de ser desmontadas |

> **Trade-off aceito:** com a opção B, o domínio continua anotado com JPA e o caso de uso continua com a anotação `@Service` do Spring. Aceitamos isso porque nenhuma das duas faz o domínio chamar o banco ou o framework, e os testes rodam sem eles. O que fica proibido (e verificado por teste) é o núcleo do módulo importar repositório, Spring Data ou `jakarta.persistence`, e o domínio importar Spring.

### 7. Mini-lab / evidência

**Hipótese testada:** isolar a regra de resgate atrás de uma porta de saída permite trocar o adapter de persistência (JPA ↔ memória) sem mudar o caso de uso e sem mudar os testes de domínio.

| Evidência | Onde está |
|---|---|
| Porta de saída | [`CatalogoDeCursos`](../../src/main/java/com/example/grupo_1_gameeducator/conquistas/CatalogoDeCursos.java) |
| Caso de uso depende só da porta | [`ConquistasService`](../../src/main/java/com/example/grupo_1_gameeducator/conquistas/ConquistasService.java) |
| Adapter de produção (JPA) | [`adapter/jpa/CatalogoDeCursosJpa`](../../src/main/java/com/example/grupo_1_gameeducator/conquistas/adapter/jpa/CatalogoDeCursosJpa.java) |
| Adapter de teste (memória) | [`adapter/memoria/CatalogoDeCursosEmMemoria`](../../src/test/java/com/example/grupo_1_gameeducator/conquistas/adapter/memoria/CatalogoDeCursosEmMemoria.java) |
| Mesmo contrato rodando nos dois adapters | [`CatalogoDeCursosContrato`](../../src/test/java/com/example/grupo_1_gameeducator/conquistas/adapter/CatalogoDeCursosContrato.java), executado por [`CatalogoDeCursosEmMemoriaTest`](../../src/test/java/com/example/grupo_1_gameeducator/conquistas/adapter/CatalogoDeCursosEmMemoriaTest.java) e [`CatalogoDeCursosJpaTest`](../../src/test/java/com/example/grupo_1_gameeducator/conquistas/adapter/CatalogoDeCursosJpaTest.java) (`@DataJpaTest` + H2) |
| Caso de uso testado com o adapter em memória | [`ConquistasServiceTest`](../../src/test/java/com/example/grupo_1_gameeducator/conquistas/ConquistasServiceTest.java) |
| Teste de arquitetura | [`FronteiraHexagonalTest`](../../src/test/java/com/example/grupo_1_gameeducator/conquistas/FronteiraHexagonalTest.java) |
| Script de medição + saídas antes/depois | [`evidencias/desafio2/`](evidencias/desafio2) |

#### 7.1 O que mudou

- Criada a porta de saída `CatalogoDeCursos` no pacote do módulo (`relacionadosA(cursoConcluido)`).
- O `ConquistasService` passou a receber `CatalogoDeCursos` no construtor, em vez de `CursoRepository`.
- O acesso ao Spring Data foi para o adapter `conquistas.adapter.jpa.CatalogoDeCursosJpa`, único ponto do módulo que conhece o repositório. O Spring injeta esse adapter automaticamente, então `MatriculaService`, controllers e rotas não mudaram.
- Nos testes, o `ConquistasServiceTest` trocou o mock do Mockito do `CursoRepository` pelo `CatalogoDeCursosEmMemoria`. Os 6 cenários continuam os mesmos.
- Os testes de domínio da AC1 (`domainTest`, 9 classes) não foram alterados.

#### 7.2 Troca de adapter

O contrato `CatalogoDeCursosContrato` define o comportamento esperado da porta (só cursos da mesma área, sem o próprio curso, sem limitar a quantidade, porque o limite de 10 é regra do domínio). Ele é executado duas vezes, uma por adapter:

| Execução | Adapter | Sobe Spring? | Usa banco? |
|---|---|---|---|
| `CatalogoDeCursosEmMemoriaTest` | `CatalogoDeCursosEmMemoria` | Não | Não |
| `CatalogoDeCursosJpaTest` | `CatalogoDeCursosJpa` | Sim (`@DataJpaTest`) | H2 |
| `ConquistasServiceTest` (caso de uso) | `CatalogoDeCursosEmMemoria` | Não | Não |
| `ConquistasIntegracaoTest` (fluxo da API, do D1) | `CatalogoDeCursosJpa` | Sim (`@SpringBootTest`) | H2 |

Os mesmos testes passando com os dois adapters mostram que eles são intercambiáveis e que o caso de uso não depende de qual está plugado.

#### 7.3 Resultado — antes × depois

Medido com `python docs/arquitetura/evidencias/desafio2/medir_acoplamento.py` ([antes](evidencias/desafio2/acoplamento-antes.txt) · [depois](evidencias/desafio2/acoplamento-depois.txt)):

| Métrica | Antes (após o D1, `1c0b840`) | Depois (Ports & Adapters) |
|---|---|---|
| Imports de repositório/Spring Data/JPA no núcleo do módulo Conquistas | 1 (`ConquistasService` → `CursoRepository`) | 0 |
| Classes do módulo que conhecem o repositório | 1 (o caso de uso) | 1 (só o adapter `CatalogoDeCursosJpa`) |
| Classes a alterar para trocar a origem dos cursos | 1 (o caso de uso) | 0 no núcleo; só um adapter novo |
| Teste do caso de uso precisa de mock de interface do Spring Data? | Sim (Mockito) | Não (adapter em memória) |
| Adapters da porta de catálogo | — | 2 (JPA e memória), validados pelo mesmo contrato |
| Testes de domínio da AC1 alterados | — | 0 |
| Imports de Spring/repositório no domínio | 0 | 0 |
| Imports de `jakarta.persistence` no domínio | 34 (8 classes) | 34 (8 classes) — trade-off mantido |

**Leitura:** a dependência que ligava a regra de resgate à tecnologia foi invertida: o caso de uso agora define o que precisa (`CatalogoDeCursos`) e a infraestrutura se adapta a ele. A troca de adapter foi feita sem tocar no caso de uso nem nos testes de domínio, o que sustenta ASR04 e ASR05. O acoplamento do domínio ao JPA não mudou, e isso foi uma escolha (RPC05/RPC06), não um esquecimento: o teste de arquitetura impede que ele piore (o domínio não pode importar Spring nem repositórios).

**O que ainda não foi comprovado:** a facilidade de evolução para o ranking (RNF07). O padrão está pronto para ser repetido, mas só será medido quando o RF01 for implementado.

#### 7.4 Execução dos testes

`./mvnw test` na raiz do projeto. Print da execução: [`evidencias/desafio2/mvn-test.png`](evidencias/desafio2/mvn-test.png).

### 8. ADR + C4

#### ADR-002 — Ports & Adapters seletivo nos módulos de gamificação

| Campo | Conteúdo |
|---|---|
| Status | Aceito |
| Contexto | As regras de gamificação mudam com frequência. Depois do ADR-001, a regra está isolada no módulo Conquistas, mas o caso de uso (`ConquistasService`) dependia diretamente do `CursoRepository` (Spring Data JPA): testá-lo exigia mock de framework ou banco, e trocar a origem dos dados exigia alterar o caso de uso. O domínio da AC1 também é o modelo JPA. |
| ASRs | ASR04 Testabilidade · ASR05 Modificabilidade · ASR01 Manutenibilidade |
| RPCs | Core em Spring Boot · reaproveitar as regras da AC1 · não reescrever o produto para aplicar um padrão · domínio = entidades JPA |
| Alternativas | (A) Manter o módulo chamando o JPA · (B) Ports & Adapters seletivo nos módulos de regra · (C) Hexagonal/Clean completa em todo o Core, com modelo de persistência separado |
| Decisão | Adotar (B). Cada módulo de gamificação tem porta de entrada (`*Api`), caso de uso interno e portas de saída para infraestrutura; adapters ficam em `adapter.*`. DDD tático (entidades e VOs) continua no domínio. Clean/Onion não são adotadas como camadas extras; só a regra de dependência. |
| Consequências positivas | Caso de uso testável sem Spring, banco ou mock; troca de tecnologia de dados restrita ao adapter; regra de dependência verificada por teste (`FronteiraHexagonalTest`); padrão pronto para o módulo Ranking. |
| Consequências negativas / trade-offs | Mais uma interface e um adapter por dependência externa; o domínio continua anotado com JPA; o caso de uso continua com `@Service`; o contrato da porta precisa ser mantido para todos os adapters. |
| Gatilhos para revisitar (rumo à opção C) | O domínio passar a ser persistido de forma muito diferente do modelo JPA (ex.: banco não relacional ou outro serviço); mudanças no mapeamento JPA começarem a quebrar testes de regra; o mesmo domínio ser usado fora do Spring. |

#### C4 — Component (Core)

O C4 Container do Desafio 1 não muda: continua 1 container de aplicação e 1 banco. A decisão é interna ao Core, por isso o nível afetado é o Component. O diagrama mostra o módulo Conquistas com a porta de entrada, o caso de uso, a porta de saída e os dois adapters (o JPA, em produção, e o em memória, só nos testes). O módulo Ranking aparece tracejado porque ainda é planejado.

![C4 Component do Core GameEducator — estado após o ADR-002](c4-component-desafio2.png)

*Figura 2 — C4 Component do Core GameEducator após o ADR-002. Amarelo: portas; tracejado: planejado ou só em teste. Fonte editável: [`c4-component-desafio2.mmd`](c4-component-desafio2.mmd).*

## Desafio 3 — Serviço Python e IA

*Definir a fronteira arquitetural de ML e RAG/LLM.*

**Cenário / problema:** a plataforma incorpora recomendação inteligente e assistente educacional. Os modelos serão desenvolvidos em Python, terão ciclo de atualização próprio, carga variável e poderão ficar indisponíveis sem impedir operações essenciais do Core.

### 1. RF — o que o sistema precisa fazer

| ID | Requisito | Descrição |
|---|---|---|
| RF03 | Recomendação inteligente | Sugerir ao aluno cursos com base no histórico dele e de alunos parecidos (ML). |
| RF04 | Assistente educacional | Responder dúvidas do aluno sobre o conteúdo dos cursos, usando RAG sobre o material e um LLM. |

### 2. RNF — qualidades a analisar

| ID | Qualidade | FURPS+ | Descrição |
|---|---|---|---|
| RNF08 | Deploy independente | + (Implantação) | Atualizar ou retreinar modelos sem reimplantar o Core. |
| RNF09 | Escalabilidade | Supportability | A IA tem carga variável (picos de uso do assistente) e custo de CPU/GPU diferente do Core. |
| RNF10 | Tolerância a falhas | Reliability | A IA fora do ar ou lenta não pode impedir matrícula, conclusão e resgate. |
| RNF11 | Interoperabilidade | + (Interface) | Java (Core) e Python (IA) precisam conversar por um contrato estável. |
| RNF12 | Desempenho | Performance | A recomendação não pode deixar a tela do aluno lenta. |
| RNF13 | Manutenibilidade | Supportability | Cientistas de dados e devs Java evoluem cada lado sem quebrar o outro. |

### 3. ASR — seleção obrigatória

| ASR | RNF de origem | ASR? | Como altera a arquitetura |
|---|---|---|---|
| ASR06 | RNF10 — Tolerância a falhas | Sim | Exige uma fronteira com timeout e resposta degradada entre o Core e a IA, o que muda a comunicação e o tratamento de erro. |
| ASR07 | RNF08 — Deploy independente | Sim | Modelos com ciclo próprio exigem um artefato e um deploy separados do Core, o que muda a implantação. |
| ASR08 | RNF09 — Escalabilidade | Sim | Carga e recursos diferentes exigem escalar a IA sem escalar o Core, o que muda a implantação e a operação. |
| — | RNF11 — Interoperabilidade | Não como driver | É atendida por HTTP/JSON, que o Core já usa; não decide sozinha a fronteira. |
| — | RNF12 — Desempenho | Não, neste momento | Sem medição de latência do modelo; o timeout de 2 s (ASR06) já limita o impacto na tela. |
| — | RNF13 — Manutenibilidade | Não como driver | Melhora com a separação, mas é consequência de ASR07. |

### 4. RPC — restrições e premissas do desafio

| ID | Tipo | Restrição / premissa |
|---|---|---|
| RPC01 | Tecnologia (legado) | O Core permanece em Java 21/Spring Boot (vem dos desafios anteriores). |
| RPC07 | Tecnologia | ML e RAG serão implementados em Python. |
| RPC08 | Disponibilidade | A indisponibilidade da IA não pode bloquear o fluxo essencial (matrícula, conclusão, resgate). |
| RPC02 | Equipe (premissa do case) | 6 devs cuidam também da operação: cada serviço novo tem custo (vem do D1). |
| P03 | Premissa | O modelo do lab é um conjunto de regras determinístico; a decisão é sobre a fronteira, não sobre a qualidade do modelo. |

### 5. Decisão do Tech Lead

**Python por si só justifica um serviço?**
Não. Só a linguagem poderia ser resolvida de outras formas (rodar o modelo exportado dentro da JVM, por exemplo). O que justifica o serviço são os ASRs: ciclo de atualização próprio (ASR07), carga e recursos diferentes (ASR08) e a necessidade de isolar a falha (ASR06). No D1, recusamos separar o Ranking porque nenhum desses três estava presente; aqui os três estão, por premissa do case.

**ML e RAG precisam da mesma fronteira?**
Precisam da mesma fronteira em relação ao Core (os dois ficam fora dele, atrás de HTTP com timeout e degradação), mas não precisam ser o mesmo deploy para sempre. Começamos com **um serviço Python com dois módulos** (recomendação e, depois, assistente), porque a equipe é pequena (RPC02) e os dois compartilham stack e ciclo de modelo. A separação em dois serviços fica condicionada a gatilhos: o RAG precisar de escala ou GPU muito diferente da recomendação, ou o custo/latência do provedor de LLM afetar a recomendação.

**Quais ASRs justificam autonomia e escala?**
ASR07 (deploy independente) e ASR08 (escala independente). O ASR06 (tolerância a falhas) justifica o *modo* como o Core consome a IA: chamada com timeout curto e resposta degradada.

**Qual comportamento degradado é aceitável?**

| Funcionalidade | IA no ar | IA fora do ar ou lenta (> 2 s) |
|---|---|---|
| Matrícula, conclusão, resgate | Normal | Normal: nunca chamam a IA |
| Recomendações (RF03) | Lista personalizada, `origem = IA` | HTTP 200, lista vazia, `origem = INDISPONIVEL` e mensagem; o front segue mostrando o catálogo normal |
| Assistente (RF04, planejado) | Resposta do assistente | Mensagem "assistente indisponível"; nenhuma outra tela é afetada |

**Decisão:** criar o **Serviço de IA em Python (FastAPI)** como container próprio, consumido pelo Core por **HTTP/JSON com timeout de 2 s e resposta degradada**. No Core, a integração segue o padrão do ADR-002: módulo `recomendacoes` com porta de entrada (`RecomendacoesApi`), caso de uso interno e porta de saída (`MotorDeRecomendacao`) implementada por um adapter HTTP. O fluxo essencial não depende do módulo de recomendações, e um teste de arquitetura garante isso.

### 6. Alternativas e trade-offs

| Critério | (A) IA embutida no Core | (B) Módulo acoplado (Python chamado pelo Core no mesmo processo/host) | (C) Serviço Python independente |
|---|---|---|---|
| ASR06 — Tolerância a falhas | Fraca: erro ou lentidão do modelo derruba ou trava o Core | Fraca/média: falha do processo Python afeta o Core, que o controla | Forte: falha isolada; Core usa timeout e degradação |
| ASR07 — Deploy independente | Não: atualizar modelo = reimplantar o Core | Parcial: artefatos separados, mas o deploy continua junto | Sim: imagem e deploy próprios |
| ASR08 — Escala independente | Não: escala o Core inteiro | Não: escalam juntos | Sim |
| RPC07 — Python | Conflita (exigiria levar o modelo para a JVM) | Atende | Atende |
| RPC02 — Custo operacional | Menor | Médio (orquestrar dois runtimes no mesmo deploy) | Maior: +1 container, rede, monitoramento |

> **Trade-off aceito:** com a opção C, ganhamos isolamento de falha, deploy e escala independentes, ao custo de mais um container para operar e de uma chamada de rede com latência e falhas possíveis. Mitigação: timeout curto, resposta degradada e o fluxo essencial sem dependência da IA. Sobre ML e RAG juntos, aceitamos que um pico do assistente possa afetar a recomendação enquanto estiverem no mesmo serviço (a recomendação é opcional para o fluxo).

### 7. Mini-lab / evidência

**Hipótese testada:** com o serviço Python fora do ar ou lento, o Core continua executando o fluxo essencial e responde às recomendações de forma degradada, em tempo limitado, sem erro para o aluno.

| Evidência | Onde está |
|---|---|
| Serviço FastAPI mínimo + testes + Dockerfile | [`labs/desafio3-ia-fastapi/`](../../labs/desafio3-ia-fastapi) |
| Serviço `ia` no `docker-compose` (Core usa `IA_BASE_URL=http://ia:8000`) | [`docker-compose.yml`](../../docker-compose.yml) |
| Módulo `recomendacoes` (porta de entrada, caso de uso, porta de saída) | [`src/main/java/.../recomendacoes/`](../../src/main/java/com/example/grupo_1_gameeducator/recomendacoes) |
| Adapter HTTP com timeout | [`MotorDeRecomendacaoHttp`](../../src/main/java/com/example/grupo_1_gameeducator/recomendacoes/adapter/http/MotorDeRecomendacaoHttp.java) |
| Endpoint `GET /api/alunos/{id}/recomendacoes` | [`RecomendacaoRestController`](../../src/main/java/com/example/grupo_1_gameeducator/controller/RecomendacaoRestController.java) |
| Adapter contra servidor HTTP falso: IA no ar, fora do ar, lenta e com erro 500 | [`MotorDeRecomendacaoHttpTest`](../../src/test/java/com/example/grupo_1_gameeducator/recomendacoes/adapter/MotorDeRecomendacaoHttpTest.java) |
| Caso de uso com IA no ar e fora do ar | [`RecomendacoesServiceTest`](../../src/test/java/com/example/grupo_1_gameeducator/recomendacoes/RecomendacoesServiceTest.java) |
| Core inteiro (Spring + H2) com a IA fora do ar | [`IaForaDoArIntegracaoTest`](../../src/test/java/com/example/grupo_1_gameeducator/recomendacoes/IaForaDoArIntegracaoTest.java) |
| Teste de arquitetura: fluxo essencial não depende da IA | [`FronteiraIaTest`](../../src/test/java/com/example/grupo_1_gameeducator/recomendacoes/FronteiraIaTest.java) |
| Roteiro do mini-lab com Docker + resultado | [`evidencias/desafio3/`](evidencias/desafio3) |

#### 7.1 Cenários simulados nos testes automatizados

| Cenário | Como é simulado | Resultado esperado |
|---|---|---|
| IA no ar | Servidor HTTP local devolvendo o mesmo JSON do FastAPI | Adapter converte os cursos; caso de uso devolve `origem = IA` |
| IA fora do ar | Servidor parado / Core apontando para a porta 1 | Adapter lança `MotorDeRecomendacaoIndisponivelException`; endpoint responde 200 com `origem = INDISPONIVEL` |
| IA lenta | Servidor que demora 3 s, timeout de 300 ms | Adapter desiste em menos de 2 s, em vez de esperar os 3 s |
| IA com erro | Servidor devolvendo HTTP 500 | Tratado como indisponível |
| Fluxo essencial com IA fora | `IaForaDoArIntegracaoTest` matricula, conclui com 9,0 e inicia o resgate | Conclui, libera +3 cursos e oferece o resgate normalmente |

#### 7.2 Derrubando o Python de verdade (Docker)

O roteiro [`roteiro-falha.ps1`](evidencias/desafio3/roteiro-falha.ps1) sobe Core + PostgreSQL + IA com `docker compose`, cria um aluno e um curso e registra o resultado de cada passo em [`resultado-roteiro.txt`](evidencias/desafio3/resultado-roteiro.txt):

1. IA no ar → `GET /recomendacoes` com `origem = IA`.
2. `docker compose stop ia` → `GET /recomendacoes` com `origem = INDISPONIVEL`; matrícula e conclusão funcionam.
3. `docker compose start ia` → recomendações voltam sem reiniciar o Core.
4. Logs do Core com o aviso `Servico de IA indisponivel`.

Prints: [`ia-no-ar.png`](evidencias/desafio3/ia-no-ar.png) · [`ia-fora-do-ar.png`](evidencias/desafio3/ia-fora-do-ar.png).

#### 7.3 Execução dos testes

- Core: `./mvnw test` na raiz. Print: [`evidencias/desafio3/mvn-test.png`](evidencias/desafio3/mvn-test.png).
- Serviço de IA: `python -m pytest -v` em `labs/desafio3-ia-fastapi`. Saída: [`evidencias/desafio3/pytest-ia.txt`](evidencias/desafio3/pytest-ia.txt).

**O que ainda não foi comprovado:** a escala independente (ASR08) não foi medida, porque não há carga real; ela fica habilitada pelo container separado e será observada no Desafio 6 (métricas). O assistente RAG (RF04) não foi implementado; a decisão de mantê-lo no mesmo serviço está registrada com os gatilhos de separação.

### 8. ADR + C4

#### ADR-003 — Serviço Python de IA fora do Core, com degradação

| Campo | Conteúdo |
|---|---|
| Status | Aceito |
| Contexto | A plataforma ganha recomendação inteligente (ML) e assistente educacional (RAG/LLM), desenvolvidos em Python, com ciclo de atualização próprio e carga variável. A IA pode ficar indisponível, mas matrícula, conclusão e resgate não podem parar. |
| ASRs | ASR06 Tolerância a falhas · ASR07 Deploy independente · ASR08 Escalabilidade |
| RPCs | Core continua Java/Spring Boot · ML/RAG em Python · indisponibilidade da IA não bloqueia o fluxo essencial · equipe de 6 devs |
| Alternativas | (A) IA embutida no Core · (B) módulo acoplado no mesmo deploy · (C) serviço Python independente |
| Decisão | Adotar (C): container `ia` em Python/FastAPI, consumido pelo Core via HTTP/JSON com timeout de 2 s e resposta degradada (`origem = INDISPONIVEL`). No Core, módulo `recomendacoes` com Ports & Adapters (ADR-002). ML e RAG começam no mesmo serviço, em módulos separados. |
| Consequências positivas | Falha da IA isolada; modelos atualizados sem reimplantar o Core; IA escala sozinha; contrato HTTP simples entre Java e Python; fluxo essencial protegido por teste de arquitetura. |
| Consequências negativas / trade-offs | +1 container para operar e monitorar; latência e falhas de rede; o aluno fica sem recomendação personalizada enquanto a IA estiver fora; ML e RAG compartilham recursos enquanto estiverem juntos. |
| Gatilhos para revisitar | Separar RAG e ML quando a carga ou os recursos (GPU, cota do LLM) divergirem; trocar HTTP síncrono por eventos (Desafio 4) se recomendações puderem ser pré-calculadas; ajustar o timeout com base nas métricas de latência do Desafio 6. |

#### C4 — Container (fronteira da IA)

A IA vira um container próprio dentro do sistema. O Core continua sendo o único dono do banco; a IA não acessa o PostgreSQL diretamente nesta etapa. O provedor de LLM aparece tracejado porque só será usado pelo assistente RAG, ainda planejado. O C4 Component do Core (Desafio 2) ganha o módulo `recomendacoes`, com o mesmo formato do módulo Conquistas.

![C4 Container do GameEducator — estado após o ADR-003](c4-container-desafio3.png)

*Figura 3 — C4 Container do GameEducator após o ADR-003. Verde: container novo; tracejado: planejado. Fonte editável: [`c4-container-desafio3.mmd`](c4-container-desafio3.mmd).*
