-- Compatibilidade com bancos criados antes do RBAC. Todas as operações são idempotentes.
ALTER TABLE administradores ADD COLUMN IF NOT EXISTS alterar_senha BOOLEAN DEFAULT FALSE;
ALTER TABLE administradores ADD COLUMN IF NOT EXISTS ultimo_login TIMESTAMP NULL;
ALTER TABLE administradores ADD COLUMN IF NOT EXISTS data_atualizacao TIMESTAMP NULL;

UPDATE administradores
SET alterar_senha = FALSE
WHERE alterar_senha IS NULL;

CREATE TABLE IF NOT EXISTS perfis (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE,
    descricao VARCHAR(255),
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS permissoes (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE,
    descricao VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS usuarios_perfis (
    id_usuario BIGINT NOT NULL REFERENCES administradores(id),
    id_perfil BIGINT NOT NULL REFERENCES perfis(id),
    PRIMARY KEY (id_usuario, id_perfil)
);

CREATE TABLE IF NOT EXISTS perfis_permissoes (
    id_perfil BIGINT NOT NULL REFERENCES perfis(id),
    id_permissao BIGINT NOT NULL REFERENCES permissoes(id),
    PRIMARY KEY (id_perfil, id_permissao)
);
