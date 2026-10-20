-- Limpa contas predefinidas ou antigas, mantendo apenas a conta SUPER_ADMIN aprovada.
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM administradores WHERE lower(email) = 'admin@nossolar.com'
    ) THEN
        RAISE EXCEPTION 'A conta admin@nossolar.com precisa existir antes de limpar as demais contas.';
    END IF;
END $$;

DELETE FROM tokens_recuperacao
WHERE id_usuario IN (
    SELECT id FROM administradores WHERE lower(email) <> 'admin@nossolar.com'
);

UPDATE midias SET id_usuario = NULL
WHERE id_usuario IN (
    SELECT id FROM administradores WHERE lower(email) <> 'admin@nossolar.com'
);

UPDATE logs_auditoria SET id_usuario = NULL
WHERE id_usuario IN (
    SELECT id FROM administradores WHERE lower(email) <> 'admin@nossolar.com'
);

UPDATE execucoes_backup SET id_usuario = NULL
WHERE id_usuario IN (
    SELECT id FROM administradores WHERE lower(email) <> 'admin@nossolar.com'
);

DELETE FROM usuarios_perfis
WHERE id_usuario IN (
    SELECT id FROM administradores WHERE lower(email) <> 'admin@nossolar.com'
);

DELETE FROM administradores
WHERE lower(email) <> 'admin@nossolar.com';
