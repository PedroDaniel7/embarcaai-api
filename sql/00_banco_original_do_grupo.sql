-- ============================================================
-- EmbarcaAi - Global Solution 2026/2 - Building Relational Database
-- Ordem: 1) tabelas  2) 60 INSERTs  3) 10 consultas  4) UPDATE  5) DELETE
-- ============================================================

-- ============================================================
-- 1) TABELAS (ordem das chaves estrangeiras)
-- ============================================================

CREATE TABLE UNIDADE_SAUDE (
  id_unidade NUMBER(4) CONSTRAINT pk_unidade_saude PRIMARY KEY,
  nome VARCHAR2(80) NOT NULL,
  tipo VARCHAR2(20) NOT NULL,
  cidade VARCHAR2(60) NOT NULL,
  CONSTRAINT ck_unidade_tipo CHECK (tipo IN ('UBS','CLINICA','HOSPITAL'))
);

COMMENT ON TABLE UNIDADE_SAUDE IS 'Unidades de saude onde as consultas acontecem (UBS, clinica ou hospital).';

CREATE TABLE PACIENTE (
  id_paciente NUMBER(6) CONSTRAINT pk_paciente PRIMARY KEY,
  nome VARCHAR2(100) NOT NULL,
  telefone VARCHAR2(15) NOT NULL,
  data_nascimento DATE NOT NULL,
  zona_rural CHAR(1) DEFAULT 'N' NOT NULL,
  CONSTRAINT uk_paciente_telefone UNIQUE (telefone),
  CONSTRAINT ck_paciente_rural CHECK (zona_rural IN ('S','N'))
);

COMMENT ON TABLE PACIENTE IS 'Pacientes atendidos pela Secretaria de Saude; o telefone e usado para o aviso por WhatsApp.';

CREATE TABLE VAN (
  id_van NUMBER(4) CONSTRAINT pk_van PRIMARY KEY,
  placa VARCHAR2(8) NOT NULL,
  capacidade NUMBER(2) NOT NULL,
  CONSTRAINT uk_van_placa UNIQUE (placa),
  CONSTRAINT ck_van_capacidade CHECK (capacidade > 0)
);

COMMENT ON TABLE VAN IS 'Vans da prefeitura usadas para levar pacientes as consultas.';

CREATE TABLE CONSULTA (
  id_consulta NUMBER(6) CONSTRAINT pk_consulta PRIMARY KEY,
  id_paciente NUMBER(6) NOT NULL,
  id_unidade NUMBER(4) NOT NULL,
  especialidade VARCHAR2(40) NOT NULL,
  data_hora DATE NOT NULL,
  precisa_transporte CHAR(1) DEFAULT 'N' NOT NULL,
  status VARCHAR2(15) NOT NULL,
  CONSTRAINT ck_consulta_transporte CHECK (precisa_transporte IN ('S','N')),
  CONSTRAINT ck_consulta_status CHECK (status IN ('AGENDADA','CONFIRMADA','CANCELADA','REALIZADA','FALTOU')),
  CONSTRAINT fk_consulta_paciente FOREIGN KEY (id_paciente) REFERENCES PACIENTE (id_paciente),
  CONSTRAINT fk_consulta_unidade FOREIGN KEY (id_unidade) REFERENCES UNIDADE_SAUDE (id_unidade)
);

COMMENT ON TABLE CONSULTA IS 'Consultas agendadas; guarda o status da confirmacao feita pelo paciente.';

CREATE TABLE LEMBRETE (
  id_lembrete NUMBER(6) CONSTRAINT pk_lembrete PRIMARY KEY,
  id_consulta NUMBER(6) NOT NULL,
  data_envio DATE NOT NULL,
  resposta VARCHAR2(15) NOT NULL,
  CONSTRAINT ck_lembrete_resposta CHECK (resposta IN ('CONFIRMOU','CANCELOU','SEM_RESPOSTA')),
  CONSTRAINT fk_lembrete_consulta FOREIGN KEY (id_consulta) REFERENCES CONSULTA (id_consulta)
);

