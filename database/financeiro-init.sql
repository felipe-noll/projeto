CREATE TABLE IF NOT EXISTS usuario (
    id       SERIAL PRIMARY KEY,
    nome     VARCHAR(100) NOT NULL,
    login    VARCHAR(50)  NOT NULL UNIQUE,
    email    VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS lancamento (
    id               SERIAL PRIMARY KEY,
    usuario_id       INTEGER NOT NULL REFERENCES usuario(id),
    descricao        VARCHAR(255) NOT NULL,
    data_lancamento  DATE         NOT NULL,
    valor            NUMERIC(10,2) NOT NULL,
    tipo_lancamento  VARCHAR(20)  NOT NULL,
    situacao         VARCHAR(20)  NOT NULL
);

INSERT INTO usuario (nome, login, email) VALUES
('Admin', 'admin', 'admin@email.com')
ON CONFLICT (login) DO NOTHING;

INSERT INTO lancamento (usuario_id, descricao, data_lancamento, valor, tipo_lancamento, situacao) VALUES
(1, 'Salário mensal',      '2026-03-05', 3500.00, 'RECEITA', 'RECEBIDO'),
(1, 'Aluguel',             '2026-03-15', 1200.00, 'DESPESA', 'PAGO'),
(1, 'Conta de luz',        '2026-03-08',  180.20, 'DESPESA', 'PENDENTE'),
(1, 'Internet',            '2026-03-10',  120.00, 'DESPESA', 'PAGO'),
(1, 'Freelance',           '2026-03-12',  800.00, 'RECEITA', 'RECEBIDO'),
(1, 'Compra supermercado', '2026-03-01',  250.75, 'DESPESA', 'PAGO'),
(1, 'Venda produto',       '2026-03-18',  450.00, 'RECEITA', 'RECEBIDO'),
(1, 'Combustível',         '2026-03-20',  220.50, 'DESPESA', 'PENDENTE'),
(1, 'Plano de saúde',      '2026-03-22',  300.00, 'DESPESA', 'PAGO'),
(1, 'Bônus',               '2026-03-25', 1000.00, 'RECEITA', 'RECEBIDO');
