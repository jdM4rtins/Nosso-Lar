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
