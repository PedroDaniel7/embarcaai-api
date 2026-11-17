# Embarca Aí - Grupo HEAVEN - Global Solution 2026/2

**Disciplina:** Domain Driven Design Using Java (DDD)  
**Turma:** 1TDSPW - FIAP  
**Tecnologia:** Java 17, Spring Boot, Maven, JDBC/Oracle, ModelMapper, Bean Validation.

## Integrantes

| Nome | RM |
|---|---|
| Lucas Bossi | 572876 |
| Arthur de Souza | 573023 |
| Pedro Moraes | 569786 |
| Rick Nascimento | 571828 |
| Eduardo Oliveira Conde | 573163 |

## O que o Java faz

É uma **API REST** para a recepção da Secretaria Municipal de Saúde, baseada no **banco Oracle que o grupo realmente entregou**.

- CRUD completo de **Paciente, Consulta, Viagem e ReservaAssento** (quatro DAOs JDBC).
- `GET /unidades` e `GET /vans` permitem ao front escolher IDs válidos já cadastrados.
- `GET /viagens/{id}/ocupacao` exibe capacidade, reservas ativas e lugares livres.
- Não permite nova reserva acima da capacidade da van (HTTP **409**).
- Só reserva transporte se a consulta pedir van, a viagem tiver o destino correto e sair antes da consulta **no mesmo dia**.
- Simula respostas de WhatsApp em `POST /consultas/{id}/respostas` com `CONFIRMOU` ou `CANCELOU`:
  - **CONFIRMOU**: marca consulta CONFIRMADA, confirma reservas ativas e grava LEMBRETE. Se precisa de van e não tem reserva, responde **409**.
  - **CANCELOU**: marca consulta CANCELADA, libera todos os seus assentos e grava LEMBRETE.
- Listagens aninhadas `/pacientes/{id}/consultas` e `/consultas/{id}/reservas` demonstram o **1:N**.
- Os DTOs validam os dados e todos os erros têm JSON padronizado.

### Limites honestos do protótipo

**Não envia WhatsApp de verdade**; a rota de resposta representa essa integração para a demonstração. **Não treina/consulta IA**; a classificação do risco de falta é responsabilidade do módulo IA/Flask do grupo. **Não redistribui automaticamente consultas na fila**, porque a estrutura Oracle recebida não possui tabela de fila de espera. Ao cancelar, a vaga é liberada para remarcação pela secretaria. Não alegar que esses recursos já estão prontos.

**Privacidade:** use somente pacientes fictícios nos testes e em URLs públicas de apresentação. Esta API acadêmica não implementa autenticação ou controle de acesso; ela não deve ser usada com dados reais de saúde ou telefone sem essas proteções.

## Como abrir no IntelliJ IDEA

1. Extraia o ZIP. No IntelliJ, escolha **Open**, selecione a pasta `EmbarcaAi_Java_HEAVEN` e importe o `pom.xml` como projeto Maven.
2. Selecione o **JDK 17 ou superior** (recomendado Java 17 para corresponder ao Docker). Aguarde o Maven baixar as bibliotecas.
3. No **Oracle da FIAP**, utilizando o mesmo usuário/schema onde serão feitas as consultas:
   - **Banco ainda não instalado:** execute `sql/00_banco_original_do_grupo.sql` primeiro; depois `sql/01_sequences_java.sql`.
   - **Banco do grupo já instalado:** **não execute novamente o SQL original**; execute **somente** `sql/01_sequences_java.sql` se as sequences ainda não existirem.
4. Abra **Run > Edit Configurations > EmbarcaAiApplication > Environment variables** e coloque `DB_URL`, `DB_USER`, `DB_PASSWORD` (ver `.env.exemplo`). **Não escreva a senha em `application.properties` ou no GitHub.**
5. Execute a classe `src/main/java/br/com/heaven/embarcaai/EmbarcaAiApplication.java`.
6. Abra o Postman/Insomnia e teste `GET http://localhost:8080/pacientes`. Teste os exemplos em `exemplos/testes.http` e `exemplos/guias_de_teste.md`.

Exemplo de URL JDBC: `jdbc:oracle:thin:@//SERVIDOR:1521/SERVICO`. A FIAP precisa informar o host, serviço e as credenciais reais.

**Terminal (com Maven instalado):** `mvn clean test` e `mvn spring-boot:run`.

### Endpoints (todos usam JSON)

| Método | Rota | Resultado |
|---|---|---|
| GET / POST | `/pacientes` | Lista 200 / cria 201 + Location |
| GET / PUT / DELETE | `/pacientes/{id}` | Busca 200 / atualiza 200 / remove 204 |
| GET / POST | `/consultas` | Lista 200 / cria 201 + Location |
| GET / PUT / DELETE | `/consultas/{id}` | Busca 200 / atualiza 200 / remove 204 |
| GET / POST | `/viagens` | Lista 200 / cria 201 + Location |
| GET / PUT / DELETE | `/viagens/{id}` | Busca 200 / atualiza 200 / remove 204 |
| GET / POST | `/reservas` | Lista 200 / cria 201 + Location |
| GET / PUT / DELETE | `/reservas/{id}` | Busca 200 / atualiza 200 / remove 204 |
| GET / POST | `/pacientes/{id}/consultas` | Relação paciente 1:N consultas |
| GET | `/consultas/{id}/reservas` | Reservas da consulta |
| GET | `/viagens/{id}/reservas` | Reservas da viagem |
| GET | `/viagens/{id}/ocupacao` | Assentos livres e ocupados |
| GET | `/consultas/{id}/lembretes` | Histórico de respostas simuladas |
| POST | `/consultas/{id}/respostas` | Registra CONFIRMOU ou CANCELOU |
| GET | `/unidades`, `/vans` | Dados de referência para os formulários |

