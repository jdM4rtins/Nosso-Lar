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

Na primeira execução, o sistema cria conteúdo demonstrativo e o acesso administrativo:

- E-mail: `admin@nossolar.com`
- Senha: `123456`

Altere essa senha após o primeiro acesso em `/admin/administradores`.
