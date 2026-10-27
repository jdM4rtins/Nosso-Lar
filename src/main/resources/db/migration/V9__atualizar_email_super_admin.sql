-- Atualiza o login institucional da conta SUPER_ADMIN existente sem alterar sua senha.
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM administradores WHERE lower(email) = 'admin@nossolar.com.br')
       AND EXISTS (SELECT 1 FROM administradores WHERE lower(email) = 'admin@nossolar.com') THEN
        RAISE EXCEPTION 'Existem duas contas administrativas para os logins antigo e novo; resolva a duplicidade antes da migration.';
    END IF;
END $$;

UPDATE administradores
SET email = 'admin@nossolar.com.br'
WHERE lower(email) = 'admin@nossolar.com';
