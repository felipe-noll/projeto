CREATE TABLE "usuario" (
  "id" SERIAL,
  "nome" VARCHAR(45) NULL,
  "login" VARCHAR(45) NULL,
  "email" VARCHAR(45) NULL,
  "senha" VARCHAR(45) NULL,
  "situacao" VARCHAR(45) NULL,
  PRIMARY KEY ("id")
);

CREATE TABLE "lancamento" (
  "id" SERIAL,
  "usuario_id" INT NOT NULL,
  "descricao" VARCHAR(45) NULL,
  "data_lancamento" DATE NULL,
  "valor" DECIMAL(10,2) NULL,
  "tipo_lancamento" VARCHAR(45) NULL,
  "situacao" VARCHAR(45) NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "fk_lancamento_usuario" 
    FOREIGN KEY ("usuario_id") 
    REFERENCES "usuario" ("id") 
);