-- ======================================================
-- 1.CONFIGURAÇÃO INICIAL E CRIAÇÃO DO BANCO
-- ======================================================
CREATE DATABASE IF NOT EXISTS clinica_medica;
USE clinica_medica;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS prontuarios;
DROP TABLE IF EXISTS consultas;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS pacientes;
DROP TABLE IF EXISTS medicos;
DROP TABLE IF EXISTS especialidades;
DROP VIEW IF EXISTS vw_historico_prontuarios;

SET FOREIGN_KEY_CHECKS = 1;

-- ======================================================
-- 2. CRIAÇÃO DAS TABELAS
-- ======================================================

CREATE TABLE especialidades (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao TEXT
);

CREATE TABLE medicos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(250) NOT NULL,
    crm VARCHAR(20) NOT NULL UNIQUE,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    telefone VARCHAR(20) NOT NULL,
    especialidade_id INT NOT NULL,
    sala VARCHAR(50) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (especialidade_id) REFERENCES especialidades(id) ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE pacientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(250) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    email VARCHAR(100) UNIQUE NULL,
    telefone VARCHAR(20) NOT NULL,
    data_nascimento DATE NOT NULL,
    numero_utente VARCHAR(20) UNIQUE NULL,
    nome_responsavel VARCHAR(250) NULL, 
    alergias VARCHAR(255) NULL,
    medicacao_habitual TEXT NULL,
    data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP,
    data_registo DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    perfil ENUM('ADMIN', 'RECEPCAO', 'MEDICO') NOT NULL DEFAULT 'RECEPCAO',
    medico_id INT NULL, 
    status ENUM('ATIVO', 'INATIVO') NOT NULL DEFAULT 'ATIVO',
    ultimo_acesso DATETIME,
    FOREIGN KEY (medico_id) REFERENCES medicos(id) ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE consultas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    paciente_id INT NOT NULL,
    medico_id INT NOT NULL,
    data_consulta DATE NOT NULL, 
    hora_consulta TIME NOT NULL,
    status ENUM('AGENDADA', 'CONFIRMADA', 'EM_ANDAMENTO', 'FINALIZADA', 'CANCELADA') NOT NULL DEFAULT 'AGENDADA',
    observacao_cancelamento TEXT NULL,
    FOREIGN KEY (paciente_id) REFERENCES pacientes(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (medico_id) REFERENCES medicos(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    UNIQUE(medico_id, data_consulta, hora_consulta) 
);

CREATE TABLE prontuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    consulta_id INT NOT NULL UNIQUE,
    medico_id INT NOT NULL, 
    tensao_arterial VARCHAR(20) NULL,
    frequencia_cardiaca INT NULL,
    temperatura DECIMAL(4,2) NULL,
    observacoes_clinicas TEXT NOT NULL,
    prescricoes TEXT,
    data_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (consulta_id) REFERENCES consultas(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (medico_id) REFERENCES medicos(id) ON UPDATE CASCADE ON DELETE RESTRICT
);

-- ======================================================
-- 3. INSERÇÃO DE DADOS
-- ======================================================

INSERT INTO especialidades (nome, descricao) VALUES  
('Cardiologia', 'Tratamento e diagnóstico de doenças do coração'),
('Pediatria', 'Assistência médica a crianças e adolescentes'),
('Ortopedia', 'Tratamento de doenças e lesões nos ossos e músculos'),
('Clínica Geral', 'Atendimento primário e diagnósticos gerais'),
('Dermatologia', 'Cuidados com a pele, cabelos e unhas'),
('Oftalmologia', 'Diagnóstico e tratamento de doenças dos olhos'),
('Neurologia', 'Diagnóstico e tratamento de distúrbios do sistema nervoso'),
('Ginecologia', 'Saúde do sistema reprodutor feminino e mamas');

INSERT INTO medicos (nome, crm, cpf, email, telefone, especialidade_id, sala, ativo) VALUES
('Dr. Roberto Alves', 'CRM-12345', '11122233344', 'roberto.alves@clinica.com', '11999991111', 1, 'Consultório 01', TRUE),
('Dra. Fernanda Lima', 'CRM-54321', '22233344455', 'fernanda.lima@clinica.com', '11988882222', 2, 'Consultório 02', TRUE),
('Dr. Carlos Mendes', 'CRM-98765', '33344455666', 'carlos.mendes@clinica.com', '11977773333', 3, 'Consultório 03', TRUE),
('Dra. Juliana Silva', 'CRM-56789', '44455666777', 'juliana.silva@clinica.com', '11966664444', 4, 'Consultório 04', TRUE),
('Dr. Marcos Vinicius', 'CRM-43210', '55666777888', 'marcos.vinicius@clinica.com', '11955555555', 5, 'Consultório 05', TRUE),
('Dra. Patricia Rocha', 'CRM-67890', '66777888999', 'patricia.rocha@clinica.com', '11944446666', 6, 'Consultório 06', TRUE),
('Dr. Eduardo Souza', 'CRM-11223', '77888999000', 'eduardo.souza@clinica.com', '11933337777', 7, 'Consultório 07', TRUE),
('Dra. Camila Ribeiro', 'CRM-44556', '88999000111', 'camila.ribeiro@clinica.com', '11922228888', 8, 'Consultório 08', TRUE);

INSERT INTO pacientes (
    nome, cpf, email, telefone, data_nascimento, numero_utente, nome_responsavel, alergias, medicacao_habitual, data_cadastro
) VALUES
('Ana Martins', '11122233344', 'ana.martins@email.com', '912 345 601', '1988-03-14', '900001024', NULL, 'Penicilina', 'Sem medicação habitual', '2026-10-10 12:00:00'),
('Marcos Paulo Dias', '22233344455', 'marcos.dias@email.com', '11923456789', '1985-10-22', '234567890', NULL, NULL, 'Losartana 50mg', '2026-10-10 12:00:00'),
('Lucas Martins', '33344455566', NULL, '11934567890', '2015-08-10', '345678901', 'Camila Martins (Mãe)', 'Dipirona', NULL, '2026-10-10 12:00:00'),
('Beatriz Costa', '44455566677', 'beatriz.costa@email.com', '11945678901', '1998-02-03', '456789012', NULL, NULL, 'Anticoncepcional', '2026-10-10 12:00:00'),
('João Pedro Santos', '55566677888', 'joao.santos@email.com', '11956789012', '1990-05-12', '567890123', NULL, 'Sulfamida', 'Vitamina C', '2026-10-11 09:00:00'),
('Mariana Oliveira', '66677788999', 'mariana.oliveira@email.com', '11967890123', '1975-12-04', '678901234', NULL, NULL, 'Omeprazol 20mg', '2026-10-11 09:30:00'),
('Gabriel Souza', '77788899000', 'gabriel.souza@email.com', '11978901234', '2002-07-19', '789012345', NULL, 'Látex', NULL, '2026-10-11 10:00:00'),
('Larissa Mendes', '88899900111', 'larissa.mendes@email.com', '11989012345', '1995-09-30', '890123456', NULL, NULL, 'Sertralina 50mg', '2026-10-11 10:30:00'),
('Bruno Henrique', '99900011222', 'bruno.henrique@email.com', '11990123456', '1982-04-15', '901234567', NULL, NULL, 'Metformina 850mg', '2026-10-12 08:00:00'),
('Camila Rocha', '12345098765', 'camila.rocha@email.com', '11901234567', '1993-11-25', '012345678', NULL, 'Ibuprofeno', 'Contraceptivo', '2026-10-12 08:30:00');

INSERT INTO usuarios (username, senha, perfil, medico_id, status) VALUES
('admin', '$2a$12$nv/cbRZk8QSN2ckYLsNp2eWAv8v8MmsuDTEw5DnF4lCa88S1cqxVC', 'ADMIN', NULL, 'ATIVO'),
('recepcao01', '$2a$12$nv/cbRZk8QSN2ckYLsNp2eWAv8v8MmsuDTEw5DnF4lCa88S1cqxVC', 'RECEPCAO', NULL, 'ATIVO'),
('recepcao02', '$2a$12$nv/cbRZk8QSN2ckYLsNp2eWAv8v8MmsuDTEw5DnF4lCa88S1cqxVC', 'RECEPCAO', NULL, 'ATIVO'),
('dr.roberto', '$2a$12$nv/cbRZk8QSN2ckYLsNp2eWAv8v8MmsuDTEw5DnF4lCa88S1cqxVC', 'MEDICO', 1, 'ATIVO'),
('dra.fernanda', '$2a$12$nv/cbRZk8QSN2ckYLsNp2eWAv8v8MmsuDTEw5DnF4lCa88S1cqxVC', 'MEDICO', 2, 'ATIVO'),
('dr.carlos', '$2a$12$nv/cbRZk8QSN2ckYLsNp2eWAv8v8MmsuDTEw5DnF4lCa88S1cqxVC', 'MEDICO', 3, 'ATIVO'),
('dra.juliana', '$2a$12$nv/cbRZk8QSN2ckYLsNp2eWAv8v8MmsuDTEw5DnF4lCa88S1cqxVC', 'MEDICO', 4, 'ATIVO'),
('dr.marcos', '$2a$12$nv/cbRZk8QSN2ckYLsNp2eWAv8v8MmsuDTEw5DnF4lCa88S1cqxVC', 'MEDICO', 5, 'ATIVO'),
('dra.patricia', '$2a$12$nv/cbRZk8QSN2ckYLsNp2eWAv8v8MmsuDTEw5DnF4lCa88S1cqxVC', 'MEDICO', 6, 'ATIVO'),
('dr.eduardo', '$2a$12$nv/cbRZk8QSN2ckYLsNp2eWAv8v8MmsuDTEw5DnF4lCa88S1cqxVC', 'MEDICO', 7, 'ATIVO'),
('dra.camila', '$2a$12$nv/cbRZk8QSN2ckYLsNp2eWAv8v8MmsuDTEw5DnF4lCa88S1cqxVC', 'MEDICO', 8, 'ATIVO');

INSERT INTO consultas (paciente_id, medico_id, data_consulta, hora_consulta, status, observacao_cancelamento) VALUES
(1, 1, '2026-10-07', '11:00:00', 'FINALIZADA', NULL),
(1, 1, '2026-10-10', '11:00:00', 'FINALIZADA', NULL),
(1, 1, '2026-10-11', '11:00:00', 'FINALIZADA', NULL),
(1, 1, CURRENT_DATE, '15:00:00', 'AGENDADA', NULL), 
(2, 1, '2026-10-10', '10:30:00', 'CONFIRMADA', NULL),
(3, 1, '2026-10-12', '09:00:00', 'FINALIZADA', NULL),
(4, 1, '2026-10-12', '10:00:00', 'FINALIZADA', NULL),
(5, 1, '2026-10-13', '14:00:00', 'CONFIRMADA', NULL),
(6, 1, '2026-10-13', '15:30:00', 'AGENDADA', NULL),
(7, 1, '2026-10-14', '08:30:00', 'CONFIRMADA', NULL),
(8, 1, '2026-10-14', '09:30:00', 'FINALIZADA', NULL),
(9, 1, '2026-10-15', '11:00:00', 'FINALIZADA', NULL),
(10, 1, '2026-10-15', '13:00:00', 'CANCELADA', 'Paciente solicitou reagendamento por motivos de força maior.'),
(2, 1, '2026-10-16', '16:00:00', 'CONFIRMADA', NULL),
(3, 1, '2026-10-16', '16:30:00', 'AGENDADA', NULL);

INSERT INTO prontuarios (consulta_id, medico_id, tensao_arterial, frequencia_cardiaca, temperatura, observacoes_clinicas, prescricoes) VALUES
(1, 1, '120/80', 75, 36.5, 'Avaliação de cefaleia ligeira. Sem sinais de alarme. Aconselhada hidratação.', 'Paracetamol 500mg se dor'),
(2, 1, '115/75', 72, 36.8, 'Reavaliação de sintomas. Quadro estável. Mantém repouso relativo.', 'Manter hidratação'),
(3, 1, '110/70', 68, 36.6, 'Alta clínica do episódio de cefaleia. Sem queixas atuais.', 'Sem medicação'),
(6, 2, '100/65', 90, 37.1, 'Paciente pediátrico com febre baixa e tosse leve. Infecção viral autolimitada.', 'Ibuprofeno gotas 1 gota/kg em caso de febre'),
(7, 3, '130/85', 78, 36.7, 'Dor lombar aguda decorrente de esforço físico no trabalho.', 'Diclofenaco sódico 50mg de 12 em 12 horas por 3 dias'),
(11, 7, '125/80', 82, 36.5, 'Queixas de formigamento nas extremidades. Solicitada eletroneuromiografia.', 'Vitamina B12 5000mcg'),
(12, 8, '118/76', 74, 36.6, 'Rotina ginecológica sem alterações palpáveis. Exames preventivos solicitados.', 'Ácido fólico 5mg contínuo');