COMMENT ON TABLE LEMBRETE IS 'Lembretes enviados por WhatsApp e a resposta do paciente.';

CREATE TABLE VIAGEM (
  id_viagem NUMBER(5) CONSTRAINT pk_viagem PRIMARY KEY,
  id_van NUMBER(4) NOT NULL,
  id_unidade_destino NUMBER(4) NOT NULL,
  data_hora_saida DATE NOT NULL,
  status VARCHAR2(15) NOT NULL,
  CONSTRAINT ck_viagem_status CHECK (status IN ('PROGRAMADA','REALIZADA','CANCELADA')),
  CONSTRAINT fk_viagem_van FOREIGN KEY (id_van) REFERENCES VAN (id_van),
  CONSTRAINT fk_viagem_unidade FOREIGN KEY (id_unidade_destino) REFERENCES UNIDADE_SAUDE (id_unidade)
);

COMMENT ON TABLE VIAGEM IS 'Viagens da van ate a unidade de saude, com data e hora de saida.';

CREATE TABLE RESERVA_ASSENTO (
  id_reserva NUMBER(6) CONSTRAINT pk_reserva_assento PRIMARY KEY,
  id_consulta NUMBER(6) NOT NULL,
  id_viagem NUMBER(5) NOT NULL,
  status_transporte VARCHAR2(15) NOT NULL,
  CONSTRAINT ck_reserva_status CHECK (status_transporte IN ('PENDENTE','CONFIRMADO','CANCELADO')),
  CONSTRAINT fk_reserva_consulta FOREIGN KEY (id_consulta) REFERENCES CONSULTA (id_consulta),
  CONSTRAINT fk_reserva_viagem FOREIGN KEY (id_viagem) REFERENCES VIAGEM (id_viagem),
  CONSTRAINT uk_reserva_consulta_viagem UNIQUE (id_consulta, id_viagem)
);

COMMENT ON TABLE RESERVA_ASSENTO IS 'Tabela associativa N:N entre CONSULTA e VIAGEM; guarda o assento reservado e a confirmacao do transporte.';

-- ============================================================
-- 2) INSERTs - 60 registros (4 unidades, 12 pacientes, 3 vans, 15 consultas, 10 lembretes, 8 viagens, 8 reservas)
-- ============================================================

-- UNIDADE_SAUDE
INSERT INTO UNIDADE_SAUDE (id_unidade, nome, tipo, cidade) VALUES (1, 'UBS Centro', 'UBS', 'Vale Verde');
INSERT INTO UNIDADE_SAUDE (id_unidade, nome, tipo, cidade) VALUES (2, 'UBS Rural Santa Luzia', 'UBS', 'Vale Verde');
INSERT INTO UNIDADE_SAUDE (id_unidade, nome, tipo, cidade) VALUES (3, 'Clinica Regional de Especialidades', 'CLINICA', 'Serra Alta');
INSERT INTO UNIDADE_SAUDE (id_unidade, nome, tipo, cidade) VALUES (4, 'Hospital Municipal Sao Lucas', 'HOSPITAL', 'Serra Alta');

