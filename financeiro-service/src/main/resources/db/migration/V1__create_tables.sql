CREATE TABLE IF NOT EXISTS usuario (
    id       SERIAL PRIMARY KEY,
    nome     VARCHAR(100) NOT NULL,
    login    VARCHAR(50)  NOT NULL UNIQUE,
    senha    VARCHAR(100) NOT NULL,
    email    VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS lancamento (
    id               SERIAL PRIMARY KEY,
    usuario_id       INTEGER       NOT NULL REFERENCES usuario(id),
    descricao        VARCHAR(255)  NOT NULL,
    data_lancamento  DATE          NOT NULL,
    valor            NUMERIC(10,2) NOT NULL,
    tipo_lancamento  VARCHAR(20)   NOT NULL,
    situacao         VARCHAR(20)   NOT NULL
);
