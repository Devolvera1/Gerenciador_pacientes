-- ======================================================
-- 1. CONFIGURAÇÃO INICIAL E CRIAÇÃO DO BANCO
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
('Oftalmologia', 'Diagnóstico e tratamento de doenças dos olhos');

INSERT INTO medicos (nome, crm, email, telefone, especialidade_id, sala, ativo) VALUES
('Dr. Roberto Alves', 'CRM-12345', 'roberto.alves@clinica.com', '11999991111', 1, 'Consultório 01', TRUE),
('Dra. Fernanda Lima', 'CRM-54321', 'fernanda.lima@clinica.com', '11988882222', 2, 'Consultório 02', TRUE),
('Dr. Carlos Mendes', 'CRM-98765', 'carlos.mendes@clinica.com', '11977773333', 3, 'Consultório 03', TRUE),
('Dra. Juliana Silva', 'CRM-56789', 'juliana.silva@clinica.com', '11966664444', 4, 'Consultório 04', TRUE),
('Dr. Marcos Vinicius', 'CRM-43210', 'marcos.vinicius@clinica.com', '11955555555', 5, 'Consultório 05', TRUE),
('Dra. Patricia Rocha', 'CRM-67890', 'patricia.rocha@clinica.com', '11944446666', 6, 'Consultório 06', TRUE);

INSERT INTO pacientes (
    nome, cpf, email, telefone, data_nascimento, numero_utente, nome_responsavel, alergias, data_cadastro
) VALUES
-- Registros Originais (Corrigidos)
('Ana Clara Souza', '11122233344', 'ana.souza@email.com', '11912345678', '1990-05-15', '123456789', NULL, 'Penicilina, Amendoim', '2026-10-10 12:00:00'),
('Marcos Paulo Dias', '22233344455', 'marcos.dias@email.com', '11923456789', '1985-10-22', '234567890', NULL, NULL, '2026-10-10 12:00:00'),
('Lucas Martins', '33344455566', NULL, '11934567890', '2015-08-10', '345678901', 'Camila Martins (Mãe)', 'Dipirona', '2026-10-10 12:00:00'),
('Beatriz Costa', '44455566677', 'beatriz.costa@email.com', '11945678901', '1998-02-03', '456789012', NULL, NULL, '2026-10-10 12:00:00'),
('Gabriel Rocha', '55566677788', 'gabriel.rocha@email.com', '11956789012', '1979-11-30', '567890123', NULL, 'Lácteos', '2026-10-10 12:00:00'),
('Mariana Oliveira', '66677788899', 'mariana.oliveira@email.com', '11967890123', '2001-07-19', '678901234', NULL, NULL, '2026-10-10 12:00:00'),
('João Pedro Ramos', '77788899900', NULL, '11978901234', '2018-03-25', '789012345', 'Sandra Ramos (Mãe)', NULL, '2026-10-10 12:00:00'),
('Camila Duarte', '88899900011', 'camila.duarte@email.com', '11989012345', '1993-12-05', '890123456', NULL, 'Sulfamida', '2026-10-10 12:00:00'),
('Rafael Alves Ribeiro', '99900011122', 'rafael.alves@email.com', '11990123456', '1988-04-12', '901234567', NULL, 'Ibuprofeno', '2026-10-11 08:30:00'),
('Sofia Ferreira Lima', '00011122233', 'sofia.lima@email.com', '11901234567', '2003-09-28', '012345678', NULL, NULL, '2026-10-11 09:15:00'),
('Enzo Gabriel Santos', '12312312344', NULL, '11911223344', '2019-11-04', '112233445', 'Juliana Santos (Mãe)', 'Poeira, Ácaros', '2026-10-11 10:00:00'),
('Larissa Mendes Silva', '23423423455', 'larissa.mendes@email.com', '11922334455', '1995-01-17', '223344556', NULL, NULL, '2026-10-11 11:20:00'),
('Thiago Henrique Prado', '34534534566', 'thiago.prado@email.com', '11933445566', '1972-06-30', '334455667', NULL, 'Frutos do Mar', '2026-10-11 14:00:00'),
('Alice Barbosa Castro', '45645645677', NULL, '11944556677', '2021-02-14', '445566778', 'Roberto Castro (Pai)', NULL, '2026-10-12 08:00:00'),
('Rodrigo Nogueira', '56756756788', 'rodrigo.nogueira@email.com', '11955667788', '1982-08-08', '556677889', NULL, 'Látex', '2026-10-12 09:45:00'),
('Isabela Fernandes', '67867867899', 'isabela.f@email.com', '11966778899', '1999-12-22', '667788990', NULL, NULL, '2026-10-12 11:10:00'),
('Mateus Cardoso Lima', '78978978900', 'mateus.cardoso@email.com', '11977889900', '2005-03-03', '778899001', NULL, 'Contraste Iodado', '2026-10-12 13:30:00'),
('Helena Xavier Gomes', '89089089011', NULL, '11988990011', '2016-07-11', '889900112', 'Vanessa Gomes (Mãe)', 'Aspirina', '2026-10-12 15:00:00');

