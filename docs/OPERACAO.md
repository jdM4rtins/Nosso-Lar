# Operação do Instituto Nosso Lar

## Deploy

1. Faça merge da branch revisada em `main`.
2. Execute `bash mvnw -q -DskipTests package` no pipeline.
3. Configure no provedor `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `SESSION_COOKIE_SECURE=true`, as três senhas iniciais e as variáveis SMTP.
4. Faça o deploy e confira os logs do Flyway. O primeiro deploy em um banco existente deve registrar o baseline; o próximo deve aplicar somente migrations novas.
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

O usuário informa o e-mail em `/recuperar-senha`. O sistema envia um token de uso único por SMTP. O token nunca é salvo em texto puro, expira em uma hora e a resposta não confirma a existência da conta.

Para usar Resend, verifique o domínio de envio no painel do serviço, crie uma API key com permissão de envio e configure `MAIL_HOST=smtp.resend.com`, `MAIL_PORT=587`, `MAIL_USERNAME=resend`, `MAIL_PASSWORD` com a API key e `MAIL_FROM` em um endereço do domínio verificado. O plano gratuito informado pelo Resend oferece 3.000 e-mails transacionais por mês, limitado a 100 por dia; confirme os limites atuais antes de colocar o serviço em produção.

## Backup

Ative `BACKUP_ENABLED` somente em um servidor com `pg_dump` e armazenamento persistente. A aplicação cria dumps custom diários conforme `BACKUP_SCHEDULE`, retém os últimos `BACKUP_RETENTION_DAYS` dias e registra checksum SHA-256. Faça um restore de teste antes da entrega e depois periodicamente.

## Capacitação

O treinamento cobre login, troca/recuperação de senha, perfis, edição de conteúdo, imagens, eventos e abertura de chamados. As credenciais administrativas são entregues individualmente e nunca em documentação versionada.
