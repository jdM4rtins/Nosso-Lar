-- Define os três perfis oficiais e seus acessos iniciais.
INSERT INTO permissoes (nome, descricao) VALUES
    ('VIEW_CONTENT', 'Visualizar conteúdo administrativo'),
    ('UPDATE_CONTENT', 'Editar conteúdo existente'),
    ('CREATE_CONTENT', 'Criar conteúdo'),
    ('DELETE_CONTENT', 'Excluir conteúdo'),
    ('VIEW_USER', 'Visualizar usuários'),
    ('CREATE_EDITOR', 'Criar usuários'),
    ('UPDATE_USER', 'Editar usuários'),
    ('DISABLE_USER', 'Ativar ou desativar usuários'),
    ('CREATE_ADMIN', 'Criar usuários administrativos'),
    ('RESET_USER_PASSWORD', 'Redefinir senha de outro usuário'),
    ('VIEW_PROFILE', 'Visualizar perfis e permissões'),
    ('MANAGE_PROFILES', 'Gerenciar perfis'),
    ('MANAGE_PERMISSIONS', 'Gerenciar permissões'),
    ('ASSIGN_ROLE', 'Atribuir perfil a usuário'),
    ('VIEW_AUDIT_LOG', 'Consultar registros de auditoria'),
    ('CHANGE_OWN_PASSWORD', 'Alterar a própria senha')
ON CONFLICT (nome) DO UPDATE SET descricao = EXCLUDED.descricao;

INSERT INTO perfis (nome, descricao, ativo) VALUES
    ('CONSULTA', 'Consulta o conteúdo sem alterá-lo', TRUE),
    ('EDITOR', 'Cria, edita e exclui conteúdo', TRUE),
    ('SUPER_ADMIN', 'Acesso completo ao sistema', TRUE)
ON CONFLICT (nome) DO UPDATE
SET descricao = EXCLUDED.descricao, ativo = TRUE;

-- Contas que usavam o perfil ADMIN legado passam para Consulta,
-- exceto a conta administrativa principal, promovida abaixo.
INSERT INTO usuarios_perfis (id_usuario, id_perfil)
SELECT up.id_usuario, consulta.id
FROM usuarios_perfis up
JOIN perfis legado ON legado.id = up.id_perfil AND legado.nome = 'ADMIN'
JOIN administradores usuario ON usuario.id = up.id_usuario
JOIN perfis consulta ON consulta.nome = 'CONSULTA'
WHERE lower(usuario.email) <> 'admin@nossolar.com'
ON CONFLICT DO NOTHING;

DELETE FROM usuarios_perfis
WHERE id_perfil IN (SELECT id FROM perfis WHERE nome = 'ADMIN');

UPDATE perfis
SET ativo = FALSE,
    descricao = 'Perfil legado desativado; substituído por CONSULTA, EDITOR e SUPER_ADMIN'
WHERE nome = 'ADMIN';

-- Aplica a matriz aprovada aos perfis existentes durante a migração.
DELETE FROM perfis_permissoes
WHERE id_perfil IN (SELECT id FROM perfis WHERE nome IN ('CONSULTA', 'EDITOR', 'SUPER_ADMIN'));

WITH acessos_padrao(perfil_nome, permissao_nome) AS (
    VALUES
        ('CONSULTA', 'VIEW_CONTENT'),
        ('CONSULTA', 'CHANGE_OWN_PASSWORD'),
        ('EDITOR', 'VIEW_CONTENT'),
        ('EDITOR', 'CREATE_CONTENT'),
        ('EDITOR', 'UPDATE_CONTENT'),
        ('EDITOR', 'DELETE_CONTENT'),
        ('EDITOR', 'CHANGE_OWN_PASSWORD'),
        ('SUPER_ADMIN', 'VIEW_CONTENT'),
        ('SUPER_ADMIN', 'UPDATE_CONTENT'),
        ('SUPER_ADMIN', 'CREATE_CONTENT'),
        ('SUPER_ADMIN', 'DELETE_CONTENT'),
        ('SUPER_ADMIN', 'VIEW_USER'),
        ('SUPER_ADMIN', 'CREATE_EDITOR'),
        ('SUPER_ADMIN', 'UPDATE_USER'),
        ('SUPER_ADMIN', 'DISABLE_USER'),
        ('SUPER_ADMIN', 'CREATE_ADMIN'),
        ('SUPER_ADMIN', 'RESET_USER_PASSWORD'),
        ('SUPER_ADMIN', 'VIEW_PROFILE'),
        ('SUPER_ADMIN', 'MANAGE_PROFILES'),
        ('SUPER_ADMIN', 'MANAGE_PERMISSIONS'),
        ('SUPER_ADMIN', 'ASSIGN_ROLE'),
        ('SUPER_ADMIN', 'VIEW_AUDIT_LOG'),
        ('SUPER_ADMIN', 'CHANGE_OWN_PASSWORD')
)
INSERT INTO perfis_permissoes (id_perfil, id_permissao)
SELECT perfil.id, permissao.id
FROM acessos_padrao
JOIN perfis perfil ON perfil.nome = acessos_padrao.perfil_nome
JOIN permissoes permissao ON permissao.nome = acessos_padrao.permissao_nome
ON CONFLICT DO NOTHING;

-- A conta admin existente é a conta de controle total aprovada pelo usuário.
INSERT INTO usuarios_perfis (id_usuario, id_perfil)
SELECT usuario.id, super_admin.id
FROM administradores usuario
JOIN perfis super_admin ON super_admin.nome = 'SUPER_ADMIN'
WHERE lower(usuario.email) = 'admin@nossolar.com'
ON CONFLICT DO NOTHING;

DELETE FROM usuarios_perfis
WHERE id_usuario IN (
    SELECT id FROM administradores WHERE lower(email) = 'admin@nossolar.com'
)
AND id_perfil NOT IN (
    SELECT id FROM perfis WHERE nome = 'SUPER_ADMIN'
);
