# Testes para demonstrar ao professor

Teste o arquivo `testes.http` no IntelliJ Ultimate ou copie cada requisição no Postman / Insomnia.

**Antes:** o Oracle deve ter tabelas, dados de exemplo e sequences. A aplicação deve estar rodando (`EmbarcaAiApplication`).

## Teste funcional completo

1. Faça GET `/pacientes`: deve mostrar os 12 pacientes do script do grupo (ou mais, se já houver testes).
2. Cadastre um paciente novo. Copie o `id` da resposta.
3. Cadastre uma consulta com `pacienteId` igual ao ID novo, unidade 4 e data 15/12/2026 às 09h.
4. Cadastre uma viagem com van 1, unidade destino 4 e saída 15/12/2026 às 06h.
5. Cadastre uma reserva PENDENTE ligando os dois novos IDs.
6. GET `/viagens/{id}/ocupacao` deve mostrar um assento ocupado pela reserva pendente.
7. POST `/consultas/{id}/respostas` com `CONFIRMOU` deve mudar a consulta para CONFIRMADA e a reserva para CONFIRMADO. Deve gravar um LEMBRETE.
8. POST de resposta `CANCELOU` deve atualizar a consulta para CANCELADA e a reserva para CANCELADO, liberando a vaga.

## Erros

| Teste | Resultado |
|---|---|
| Nome do paciente vazio | 400 |
| Consulta id 999999 | 404 |
| PATCH /pacientes/1 | 405 |
| Reserva de consulta do hospital 4 para viagem na unidade 3 | 409 |
| DELETE de paciente com consultas | 409 (chave estrangeira) |

## Observações

- Para testar **lotação**, use uma van existente de baixa capacidade e cadastre reservas válidas para sua viagem até atingir o limite; a próxima deverá dar 409. Não altere dados de produção para esta demonstração.
- Se algum dado ficar com ID diferente de 1000, substitua nos exemplos. Se o script original do grupo já tiver rodado, não o execute novamente.
- O módulo não envia WhatsApp real e não calcula IA. Essas funcionalidades não devem ser demonstradas como prontas.
