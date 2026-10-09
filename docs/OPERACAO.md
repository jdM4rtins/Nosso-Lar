# Operação do Instituto Nosso Lar

## Deploy

1. Faça merge da branch revisada em `main`.
2. Execute `bash mvnw -q -DskipTests package` no pipeline.
3. Configure no provedor `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `SESSION_COOKIE_SECURE=true`, as três senhas iniciais, `RESEND_API_KEY` e `RESEND_FROM`.
4. Faça o deploy e confira os logs do Flyway. O primeiro deploy em um banco existente deve registrar o baseline; o próximo deve aplicar somente migrations novas. Se a base legada ainda não tiver as colunas do RBAC, configure `JPA_DDL_AUTO=update` somente durante esse deploy de alinhamento, faça backup e depois volte para `JPA_DDL_AUTO=validate`.
5. Verifique `/`, `/login`, `/recuperar-senha` e `/admin/dashboard`.

## Teste funcional mínimo

| Área | Verificação |
| --- | --- |
| EDITOR | Consulta e edição de conteúdo permitido; bloqueio de usuários e perfis |
| ADMIN | Criação de EDITOR; bloqueio de ADMIN/SUPER_ADMIN |
| SUPER_ADMIN | Usuários, perfis, permissões e auditoria |
| Conteúdo | Notícia e evento com upload, edição, publicação e remoção |
| Segurança | CSRF, sessão única, troca obrigatória e recuperação de senha |
| Banco | Migration aplicada, backup criado e restauração validada |

## Recuperação de senha

O usuário informa o e-mail em `/recuperar-senha`. O sistema envia um token de uso único pela API do Resend. O token nunca é salvo em texto puro, expira em uma hora e a resposta não confirma a existência da conta.

Login e recuperação permitem três tentativas por combinação de e-mail e IP dentro da janela configurada. A partir da quarta tentativa, o acesso fica bloqueado temporariamente; a página não revela detalhes que possam confirmar a existência de uma conta.

Para usar Resend, verifique o domínio de envio no painel do serviço, crie uma API key com permissão de envio e configure `RESEND_API_KEY` com a chave e `RESEND_FROM` em um endereço do domínio verificado. O plano gratuito informado pelo Resend oferece 3.000 e-mails transacionais por mês, limitado a 100 por dia; confirme os limites atuais antes de colocar o serviço em produção.

## Backup

Ative `BACKUP_ENABLED` somente em um servidor com `pg_dump` e armazenamento persistente. A aplicação cria dumps custom diários conforme `BACKUP_SCHEDULE`, retém os últimos `BACKUP_RETENTION_DAYS` dias e registra checksum SHA-256. Faça um restore de teste antes da entrega e depois periodicamente.

## Capacitação

O treinamento cobre login, troca/recuperação de senha, perfis, edição de conteúdo, imagens, eventos e abertura de chamados. As credenciais administrativas são entregues individualmente e nunca em documentação versionada.
