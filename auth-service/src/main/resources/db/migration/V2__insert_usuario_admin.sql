INSERT INTO usuario (nome, login, email, senha, situacao)
VALUES ('Admin', 'admin', 'admin@email.com', '1234', 'ATIVO')
ON CONFLICT (login) DO NOTHING;