-- PACIENTE
INSERT INTO PACIENTE (id_paciente, nome, telefone, data_nascimento, zona_rural) VALUES (1, 'Maria das Gracas Silva', '(11) 99000-0001', TO_DATE('1958-03-12','YYYY-MM-DD'), 'S');
INSERT INTO PACIENTE (id_paciente, nome, telefone, data_nascimento, zona_rural) VALUES (2, 'Jose Carlos Pereira', '(11) 99000-0002', TO_DATE('1949-07-25','YYYY-MM-DD'), 'S');
INSERT INTO PACIENTE (id_paciente, nome, telefone, data_nascimento, zona_rural) VALUES (3, 'Ana Paula Souza', '(11) 99000-0003', TO_DATE('1990-11-02','YYYY-MM-DD'), 'N');
INSERT INTO PACIENTE (id_paciente, nome, telefone, data_nascimento, zona_rural) VALUES (4, 'Antonio Ferreira Lima', '(11) 99000-0004', TO_DATE('1965-01-30','YYYY-MM-DD'), 'S');
INSERT INTO PACIENTE (id_paciente, nome, telefone, data_nascimento, zona_rural) VALUES (5, 'Francisca Oliveira', '(11) 99000-0005', TO_DATE('1972-09-18','YYYY-MM-DD'), 'S');
INSERT INTO PACIENTE (id_paciente, nome, telefone, data_nascimento, zona_rural) VALUES (6, 'Joao Batista Santos', '(11) 99000-0006', TO_DATE('1983-05-07','YYYY-MM-DD'), 'N');
INSERT INTO PACIENTE (id_paciente, nome, telefone, data_nascimento, zona_rural) VALUES (7, 'Lucia Helena Costa', '(11) 99000-0007', TO_DATE('1955-12-21','YYYY-MM-DD'), 'S');
INSERT INTO PACIENTE (id_paciente, nome, telefone, data_nascimento, zona_rural) VALUES (8, 'Pedro Henrique Alves', '(11) 99000-0008', TO_DATE('2001-04-14','YYYY-MM-DD'), 'N');
INSERT INTO PACIENTE (id_paciente, nome, telefone, data_nascimento, zona_rural) VALUES (9, 'Rita de Cassia Rocha', '(11) 99000-0009', TO_DATE('1968-08-09','YYYY-MM-DD'), 'S');
INSERT INTO PACIENTE (id_paciente, nome, telefone, data_nascimento, zona_rural) VALUES (10, 'Carlos Eduardo Mendes', '(11) 99000-0010', TO_DATE('1977-02-27','YYYY-MM-DD'), 'N');
INSERT INTO PACIENTE (id_paciente, nome, telefone, data_nascimento, zona_rural) VALUES (11, 'Josefa Maria Barbosa', '(11) 99000-0011', TO_DATE('1944-06-03','YYYY-MM-DD'), 'S');
INSERT INTO PACIENTE (id_paciente, nome, telefone, data_nascimento, zona_rural) VALUES (12, 'Marcos Vinicius Ribeiro', '(11) 99000-0012', TO_DATE('1995-10-16','YYYY-MM-DD'), 'N');

-- VAN
INSERT INTO VAN (id_van, placa, capacidade) VALUES (1, 'ABC1D23', 12);
INSERT INTO VAN (id_van, placa, capacidade) VALUES (2, 'EFG4H56', 8);
INSERT INTO VAN (id_van, placa, capacidade) VALUES (3, 'IJK7L89', 15);

