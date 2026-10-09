# Instituto Nosso Lar

Site institucional desenvolvido com Spring Boot, Thymeleaf e PostgreSQL.

## Requisitos

- Java 21 ou superior
- PostgreSQL 16 ou superior

## Como executar

1. Crie um banco PostgreSQL chamado `instituto_nosso_lar` e um usuário com acesso a ele.
2. Defina as variáveis de ambiente abaixo, ou atualize `src/main/resources/application.properties`:

```bash
export DB_URL='jdbc:postgresql://localhost:5432/instituto_nosso_lar'
export DB_USERNAME='instituto_user'
export DB_PASSWORD='sua-senha'
```

3. Inicie a aplicação:

```bash
bash mvnw spring-boot:run
```

4. Abra `http://localhost:8080`.

Para executar apenas a interface localmente, sem um PostgreSQL instalado, use o perfil temporário H2:

```bash
bash mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Esse perfil usa banco em memória e perde os dados ao encerrar a aplicação. Para desenvolvimento integrado e produção, use PostgreSQL.

Na primeira execução, o sistema cria os perfis `EDITOR`, `ADMIN` e `SUPER_ADMIN` e suas permissões. As contas iniciais de superadministradores são criadas somente quando estas variáveis contêm senhas temporárias:

```bash
export SUPER_ADMIN_JUAN_OLIVEIRA_PASSWORD='senha-temporaria'
export SUPER_ADMIN_GABRIEL_AYRES_PASSWORD='senha-temporaria'
export SUPER_ADMIN_JUAN_RAPHAEL_PASSWORD='senha-temporaria'
```

Não grave senhas reais neste arquivo, em `application.properties` ou em qualquer arquivo versionado. Em produção, configure-as como variáveis secretas no painel do Render.

## Controle de acesso

O painel usa controle de acesso por perfis e permissões (RBAC):

- `EDITOR`: visualiza e altera os conteúdos já existentes.
- `ADMIN`: gerencia conteúdos e cria ou administra contas de editores.
- `SUPER_ADMIN`: administra todos os usuários e configura as permissões dos perfis `EDITOR` e `ADMIN`.

As verificações são realizadas no servidor, nos métodos dos controladores e serviços. Ocultar uma opção na interface não concede nem remove acesso. Contas são desativadas em vez de excluídas, a própria conta não pode ser desativada pela tela de usuários e o último superadministrador ativo é protegido contra desativação.

Senhas temporárias devem seguir a política exibida na tela e precisam ser substituídas no primeiro acesso. A aplicação também limita cada conta a uma sessão ativa. Alterações de usuário ou permissões encerram as sessões afetadas para que as novas regras sejam aplicadas no próximo login.

Em produção com HTTPS, configure também:

```bash
export SESSION_COOKIE_SECURE=true
```

## Migrations com Flyway

O schema existente é registrado automaticamente como baseline 1. Alterações posteriores devem ser adicionadas em `src/main/resources/db/migration` com numeração crescente (`V3__descricao.sql`, `V4__descricao.sql` etc.). O Hibernate usa `validate` por padrão e não altera tabelas silenciosamente. Para alinhar uma base legada no primeiro deploy após a adoção do Flyway, configure temporariamente `JPA_DDL_AUTO=update`, faça um backup, confirme a inicialização e depois remova a variável ou defina `JPA_DDL_AUTO=validate`. Antes de cada deploy, valide a migration em uma cópia do banco e mantenha o backup do banco atual.

## Recuperação de senha

Configure o Resend em produção para que os links sejam enviados:

```bash
export RESEND_API_KEY='re_sua_chave_de_envio'
export RESEND_FROM='noreply@seudominioverificado.com'
export APP_PUBLIC_URL='https://seu-dominio.com'
```

### Usando Resend

O Resend entra como a API que entrega o e-mail de recuperação. A aplicação usa a SDK oficial Java. No Resend, verifique um domínio de envio e crie uma API key com permissão de envio. Configure `RESEND_API_KEY` e `RESEND_FROM` como segredos no provedor, nunca no código.

O plano gratuito do Resend está listado como US$ 0 por mês, com 3.000 e-mails transacionais por mês e limite de 100 por dia. Isso atende com folga a recuperação de senha de uma instituição pequena, mas depende da verificação do domínio e dos limites vigentes na conta. Confira os valores atuais na [página oficial de preços do Resend](https://resend.com/pricing).

O token é aleatório, armazenado somente como SHA-256, expira em uma hora e é invalidado depois do uso. A tela sempre mostra uma resposta genérica para não revelar se um e-mail está cadastrado.

Login e recuperação possuem proteção contra abuso por e-mail e endereço IP. Após três falhas dentro da janela configurada, novas tentativas são bloqueadas por 15 minutos. Os valores podem ser ajustados com `AUTH_MAX_ATTEMPTS`, `AUTH_ATTEMPT_WINDOW` e `AUTH_BLOCK_DURATION`.

## Backups recorrentes

O agendamento usa `pg_dump` no servidor e fica desligado por padrão. Para ativá-lo em um ambiente com diretório persistente, configure:

```bash
export BACKUP_ENABLED=true
export BACKUP_SCHEDULE='0 0 3 * * *' # todos os dias às 03:00
export BACKUP_DIRECTORY='/var/backups/nosso-lar'
export BACKUP_RETENTION_DAYS=30
export PG_DUMP_COMMAND='pg_dump'
```

Também configure `DB_URL`, `DB_USERNAME` e `DB_PASSWORD`. Cada execução registra status, tamanho e SHA-256 em `execucoes_backup`. O arquivo gerado é um dump custom do PostgreSQL. Para restaurar em uma base vazia:

```bash
pg_restore --clean --if-exists --no-owner \
  --dbname="$DB_URL" /var/backups/nosso-lar/instituto_nosso_lar-AAAA-MM-DD.dump
