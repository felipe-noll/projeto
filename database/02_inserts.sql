INSERT INTO usuario (nome, login, email, senha, situacao) VALUES
('Admin', 'admin', 'admin@email.com', '1234', 'ATIVO');

INSERT INTO lancamento (usuario_id, descricao, data_lancamento, valor, tipo_lancamento, situacao) VALUES
(1, 'Compra supermercado', '2026-03-01', 250.75, 'DESPESA', 'PAGO'),
(1, 'Salário mensal', '2026-03-05', 3500.00, 'RECEITA', 'RECEBIDO'),
(1, 'Conta de luz', '2026-03-08', 180.20, 'DESPESA', 'PENDENTE'),
(1, 'Internet', '2026-03-10', 120.00, 'DESPESA', 'PAGO'),
(1, 'Freelance', '2026-03-12', 800.00, 'RECEITA', 'RECEBIDO'),
(1, 'Aluguel', '2026-03-15', 1200.00, 'DESPESA', 'PAGO'),
(1, 'Venda produto', '2026-03-18', 450.00, 'RECEITA', 'RECEBIDO'),
(1, 'Combustível', '2026-03-20', 220.50, 'DESPESA', 'PENDENTE'),
(1, 'Plano de saúde', '2026-03-22', 300.00, 'DESPESA', 'PAGO'),
(1, 'Bônus', '2026-03-25', 1000.00, 'RECEITA', 'RECEBIDO');