-- CONSULTA
INSERT INTO CONSULTA (id_consulta, id_paciente, id_unidade, especialidade, data_hora, precisa_transporte, status) VALUES (1, 1, 4, 'Cardiologia', TO_DATE('2026-11-23 08:00','YYYY-MM-DD HH24:MI'), 'S', 'CONFIRMADA');
INSERT INTO CONSULTA (id_consulta, id_paciente, id_unidade, especialidade, data_hora, precisa_transporte, status) VALUES (2, 2, 4, 'Cardiologia', TO_DATE('2026-11-23 09:00','YYYY-MM-DD HH24:MI'), 'S', 'CONFIRMADA');
INSERT INTO CONSULTA (id_consulta, id_paciente, id_unidade, especialidade, data_hora, precisa_transporte, status) VALUES (3, 3, 1, 'Clinica Geral', TO_DATE('2026-11-20 10:00','YYYY-MM-DD HH24:MI'), 'N', 'CONFIRMADA');
INSERT INTO CONSULTA (id_consulta, id_paciente, id_unidade, especialidade, data_hora, precisa_transporte, status) VALUES (4, 4, 3, 'Oftalmologia', TO_DATE('2026-11-24 08:30','YYYY-MM-DD HH24:MI'), 'S', 'CONFIRMADA');
INSERT INTO CONSULTA (id_consulta, id_paciente, id_unidade, especialidade, data_hora, precisa_transporte, status) VALUES (5, 5, 3, 'Dermatologia', TO_DATE('2026-11-24 09:30','YYYY-MM-DD HH24:MI'), 'S', 'CANCELADA');
INSERT INTO CONSULTA (id_consulta, id_paciente, id_unidade, especialidade, data_hora, precisa_transporte, status) VALUES (6, 6, 3, 'Ortopedia', TO_DATE('2026-11-24 10:30','YYYY-MM-DD HH24:MI'), 'N', 'CANCELADA');
INSERT INTO CONSULTA (id_consulta, id_paciente, id_unidade, especialidade, data_hora, precisa_transporte, status) VALUES (7, 7, 3, 'Oftalmologia', TO_DATE('2026-11-25 08:00','YYYY-MM-DD HH24:MI'), 'S', 'CANCELADA');
INSERT INTO CONSULTA (id_consulta, id_paciente, id_unidade, especialidade, data_hora, precisa_transporte, status) VALUES (8, 8, 1, 'Clinica Geral', TO_DATE('2026-11-21 14:00','YYYY-MM-DD HH24:MI'), 'N', 'AGENDADA');
INSERT INTO CONSULTA (id_consulta, id_paciente, id_unidade, especialidade, data_hora, precisa_transporte, status) VALUES (9, 9, 4, 'Endocrinologia', TO_DATE('2026-11-26 07:30','YYYY-MM-DD HH24:MI'), 'S', 'AGENDADA');
INSERT INTO CONSULTA (id_consulta, id_paciente, id_unidade, especialidade, data_hora, precisa_transporte, status) VALUES (10, 10, 2, 'Clinica Geral', TO_DATE('2026-11-10 08:00','YYYY-MM-DD HH24:MI'), 'N', 'FALTOU');
INSERT INTO CONSULTA (id_consulta, id_paciente, id_unidade, especialidade, data_hora, precisa_transporte, status) VALUES (11, 11, 4, 'Cardiologia', TO_DATE('2026-11-10 09:00','YYYY-MM-DD HH24:MI'), 'S', 'FALTOU');
INSERT INTO CONSULTA (id_consulta, id_paciente, id_unidade, especialidade, data_hora, precisa_transporte, status) VALUES (12, 12, 1, 'Clinica Geral', TO_DATE('2026-11-11 15:00','YYYY-MM-DD HH24:MI'), 'N', 'FALTOU');
INSERT INTO CONSULTA (id_consulta, id_paciente, id_unidade, especialidade, data_hora, precisa_transporte, status) VALUES (13, 1, 4, 'Cardiologia', TO_DATE('2026-11-03 08:00','YYYY-MM-DD HH24:MI'), 'S', 'REALIZADA');
INSERT INTO CONSULTA (id_consulta, id_paciente, id_unidade, especialidade, data_hora, precisa_transporte, status) VALUES (14, 2, 2, 'Clinica Geral', TO_DATE('2026-11-04 09:00','YYYY-MM-DD HH24:MI'), 'S', 'REALIZADA');
INSERT INTO CONSULTA (id_consulta, id_paciente, id_unidade, especialidade, data_hora, precisa_transporte, status) VALUES (15, 9, 3, 'Oftalmologia', TO_DATE('2026-11-05 10:00','YYYY-MM-DD HH24:MI'), 'S', 'REALIZADA');

