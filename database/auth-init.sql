CREATE TABLE IF NOT EXISTS usuario (
    id        SERIAL PRIMARY KEY,
    nome      VARCHAR(100) NOT NULL,
    login     VARCHAR(50)  NOT NULL UNIQUE,
    senha     VARCHAR(100) NOT NULL,
    email     VARCHAR(100) NOT NULL,
    situacao  VARCHAR(20)  NOT NULL DEFAULT 'ATIVO'
);

INSERT INTO usuario (nome, login, email, senha, situacao) VALUES
('Admin', 'admin', 'admin@email.com', '1234', 'ATIVO')
ON CONFLICT (login) DO NOTHING;