### Modelos dos JSONs

**POST /pacientes:**
```json
{"nome":"Paciente Demonstração","telefone":"11999887766","dataNascimento":"1988-02-15","zonaRural":"S"}
```

**POST /consultas:**
```json
{"pacienteId":1000,"unidadeId":4,"especialidade":"Cardiologia","dataHora":"2026-12-15T09:00:00","precisaTransporte":"S","status":"AGENDADA"}
```

**POST /viagens:**
```json
{"vanId":1,"unidadeDestinoId":4,"dataHoraSaida":"2026-12-15T06:00:00","status":"PROGRAMADA"}
```

**POST /reservas:**
```json
{"consultaId":1000,"viagemId":1000,"statusTransporte":"PENDENTE"}
```

**POST /consultas/1000/respostas:**
```json
{"resposta":"CONFIRMOU"}
```
Depois, execute novamente com `{"resposta":"CANCELOU"}`: consulta cancelada e assentos liberados.

Os IDs `1000` são exemplos válidos para um banco recém-inicializado com as sequences fornecidas. **Use os IDs reais devolvidos pelas respostas de cadastro** se forem diferentes. Nos PUTs, envie todos os campos de entrada, mesmo que só tenha mudado um deles.

### Erros para mostrar ao professor

| HTTP | Causa demonstrável |
|---|---|
| 400 | POST paciente com nome vazio, JSON inválido, status não permitido |
| 404 | GET `/consultas/999999` |
| 405 | PATCH `/pacientes/1` |
| 409 | Reserva com destino incompatível ou van lotada, duplicidade de telefone, exclusão com registro filho |
| 500 | Falha inesperada, sem mostrar SQL nem dados da senha |

Exemplo da resposta: `{"status":409,"mensagem":"Van lotada: nao existe assento disponivel","momento":"2026-11-17T12:00:00"}`.

## Explicação do código na apresentação

1. **Model**: representa `Paciente`, `Consulta`, `Viagem`, `ReservaAssento`.
2. **DTO**: valida o JSON que entra e define o JSON que sai.
3. **Controller**: recebe as requisições HTTP e informa o status correto.
4. **Service**: verifica lotação, destino e respostas; usa `@Transactional` nas alterações combinadas.
5. **DAO**: executa SQL no Oracle com `DataSource` e `PreparedStatement` **(sem JPA/Hibernate)**.
6. **Handler**: converte falhas em JSON 400/404/405/409/500.

**Fluxo:** front envia POST `/reservas` -> controller valida DTO -> service confere vagas -> DAO faz INSERT no Oracle -> controller devolve 201 e Location.

## Banco original: não modificar sem combinar com a equipe

O banco de dados **tem sete tabelas**: `PACIENTE`, `UNIDADE_SAUDE`, `VAN`, `CONSULTA`, `LEMBRETE`, `VIAGEM`, `RESERVA_ASSENTO`. A API usa **os nomes exatos**, inclusive `ID_PACIENTE`, `ID_CONSULTA`, `STATUS_TRANSPORTE`. A tabela `RESERVA_ASSENTO` faz a relação N:N de `CONSULTA` com `VIAGEM`.

**Diferença em relação à rubrica Java:** o SQL do grupo utiliza nomes como `PACIENTE`/`ID_PACIENTE`, enquanto o padrão recomendado pela disciplina é `tb_nome`/`cd_nome`. Alterar as tabelas sem alinhar com o colega de Banco quebraria as consultas da outra entrega. As **sequences da parte Java** seguem a convenção `SQ_TB_*`, incluídas à parte.

O campo **risco de falta** não existe no banco entregue, portanto não é persistido pela API. Caso o grupo queira exibi-lo no front, precisa combinar uma migração SQL com Banco e integração com IA antes de apresentar esse dado como funcional.

## Render e Front-end

O Dockerfile já tem duas etapas. No **Render**, crie um Web Service do tipo Docker e informe as variáveis de ambiente `DB_URL`, `DB_USER`, `DB_PASSWORD`, `APP_CORS_ORIGINS` (domínio do front-end, por exemplo `https://seu-front.vercel.app`). Não coloque os valores dessas variáveis em documentação ou repositório.

**Atenção:** o Render só conseguirá ler o Oracle da FIAP se o banco aceitar conexões originadas do Render. Verifique isso com a faculdade. A publicação e os testes com o Oracle real ainda precisam ser feitos.

Arquivo `integracao-front/exemplo-fetch.ts` ajuda a equipe de Front-end a chamar GET e POST com a URL correta.

## Links a completar antes da entrega

- **URL da API Render:** AINDA_NAO_PUBLICADA
- **URL vídeo YouTube (não listado, até 5 minutos):** AINDA_NAO_GRAVADO
- **URL repositório GitHub:** AINDA_NAO_PUBLICADO

## Checklist da entrega

- [ ] Abrir projeto no IntelliJ, Maven sincronizado, `mvn test` passando
- [ ] Executar SQL original e sequences Java no Oracle FIAP (uma vez)
- [ ] Testar cada um dos quatro CRUDs e demonstrações 400/404/405/409
- [ ] Integrar GET e POST reais com o Front-end usando CORS
- [ ] Publicar Docker/Render conectado ao Oracle FIAP
- [ ] Completar links no README e na documentação PDF
- [ ] Gravar vídeo, ensaiar apresentação e enviar ZIP **sem pasta target** e sem senhas

**IA generativa:** código-base e documentação produzidos com apoio do ChatGPT; integrantes devem revisar, executar e explicar antes de entregar.
