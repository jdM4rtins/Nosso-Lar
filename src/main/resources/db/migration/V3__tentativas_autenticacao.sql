CREATE TABLE IF NOT EXISTS tentativas_autenticacao (
    id BIGSERIAL PRIMARY KEY,
    chave VARCHAR(320) NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    tentativas INTEGER NOT NULL DEFAULT 0,
    ultima_tentativa TIMESTAMP NOT NULL,
    bloqueado_ate TIMESTAMP NULL,
    CONSTRAINT uk_tentativa_chave UNIQUE (chave)
);

CREATE INDEX IF NOT EXISTS idx_tentativas_bloqueado_ate
    ON tentativas_autenticacao (bloqueado_ate);
