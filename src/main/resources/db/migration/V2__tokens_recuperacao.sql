CREATE TABLE IF NOT EXISTS tokens_recuperacao (
    id BIGSERIAL PRIMARY KEY,
    id_usuario BIGINT NOT NULL REFERENCES administradores(id),
    token_hash VARCHAR(128) NOT NULL UNIQUE,
    data_expiracao TIMESTAMP NOT NULL,
    data_utilizacao TIMESTAMP NULL,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_tokens_recuperacao_usuario
    ON tokens_recuperacao (id_usuario);

CREATE INDEX IF NOT EXISTS idx_tokens_recuperacao_expiracao
    ON tokens_recuperacao (data_expiracao);