-- LEMBRETE
INSERT INTO LEMBRETE (id_lembrete, id_consulta, data_envio, resposta) VALUES (1, 1, TO_DATE('2026-11-20 09:00','YYYY-MM-DD HH24:MI'), 'CONFIRMOU');
INSERT INTO LEMBRETE (id_lembrete, id_consulta, data_envio, resposta) VALUES (2, 2, TO_DATE('2026-11-20 09:00','YYYY-MM-DD HH24:MI'), 'CONFIRMOU');
INSERT INTO LEMBRETE (id_lembrete, id_consulta, data_envio, resposta) VALUES (3, 3, TO_DATE('2026-11-19 09:00','YYYY-MM-DD HH24:MI'), 'CONFIRMOU');
INSERT INTO LEMBRETE (id_lembrete, id_consulta, data_envio, resposta) VALUES (4, 4, TO_DATE('2026-11-21 09:00','YYYY-MM-DD HH24:MI'), 'CONFIRMOU');
INSERT INTO LEMBRETE (id_lembrete, id_consulta, data_envio, resposta) VALUES (5, 5, TO_DATE('2026-11-21 09:00','YYYY-MM-DD HH24:MI'), 'CANCELOU');
INSERT INTO LEMBRETE (id_lembrete, id_consulta, data_envio, resposta) VALUES (6, 6, TO_DATE('2026-11-21 09:00','YYYY-MM-DD HH24:MI'), 'CANCELOU');
INSERT INTO LEMBRETE (id_lembrete, id_consulta, data_envio, resposta) VALUES (7, 7, TO_DATE('2026-11-22 09:00','YYYY-MM-DD HH24:MI'), 'CANCELOU');
INSERT INTO LEMBRETE (id_lembrete, id_consulta, data_envio, resposta) VALUES (8, 10, TO_DATE('2026-11-07 09:00','YYYY-MM-DD HH24:MI'), 'SEM_RESPOSTA');
INSERT INTO LEMBRETE (id_lembrete, id_consulta, data_envio, resposta) VALUES (9, 11, TO_DATE('2026-11-07 09:00','YYYY-MM-DD HH24:MI'), 'SEM_RESPOSTA');
INSERT INTO LEMBRETE (id_lembrete, id_consulta, data_envio, resposta) VALUES (10, 12, TO_DATE('2026-11-08 09:00','YYYY-MM-DD HH24:MI'), 'SEM_RESPOSTA');

-- VIAGEM
INSERT INTO VIAGEM (id_viagem, id_van, id_unidade_destino, data_hora_saida, status) VALUES (1, 1, 4, TO_DATE('2026-11-23 05:30','YYYY-MM-DD HH24:MI'), 'PROGRAMADA');
INSERT INTO VIAGEM (id_viagem, id_van, id_unidade_destino, data_hora_saida, status) VALUES (2, 1, 3, TO_DATE('2026-11-24 06:00','YYYY-MM-DD HH24:MI'), 'PROGRAMADA');
INSERT INTO VIAGEM (id_viagem, id_van, id_unidade_destino, data_hora_saida, status) VALUES (3, 2, 3, TO_DATE('2026-11-25 06:00','YYYY-MM-DD HH24:MI'), 'PROGRAMADA');
INSERT INTO VIAGEM (id_viagem, id_van, id_unidade_destino, data_hora_saida, status) VALUES (4, 1, 4, TO_DATE('2026-11-26 05:30','YYYY-MM-DD HH24:MI'), 'PROGRAMADA');
INSERT INTO VIAGEM (id_viagem, id_van, id_unidade_destino, data_hora_saida, status) VALUES (5, 1, 4, TO_DATE('2026-11-03 05:30','YYYY-MM-DD HH24:MI'), 'REALIZADA');
INSERT INTO VIAGEM (id_viagem, id_van, id_unidade_destino, data_hora_saida, status) VALUES (6, 2, 2, TO_DATE('2026-11-04 07:00','YYYY-MM-DD HH24:MI'), 'REALIZADA');
INSERT INTO VIAGEM (id_viagem, id_van, id_unidade_destino, data_hora_saida, status) VALUES (7, 2, 3, TO_DATE('2026-11-05 06:30','YYYY-MM-DD HH24:MI'), 'REALIZADA');
INSERT INTO VIAGEM (id_viagem, id_van, id_unidade_destino, data_hora_saida, status) VALUES (8, 1, 4, TO_DATE('2026-11-10 05:30','YYYY-MM-DD HH24:MI'), 'CANCELADA');

