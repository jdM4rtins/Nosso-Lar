-- A recuperação de acesso agora é feita pelo SUPER_ADMIN.
DELETE FROM tentativas_autenticacao WHERE tipo = 'RECUPERACAO';
DROP TABLE IF EXISTS tokens_recuperacao;
