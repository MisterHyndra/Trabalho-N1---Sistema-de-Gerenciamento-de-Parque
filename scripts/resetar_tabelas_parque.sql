-- Script para redefinir as tabelas (drop e recreate)
-- Conecta ao banco de dados
\c parque;

-- Remove as tabelas existentes na ordem correta (para evitar problemas com chaves estrangeiras)
DROP TABLE IF EXISTS atracao_cliente;
DROP TABLE IF EXISTS ingresso;
DROP TABLE IF EXISTS bilheteria;
DROP TABLE IF EXISTS atracao;
DROP TABLE IF EXISTS cliente;

-- Remove a função e o trigger
DROP TRIGGER IF EXISTS trigger_decrementar_bilhete ON ingresso;
DROP FUNCTION IF EXISTS decrementacao_bilhete();

-- Remove o tipo enum
DROP TYPE IF EXISTS forma_pagamento;

-- Cria o tipo ENUM para formas de pagamento
CREATE TYPE forma_pagamento AS ENUM ('pix', 'credito', 'debito', 'dinheiro');

-- Cria a tabela cliente
CREATE TABLE IF NOT EXISTS cliente (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    telefone VARCHAR(20),
    cpf VARCHAR(14) UNIQUE NOT NULL
);

-- Cria a tabela bilheteria
CREATE TABLE IF NOT EXISTS bilheteria (
    id SERIAL PRIMARY KEY,
    preco DECIMAL(5,2),
    horario_fechamento TIME NOT NULL DEFAULT '15:00:00',
    funcionamento DATE NOT NULL DEFAULT CURRENT_DATE,
    quantidade_disponivel INT NOT NULL CHECK (quantidade_disponivel >= 0)
);

-- Cria a tabela ingresso
CREATE TABLE IF NOT EXISTS ingresso (
    id SERIAL PRIMARY KEY,
    id_cliente INT NOT NULL, 
    id_bilheteria INT NOT NULL,
    data_venda DATE NOT NULL DEFAULT CURRENT_DATE,
    pagamento forma_pagamento NOT NULL,
    FOREIGN KEY (id_cliente) REFERENCES cliente(id),
    FOREIGN KEY (id_bilheteria) REFERENCES bilheteria(id)
);

-- Cria a tabela atracao
CREATE TABLE IF NOT EXISTS atracao (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    horario_inicio TIME NOT NULL,
    horario_fim TIME NOT NULL,
    capacidade INT NOT NULL CHECK (capacidade > 0)
);

-- Cria a tabela atracao_cliente
CREATE TABLE IF NOT EXISTS atracao_cliente (
    id SERIAL PRIMARY KEY,
    id_atracao INT NOT NULL,
    id_ingresso INT NOT NULL UNIQUE,
    horario_uso TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_atracao) REFERENCES atracao(id),
    FOREIGN KEY (id_ingresso) REFERENCES ingresso(id)
);

-- Cria a função para decrementar a quantidade de bilhetes disponíveis
CREATE OR REPLACE FUNCTION decrementacao_bilhete()
RETURNS TRIGGER AS $$
BEGIN
    IF (SELECT quantidade_disponivel FROM bilheteria WHERE id = NEW.id_bilheteria) <= 0
        THEN RAISE EXCEPTION 'Não há ingressos disponíveis!';
    END IF;
    
    UPDATE bilheteria  
    SET quantidade_disponivel = quantidade_disponivel - 1
    WHERE id = NEW.id_bilheteria;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Cria o trigger para decrementar a quantidade de bilhetes disponíveis
CREATE TRIGGER trigger_decrementar_bilhete
BEFORE INSERT ON ingresso
FOR EACH ROW
EXECUTE FUNCTION decrementacao_bilhete(); 