-- RESERVA_ASSENTO
INSERT INTO RESERVA_ASSENTO (id_reserva, id_consulta, id_viagem, status_transporte) VALUES (1, 1, 1, 'CONFIRMADO');
INSERT INTO RESERVA_ASSENTO (id_reserva, id_consulta, id_viagem, status_transporte) VALUES (2, 4, 2, 'CONFIRMADO');
INSERT INTO RESERVA_ASSENTO (id_reserva, id_consulta, id_viagem, status_transporte) VALUES (3, 5, 2, 'CANCELADO');
INSERT INTO RESERVA_ASSENTO (id_reserva, id_consulta, id_viagem, status_transporte) VALUES (4, 7, 3, 'CANCELADO');
INSERT INTO RESERVA_ASSENTO (id_reserva, id_consulta, id_viagem, status_transporte) VALUES (5, 9, 4, 'PENDENTE');
INSERT INTO RESERVA_ASSENTO (id_reserva, id_consulta, id_viagem, status_transporte) VALUES (6, 13, 5, 'CONFIRMADO');
INSERT INTO RESERVA_ASSENTO (id_reserva, id_consulta, id_viagem, status_transporte) VALUES (7, 14, 6, 'CONFIRMADO');
INSERT INTO RESERVA_ASSENTO (id_reserva, id_consulta, id_viagem, status_transporte) VALUES (8, 15, 7, 'CONFIRMADO');

COMMIT;

-- ============================================================
-- 3) CONSULTAS (10, numeradas)
-- ============================================================

-- CONSULTA 1: Quais consultas estao confirmadas e precisam de transporte?
-- Recurso: WHERE
SELECT c.id_consulta, p.nome AS paciente, c.especialidade, c.data_hora
FROM CONSULTA c
INNER JOIN PACIENTE p ON p.id_paciente = c.id_paciente
WHERE c.status = 'CONFIRMADA'
  AND c.precisa_transporte = 'S';

-- CONSULTA 2: Qual a lista de consultas por data, com o paciente e a unidade?
-- Recurso: ORDER BY e INNER JOIN
SELECT c.data_hora, p.nome AS paciente, u.nome AS unidade, c.especialidade, c.status
FROM CONSULTA c
INNER JOIN PACIENTE p ON p.id_paciente = c.id_paciente
INNER JOIN UNIDADE_SAUDE u ON u.id_unidade = c.id_unidade
ORDER BY c.data_hora;

-- CONSULTA 3: Quantas consultas existem em cada status?
-- Recurso: COUNT e GROUP BY
SELECT status, COUNT(*) AS total
FROM CONSULTA
GROUP BY status
ORDER BY total DESC;

