-- Script para inserir dados de exemplo
-- Conecta ao banco de dados
\c parque;

-- Limpa os dados existentes (opcional)
DELETE FROM atracao_cliente;
DELETE FROM ingresso;
DELETE FROM bilheteria;
DELETE FROM atracao;
DELETE FROM cliente;

-- Reinicia as sequências das tabelas
ALTER SEQUENCE cliente_id_seq RESTART WITH 1;
ALTER SEQUENCE bilheteria_id_seq RESTART WITH 1;
ALTER SEQUENCE ingresso_id_seq RESTART WITH 1;
ALTER SEQUENCE atracao_id_seq RESTART WITH 1;
ALTER SEQUENCE atracao_cliente_id_seq RESTART WITH 1;

-- Insere clientes de exemplo
INSERT INTO cliente (nome, email, telefone, cpf) VALUES
('João Silva', 'joao@email.com', '(11) 98765-4321', '123.456.789-00'),
('Maria Santos', 'maria@email.com', '(11) 91234-5678', '987.654.321-00'),
('Pedro Oliveira', 'pedro@email.com', '(11) 95555-5555', '456.789.123-00'),
('Ana Souza', 'ana@email.com', '(11) 94444-4444', '789.123.456-00'),
('Carlos Ferreira', 'carlos@email.com', '(11) 93333-3333', '321.654.987-00');

-- Insere bilheterias de exemplo para os próximos 7 dias
INSERT INTO bilheteria (preco, horario_fechamento, funcionamento, quantidade_disponivel) VALUES
(25.90, '17:00:00', CURRENT_DATE, 100),
(35.50, '18:00:00', CURRENT_DATE + INTERVAL '1 day', 150),
(45.00, '19:00:00', CURRENT_DATE + INTERVAL '2 days', 200),
(29.90, '17:30:00', CURRENT_DATE + INTERVAL '3 days', 120),
(39.90, '18:30:00', CURRENT_DATE + INTERVAL '4 days', 180),
(49.90, '19:30:00', CURRENT_DATE + INTERVAL '5 days', 220),
(55.00, '20:00:00', CURRENT_DATE + INTERVAL '6 days', 250);

-- Insere atrações de exemplo
INSERT INTO atracao (nome, descricao, horario_inicio, horario_fim, capacidade) VALUES
('Montanha-Russa', 'Uma emocionante montanha-russa com loopings e quedas', '09:00:00', '18:00:00', 30),
('Roda Gigante', 'Uma roda gigante com vista panorâmica do parque', '10:00:00', '19:00:00', 40),
('Casa Mal-Assombrada', 'Uma casa com efeitos especiais assustadores', '11:00:00', '20:00:00', 20),
('Carrossel', 'Um clássico carrossel para todas as idades', '09:30:00', '17:30:00', 25),
('Splash', 'Uma queda d''água refrescante', '10:30:00', '18:30:00', 15),
('Auto-Pista', 'Pista de carrinhos de choque', '11:30:00', '19:30:00', 18),
('Trem Fantasma', 'Um passeio assustador em um trem que atravessa túneis escuros', '12:00:00', '20:30:00', 22),
('Barco Viking', 'Um barco que balança como as ondas do mar', '10:00:00', '18:00:00', 24);

-- Insere ingressos de exemplo (usará o trigger para decrementar a quantidade disponível)
INSERT INTO ingresso (id_cliente, id_bilheteria, data_venda, pagamento) VALUES
(1, 1, CURRENT_DATE, 'pix'),
(2, 1, CURRENT_DATE, 'credito'),
(3, 2, CURRENT_DATE, 'debito'),
(4, 2, CURRENT_DATE, 'dinheiro'),
(5, 3, CURRENT_DATE, 'pix');

-- Insere participações em atrações de exemplo
INSERT INTO atracao_cliente (id_atracao, id_ingresso, horario_uso) VALUES
(1, 1, CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(2, 2, CURRENT_TIMESTAMP - INTERVAL '2 hours'),
(3, 3, CURRENT_TIMESTAMP - INTERVAL '30 minutes'); 