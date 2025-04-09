-- Cria o banco de dados
CREATE DATABASE parque;

-- Conecta ao banco de dados
\c parque;

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

-- Dados de exemplo (opcional)

-- Insere clientes de exemplo
INSERT INTO cliente (nome, email, telefone, cpf) VALUES
('João Silva', 'joao@email.com', '(11) 98765-4321', '123.456.789-00'),
('Maria Santos', 'maria@email.com', '(11) 91234-5678', '987.654.321-00'),
('Pedro Oliveira', 'pedro@email.com', '(11) 95555-5555', '456.789.123-00');

-- Insere bilheterias de exemplo
INSERT INTO bilheteria (preco, horario_fechamento, funcionamento, quantidade_disponivel) VALUES
(25.90, '17:00:00', CURRENT_DATE, 100),
(35.50, '18:00:00', CURRENT_DATE + INTERVAL '1 day', 150),
(45.00, '19:00:00', CURRENT_DATE + INTERVAL '2 days', 200);

-- Insere atrações de exemplo
INSERT INTO atracao (nome, descricao, horario_inicio, horario_fim, capacidade) VALUES
('Montanha-Russa', 'Uma emocionante montanha-russa com loopings e quedas', '09:00:00', '18:00:00', 30),
('Roda Gigante', 'Uma roda gigante com vista panorâmica do parque', '10:00:00', '19:00:00', 40),
('Casa Mal-Assombrada', 'Uma casa com efeitos especiais assustadores', '11:00:00', '20:00:00', 20),
('Carrossel', 'Um clássico carrossel para todas as idades', '09:30:00', '17:30:00', 25),
('Splash', 'Uma queda d''água refrescante', '10:30:00', '18:30:00', 15); 