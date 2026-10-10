-- ======================================================
-- 1. CONFIGURAÇÃO INICIAL E CRIAÇÃO DO BANCO
-- ======================================================
CREATE DATABASE IF NOT EXISTS clinica_medica;
USE clinica_medica;

SET FOREIGN_KEY_CHECKS = 0;

-- Limpeza de tabelas para evitar conflitos (Ordem reversa das dependências)
DROP TABLE IF EXISTS prontuarios;
DROP TABLE IF EXISTS consultas;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS pacientes;
DROP TABLE IF EXISTS medicos;
DROP TABLE IF EXISTS especialidades;

SET FOREIGN_KEY_CHECKS = 1;

-- ======================================================
-- 2. CRIAÇÃO DAS TABELAS (Estruturadas na 3FN)
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
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (especialidade_id) REFERENCES especialidades(id) ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE pacientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(250) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    email VARCHAR(100) UNIQUE,
    telefone VARCHAR(20) NOT NULL,
    data_nascimento DATE NOT NULL,
    nome_responsavel VARCHAR(250) NULL, -- Atende à RN03 (Responsável legal para menores)
    data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    perfil ENUM('ADMIN', 'RECEPCAO', 'MEDICO') NOT NULL DEFAULT 'RECEPCAO',
    medico_id INT NULL, -- Vincula o usuário ao médico, se o perfil for 'MEDICO'
    status ENUM('ATIVO', 'INATIVO') NOT NULL DEFAULT 'ATIVO',
    ultimo_acesso DATETIME,
    FOREIGN KEY (medico_id) REFERENCES medicos(id) ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE consultas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    paciente_id INT NOT NULL,
    medico_id INT NOT NULL,
    data_hora DATETIME NOT NULL, -- Atende à RN01 (Validação de conflitos de horário via software)
    status ENUM('AGENDADA', 'CONFIRMADA', 'EM_ANDAMENTO', 'FINALIZADA', 'CANCELADA') NOT NULL DEFAULT 'AGENDADA',
    observacao_cancelamento TEXT NULL,
    FOREIGN KEY (paciente_id) REFERENCES pacientes(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (medico_id) REFERENCES medicos(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    UNIQUE(medico_id, data_hora) -- Impede a mesma data/hora exata para o mesmo médico no banco
);

CREATE TABLE prontuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    consulta_id INT NOT NULL UNIQUE,
    medico_id INT NOT NULL, -- Atende à RN04 (Apenas o médico que atendeu pode registrar)
    observacoes_clinicas TEXT NOT NULL,
    prescricoes TEXT,
    data_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (consulta_id) REFERENCES consultas(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (medico_id) REFERENCES medicos(id) ON UPDATE CASCADE ON DELETE RESTRICT
);

-- ======================================================
-- 3. INSERÇÃO DE DADOS (Carga Inicial para Testes da A3)
-- ======================================================

INSERT INTO especialidades (nome, descricao) VALUES  
('Cardiologia', 'Tratamento e diagnóstico de doenças do coração'),
('Pediatria', 'Assistência médica a crianças e adolescentes'),
('Ortopedia', 'Tratamento de doenças e lesões nos ossos e músculos'),
('Clínica Geral', 'Atendimento primário e diagnósticos gerais');

INSERT INTO medicos (nome, crm, email, telefone, especialidade_id, ativo) VALUES
('Dr. Roberto Alves', 'CRM-12345', 'roberto.alves@clinica.com', '11999991111', 1, TRUE),
('Dra. Fernanda Lima', 'CRM-54321', 'fernanda.lima@clinica.com', '11988882222', 2, TRUE),
('Dr. Carlos Mendes', 'CRM-98765', 'carlos.mendes@clinica.com', '11977773333', 3, TRUE),
('Dra. Juliana Silva', 'CRM-56789', 'juliana.silva@clinica.com', '11966664444', 4, TRUE);

INSERT INTO pacientes (nome, cpf, email, telefone, data_nascimento, nome_responsavel) VALUES
('Ana Clara Souza', '11122233344', 'ana.souza@email.com', '11912345678', '1990-05-15', NULL),
('Marcos Paulo Dias', '22233344455', 'marcos.dias@email.com', '11923456789', '1985-10-22', NULL),
('Lucas Martins', '33344455566', NULL, '11934567890', '2015-08-10', 'Camila Martins (Mãe)'),
('Beatriz Costa', '44455566677', 'beatriz.costa@email.com', '11945678901', '1998-02-03', NULL);

INSERT INTO usuarios (username, senha, perfil, medico_id, status) VALUES
('admin', '$2a$12$nv/cbRZk8QSN2ckYLsNp2eWAv8v8MmsuDTEw5DnF4lCa88S1cqxVC', 'ADMIN', NULL, 'ATIVO'),
('recepcao01', '$2a$10$7R6Bv2BbeZ.q9Eomf1h/8OyNf2B0gE97Aom6b1M6w2i.l8y3aDqS', 'RECEPCAO', NULL, 'ATIVO'),
('dr.roberto', '$2a$10$yR6Cv2BbeZ.q9Eomf1h/8OyNf2B0gE97Aom6b1M6w2i.l8y3aDqS', 'MEDICO', 1, 'ATIVO'),
('dra.fernanda', '$2a$10$yR6Cv2BbeZ.q9Eomf1h/8OyNf2B0gE97Aom6b1M6w2i.l8y3aDqS', 'MEDICO', 2, 'ATIVO');

INSERT INTO consultas (paciente_id, medico_id, data_hora, status) VALUES
(1, 1, '2026-11-10 09:00:00', 'AGENDADA'),
(2, 3, '2026-11-10 10:30:00', 'CONFIRMADA'),
(3, 2, '2026-11-11 14:00:00', 'FINALIZADA'),
(4, 4, '2026-11-11 15:00:00', 'CANCELADA');

INSERT INTO prontuarios (consulta_id, medico_id, observacoes_clinicas, prescricoes) VALUES
(3, 2, 'Paciente apresentou febre leve. Exame físico normal.', 'Paracetamol 500mg se dor ou febre.');