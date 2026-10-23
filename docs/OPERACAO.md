# Operação do Instituto Nosso Lar

## Deploy

1. Faça merge da branch revisada em `main`.
2. Execute `bash mvnw -q -DskipTests package` no pipeline.
3. Configure no provedor `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `SESSION_COOKIE_SECURE=true` e as credenciais iniciais necessárias.
4. Faça o deploy e confira os logs do Flyway. O primeiro deploy em um banco existente deve registrar o baseline; o próximo deve aplicar somente migrations novas. Se a base legada ainda não tiver as colunas do RBAC, configure `JPA_DDL_AUTO=update` somente durante esse deploy de alinhamento, faça backup e depois volte para `JPA_DDL_AUTO=validate`.
5. Verifique `/`, `/login`, `/admin/dashboard` e a redefinição de senha na lista de usuários.

## Teste funcional mínimo

| Área | Verificação |
| --- | --- |
| EDITOR | Consulta e edição de conteúdo permitido; bloqueio de usuários e perfis |
| ADMIN | Criação de EDITOR; bloqueio de ADMIN/SUPER_ADMIN |
| SUPER_ADMIN | Usuários, perfis, permissões e auditoria |
| Conteúdo | Notícia e evento com upload, edição, publicação e remoção |
| Segurança | CSRF, sessão única, troca obrigatória e recuperação de senha |
| Banco | Migration aplicada, backup criado e restauração validada |

## Redefinição de senha

O SUPER_ADMIN confirma a identidade do usuário por um canal já combinado e usa **Redefinir senha** na lista de usuários. O sistema cria uma senha temporária, registra a ação na auditoria, encerra sessões abertas e exige a troca da senha no próximo login. A senha aparece uma única vez na resposta da ação; entregue-a ao usuário por canal seguro. A aba **Histórico de redefinições** lista as 100 ações mais recentes, sem expor as senhas. O SUPER_ADMIN não pode redefinir a própria senha por essa tela; deve usar a opção de alterar a própria senha.

O cadastro de usuário já segue o mesmo fluxo: o SUPER_ADMIN define uma senha temporária e o usuário precisa alterá-la no primeiro acesso.

## Backup

Ative `BACKUP_ENABLED` somente em um servidor com `pg_dump` e armazenamento persistente. A aplicação cria dumps custom diários conforme `BACKUP_SCHEDULE`, retém os últimos `BACKUP_RETENTION_DAYS` dias e registra checksum SHA-256. Faça um restore de teste antes da entrega e depois periodicamente.

## Capacitação

O treinamento cobre login, troca de senha, redefinição pelo SUPER_ADMIN, perfis, edição de conteúdo, imagens, eventos e abertura de chamados. As credenciais administrativas são entregues individualmente e nunca em documentação versionada.