-- CONSULTA 4: Qual a taxa de faltas por unidade de saude?
-- Recurso: SUM, calculo e GROUP BY
SELECT u.nome AS unidade,
       COUNT(*) AS total_consultas,
       SUM(CASE WHEN c.status = 'FALTOU' THEN 1 ELSE 0 END) AS faltas,
       ROUND(SUM(CASE WHEN c.status = 'FALTOU' THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 1) AS taxa_faltas_pct
FROM CONSULTA c
INNER JOIN UNIDADE_SAUDE u ON u.id_unidade = c.id_unidade
GROUP BY u.nome
ORDER BY taxa_faltas_pct DESC;

-- CONSULTA 5: Qual a idade media, a menor e a maior idade dos pacientes que faltaram?
-- Recurso: AVG, MIN e MAX
SELECT COUNT(*) AS total_faltas,
       ROUND(AVG(TRUNC(MONTHS_BETWEEN(SYSDATE, p.data_nascimento) / 12)), 1) AS idade_media,
       MIN(TRUNC(MONTHS_BETWEEN(SYSDATE, p.data_nascimento) / 12)) AS idade_minima,
       MAX(TRUNC(MONTHS_BETWEEN(SYSDATE, p.data_nascimento) / 12)) AS idade_maxima
FROM CONSULTA c
INNER JOIN PACIENTE p ON p.id_paciente = c.id_paciente
WHERE c.status = 'FALTOU';

-- CONSULTA 6: Quais unidades de saude tem mais de 2 consultas canceladas?
-- Recurso: HAVING
SELECT u.nome AS unidade, COUNT(*) AS canceladas
FROM CONSULTA c
INNER JOIN UNIDADE_SAUDE u ON u.id_unidade = c.id_unidade
WHERE c.status = 'CANCELADA'
GROUP BY u.nome
HAVING COUNT(*) > 2;

-- CONSULTA 7: Quantos assentos estao reservados e quantos estao livres em cada viagem?
-- Recurso: SUM, calculo e LEFT JOIN
SELECT v.id_viagem, v.data_hora_saida, va.capacidade,
       SUM(CASE WHEN r.status_transporte <> 'CANCELADO' THEN 1 ELSE 0 END) AS reservados,
       va.capacidade - SUM(CASE WHEN r.status_transporte <> 'CANCELADO' THEN 1 ELSE 0 END) AS livres
FROM VIAGEM v
INNER JOIN VAN va ON va.id_van = v.id_van
LEFT JOIN RESERVA_ASSENTO r ON r.id_viagem = v.id_viagem
GROUP BY v.id_viagem, v.data_hora_saida, va.capacidade
ORDER BY v.id_viagem;

-- CONSULTA 8: Quais pacientes deixaram algum lembrete sem resposta?
-- Recurso: Subconsulta
SELECT p.id_paciente, p.nome, p.telefone
FROM PACIENTE p
WHERE p.id_paciente IN (SELECT c.id_paciente
                        FROM CONSULTA c
                        INNER JOIN LEMBRETE l ON l.id_consulta = c.id_consulta
                        WHERE l.resposta = 'SEM_RESPOSTA');

-- CONSULTA 9: Quais consultas que precisam de transporte ainda nao tem reserva de assento?
-- Recurso: LEFT JOIN
SELECT c.id_consulta, c.especialidade, c.data_hora, c.status
FROM CONSULTA c
LEFT JOIN RESERVA_ASSENTO r ON r.id_consulta = c.id_consulta
WHERE c.precisa_transporte = 'S'
  AND c.status IN ('AGENDADA', 'CONFIRMADA')
  AND r.id_reserva IS NULL;

-- CONSULTA 10: Quais vans nunca foram usadas em viagens?
-- Recurso: RIGHT JOIN
SELECT va.id_van, va.placa, va.capacidade
FROM VIAGEM v
RIGHT JOIN VAN va ON va.id_van = v.id_van
WHERE v.id_viagem IS NULL;

-- ============================================================
-- 4) UPDATE (com WHERE)
-- ============================================================

-- Paciente 8 confirmou a consulta apos o lembrete
UPDATE CONSULTA SET status = 'CONFIRMADA' WHERE id_consulta = 8;

-- Funcionario confirmou o transporte da reserva 5
UPDATE RESERVA_ASSENTO SET status_transporte = 'CONFIRMADO' WHERE id_reserva = 5;

COMMIT;

-- ============================================================
-- 5) DELETE (com WHERE)
-- ============================================================

-- Remove a reserva cancelada 4
DELETE FROM RESERVA_ASSENTO WHERE id_reserva = 4;

COMMIT;