INSERT INTO usuarios (username, senha, perfil, medico_id, status) VALUES
('admin', '$2a$12$nv/cbRZk8QSN2ckYLsNp2eWAv8v8MmsuDTEw5DnF4lCa88S1cqxVC', 'ADMIN', NULL, 'ATIVO'),
('recepcao01', '$2a$12$nv/cbRZk8QSN2ckYLsNp2eWAv8v8MmsuDTEw5DnF4lCa88S1cqxVC', 'RECEPCAO', NULL, 'ATIVO'),
('recepcao02', '$2a$12$nv/cbRZk8QSN2ckYLsNp2eWAv8v8MmsuDTEw5DnF4lCa88S1cqxVC', 'RECEPCAO', NULL, 'ATIVO'),
('dr.roberto', '$2a$12$nv/cbRZk8QSN2ckYLsNp2eWAv8v8MmsuDTEw5DnF4lCa88S1cqxVC', 'MEDICO', 1, 'ATIVO'),
('dra.fernanda', '$2a$12$nv/cbRZk8QSN2ckYLsNp2eWAv8v8MmsuDTEw5DnF4lCa88S1cqxVC', 'MEDICO', 2, 'ATIVO'),
('dr.carlos', '$2a$12$nv/cbRZk8QSN2ckYLsNp2eWAv8v8MmsuDTEw5DnF4lCa88S1cqxVC', 'MEDICO', 3, 'ATIVO'),
('dra.juliana', '$2a$12$nv/cbRZk8QSN2ckYLsNp2eWAv8v8MmsuDTEw5DnF4lCa88S1cqxVC', 'MEDICO', 4, 'ATIVO'),
('dr.marcos', '$2a$12$nv/cbRZk8QSN2ckYLsNp2eWAv8v8MmsuDTEw5DnF4lCa88S1cqxVC', 'MEDICO', 5, 'ATIVO'),
('dra.patricia', '$2a$12$nv/cbRZk8QSN2ckYLsNp2eWAv8v8MmsuDTEw5DnF4lCa88S1cqxVC', 'MEDICO', 6, 'ATIVO');

INSERT INTO consultas (paciente_id, medico_id, data_consulta, hora_consulta, status, observacao_cancelamento) VALUES
(1, 1, '2026-10-10', '09:00:00', 'FINALIZADA', NULL),
(2, 1, '2026-10-10', '10:30:00', 'CONFIRMADA', NULL),
(3, 1, '2026-10-10', '14:00:00', 'FINALIZADA', NULL),
(4, 1, '2026-10-10', '15:00:00', 'CANCELADA', 'Paciente desmarcou por motivo de viagem.'),
(5, 1, '2026-10-11', '08:00:00', 'AGENDADA', NULL),
(6, 1, '2026-10-11', '09:30:00', 'AGENDADA', NULL),
(7, 1, '2026-10-11', '11:00:00', 'CONFIRMADA', NULL),
(8, 1, '2026-10-12', '14:30:00', 'AGENDADA', NULL),
(1, 1, '2026-10-12', '16:00:00', 'AGENDADA', NULL),
(2, 1, '2026-10-13', '10:00:00', 'AGENDADA', NULL);

INSERT INTO prontuarios (consulta_id, medico_id, observacoes_clinicas, prescricoes) VALUES
(1, 1, 'Paciente relata dores leves no peito ao esforço. Eletrocardiograma sem alterações agudas.', 'AAS 100mg 1x ao dia. Retorno em 30 dias.'),
(3, 2, 'Paciente apresentou febre leve e tosse seca. Exame físico normal nas vias aéreas superiores.', 'Paracetamol 500mg se dor ou febre. Repouso por 3 dias.');