```

O diretório precisa estar em volume persistente e ser protegido com permissões restritas. O disco efêmero do Render não é suficiente para retenção de backups; nesse caso, execute o backup em um job/servidor com volume persistente ou conecte um armazenamento de backup antes de ativar `BACKUP_ENABLED`.

## Checklist de entrega e treinamento

Antes da entrega, valide login, recuperação de senha, upload e remoção de imagens, criação/edição/publicação de notícia e evento, desativação de usuários, proteção do último `SUPER_ADMIN`, bloqueio de rotas por perfil, sessão única e visualização em Chrome, Firefox, Edge e celular. Registre a versão implantada, URL, variáveis secretas configuradas e o resultado do restore de um backup de teste.

Na capacitação, demonstre o login, a troca de senha, recuperação de acesso, criação de conteúdo, upload de imagem até 2 MB, edição/publicação, criação de eventos e o procedimento de solicitar suporte. Entregue as credenciais por canal seguro e peça a troca imediata das senhas temporárias.

## Imagens

As imagens enviadas para atividades e eventos são validadas e armazenadas na tabela `midias` do PostgreSQL. São aceitos arquivos JPEG, PNG e GIF com até 2 MB, dimensões máximas de 4096 × 4096 pixels e limite de 16 milhões de pixels. O conteúdo é servido pela rota pública somente de leitura `/midias/{id}`.

O armazenamento não depende do sistema de arquivos da aplicação. Assim, as imagens permanecem disponíveis após reinícios e novos deploys no Render e são incluídas junto com o banco nos procedimentos de backup.

## Eventos e notícias

Eventos ativos que possuem título e data de início aparecem na seção pública de eventos, ordenados pela data inicial. O cadastro administrativo permite informar título, descrição, início, término, local, link externo, imagem e descrição acessível.

Notícias podem ser publicadas sem imagem. Na edição, a imagem atual pode ser mantida, substituída por outro upload ou removida. Imagens novas de eventos e notícias usam o armazenamento da tabela `midias`; caminhos estáticos existentes continuam compatíveis.
