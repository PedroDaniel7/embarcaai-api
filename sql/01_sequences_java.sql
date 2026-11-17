-- Execute DEPOIS do script embarcaai_script.sql original do grupo.
-- As sequences nao existem no SQL de Banco; a API Java precisa delas.
-- START WITH 1000 evita conflito com os IDs 1 a 15 do banco de exemplo.
-- Se algum ID ja for >= 1000, ajustar START WITH para MAX(ID) + 1.
-- Este script deve ser executado UMA UNICA VEZ no mesmo schema Oracle.

CREATE SEQUENCE SQ_TB_PACIENTE START WITH 1000 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE SQ_TB_CONSULTA START WITH 1000 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE SQ_TB_VIAGEM START WITH 1000 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE SQ_TB_RESERVA_ASSENTO START WITH 1000 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE SQ_TB_LEMBRETE START WITH 1000 INCREMENT BY 1 NOCACHE;

-- Nao criar novamente as tabelas: a API utiliza as tabelas originais PACIENTE,
-- CONSULTA, VIAGEM, RESERVA_ASSENTO, VAN, UNIDADE_SAUDE e LEMBRETE.
