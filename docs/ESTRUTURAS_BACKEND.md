# Estruturas principais do backend

Este documento descreve o backend do Instituto Nosso Lar com base nas classes e configurações existentes no repositório. O sistema é um site institucional com páginas públicas e um painel administrativo protegido. Foi desenvolvido em Java 21 e Spring Boot. Usa Spring MVC para processar requisições HTTP, Thymeleaf para renderizar páginas no servidor, Spring Security para autenticação e autorização, Spring Data JPA/Hibernate para persistência e PostgreSQL como banco de produção.

O fluxo geral é organizado em camadas: os controllers recebem requisições e definem respostas; os services executam operações e regras de negócio; os repositories consultam e alteram os dados; e as entidades representam os registros persistidos. A maior parte das respostas consiste em páginas HTML renderizadas pelo Thymeleaf. Existem também respostas específicas, como JSON para redefinição temporária de senha e conteúdo binário para imagens.

## 1. Controllers

Um controller associa rotas HTTP a operações da aplicação. Ele identifica o caminho e o método HTTP, lê parâmetros de URL ou de formulário, chama os services e escolhe a resposta. Em páginas web, essa resposta normalmente é o nome de um template ou um redirecionamento. Controllers podem colocar dados no `Model`, que o Thymeleaf usa para montar a página.

Os arquivos ficam em `src/main/java/com/nossolaritapetininga/InstitutoNossoLar/controller/`. Por exemplo, `HomeController.java` atende `GET /`, chama os services de conteúdo, notícias, atividades e eventos, adiciona os resultados ao `Model` e retorna o template `index`. `EventoController.java` atende as operações administrativas de eventos. No método de criação, recebe os campos do formulário e um possível arquivo, delega a validação e o upload aos services e redireciona para a lista após salvar.

Os controllers administrativos também usam `@PreAuthorize` para limitar operações a usuários com o perfil ou a permissão adequados. Por exemplo, o acesso ao controller de conteúdo requer `VIEW_CONTENT`, enquanto criar, editar e excluir exigem, respectivamente, permissões como `CREATE_CONTENT`, `UPDATE_CONTENT` e `DELETE_CONTENT`. Essas regras são verificadas no servidor.

## 2. Services

Um service executa operações da aplicação e aplica as regras de negócio. Ele faz a ligação entre controllers e repositories, mantendo os detalhes de persistência fora dos controllers. Um service também pode coordenar outros services, por exemplo, para salvar uma entidade e tratar um arquivo associado.

Os services ficam em `src/main/java/com/nossolaritapetininga/InstitutoNossoLar/service/`. `EventoService.java` oferece operações para listar, buscar, salvar e excluir eventos; também define a consulta de eventos ativos, com título e data inicial, ordenados pela data. `AdministradorService.java` valida dados de usuários, confere as regras de senha, associa perfis e codifica senhas antes da persistência. `ImagemService.java` valida e armazena imagens.

Um exemplo de colaboração entre camadas está no cadastro de evento. O controller recebe os dados e chama a validação e o tratamento de imagem; em seguida, chama `EventoService`, que usa `EventoRepository` para persistir a entidade. A validação de eventos exige título e data de início, impede data final anterior à inicial e exige que links preenchidos comecem por `http://` ou `https://`.

## 3. Repositories

Os repositories são a camada de acesso a dados. As interfaces do projeto estendem `JpaRepository`, que já oferece operações como `findById`, `findAll`, `save` e `deleteById`. O Spring Data JPA implementa essas operações com base nas entidades e nos nomes dos métodos.

Os arquivos estão em `src/main/java/com/nossolaritapetininga/InstitutoNossoLar/repository/`. `EventoRepository.java` estende `JpaRepository<Evento, Long>` e declara métodos de consulta adicionais. O método `findByAtivoTrueAndTituloIsNotNullAndDataInicioIsNotNullOrderByDataInicioAsc()` expressa, pelo nome, os filtros e a ordenação usados na consulta dos eventos públicos. `NoticiaRepository.java` oferece uma consulta semelhante para notícias ativas, ordenadas por data de publicação.

`AdministradorRepository.java` contém buscas por e-mail e uma consulta para contar usuários ativos por perfil, usada para proteger a última conta `SUPER_ADMIN`. A interface `HistoricoRedefinicaoSenhaProjection.java` seleciona campos específicos do histórico. Essa projeção serve a uma consulta especializada; não representa uma camada geral de DTOs.

## 4. Models e Entities

As classes em `model` representam os dados que o Hibernate mapeia às tabelas e aos relacionamentos do banco. As anotações JPA, como `@Entity`, `@Table`, `@Column`, `@ManyToOne` e `@ManyToMany`, definem entidades, tabelas, colunas e associações.

Os arquivos ficam em `src/main/java/com/nossolaritapetininga/InstitutoNossoLar/model/`. Entre as entidades de conteúdo estão `Conteudo`, `Atividade`, `Evento` e `Noticia`. `Evento` contém título, descrição, datas, local, link e referências opcionais à instituição e à mídia. `Noticia` contém título, resumo, conteúdo, data de publicação e estado ativo. `Midia` guarda metadados e bytes da imagem. Para administração, `Usuario` representa a tabela `administradores` e se associa a `Perfil`; cada `Perfil` se associa a `Permissao`.

Algumas entidades herdam de `EntidadeAuditavel`, que preenche timestamps de criação e atualização usando callbacks JPA. Há também entidades como `LogAuditoria`, `TentativaAutenticacao` e `ExecucaoBackup`, que registram ações administrativas, tentativas de login e execuções de backup. A estrutura das tabelas é evoluída por migrations Flyway; o Hibernate usa `ddl-auto=validate` por padrão para verificar se o esquema corresponde às entidades, sem alterá-lo automaticamente.

## 5. DTOs e transferência de dados

O projeto **não possui uma camada própria de DTOs** para receber e responder a todas as requisições. Em alguns pontos, os controllers recebem parâmetros com `@RequestParam`; em outros, os campos do formulário são vinculados diretamente a uma entidade por `@ModelAttribute`, como ocorre no cadastro de eventos. Portanto, não existe um conjunto separado de objetos de entrada e saída cobrindo os endpoints.

O projeto inclui a dependência de validação do Spring, mas as validações observadas estão implementadas sobretudo nos services e controllers. `HistoricoRedefinicaoSenhaProjection.java` é uma projeção do Spring Data para uma consulta específica, não uma camada geral de DTOs. Para as operações JSON, como a redefinição temporária de senha, o controller monta diretamente um mapa com os campos da resposta.

## 6. Rotas e endpoints

As rotas são declaradas com `@RequestMapping`, `@GetMapping` e `@PostMapping`. Em geral, `GET` exibe páginas e formulários; `POST` processa formulários. A aplicação é principalmente um site renderizado no servidor, não uma API REST JSON genérica.

| Área | Rotas principais |
|---|---|
| Site público e acesso | `GET /`, `GET /login`, `GET /acesso-negado` |
| Dashboard | `GET /admin`, `GET /admin/` e `GET /admin/dashboard` |
| Conteúdo | `GET /admin/conteudos`, `GET /admin/conteudos/novo`, `POST /admin/conteudos`, `GET` e `POST /admin/conteudos/editar/{id}`, `POST /admin/conteudos/excluir/{id}` |
| Atividades | `GET /admin/atividades`, `GET /admin/atividades/novo`, `POST /admin/atividades`, `GET` e `POST /admin/atividades/editar/{id}`, `POST /admin/atividades/excluir/{id}` |
| Eventos | `GET /admin/eventos`, `GET /admin/eventos/novo`, `POST /admin/eventos`, `GET` e `POST /admin/eventos/editar/{id}`, `POST /admin/eventos/excluir/{id}` |
| Notícias | `GET /admin/noticias`, `GET /admin/noticias/novo`, `POST /admin/noticias`, `GET` e `POST /admin/noticias/editar/{id}`, `POST /admin/noticias/excluir/{id}` |
| Administradores | Rotas sob `/admin/administradores` para listar, criar, editar, ativar/desativar, consultar histórico e redefinir senha temporária |
| Perfis | `GET /admin/perfis`, `GET /admin/perfis/editar/{id}`, `POST /admin/perfis/editar/{id}` |
| Senha própria | `GET /alterar-senha`, `POST /alterar-senha` |
| Mídia | `GET /midias/{id}` |

O formulário de login e o logout são tratados pela configuração do Spring Security, e não como métodos de controller equivalentes. `GET /midias/{id}` retorna os bytes da mídia. A redefinição de senha temporária também retorna JSON, embora a maior parte das rotas seja orientada a páginas e formulários.

## 7. Filtros, autenticação, autorização e erros

O projeto não define uma camada de middleware independente. As funções transversais de segurança são implementadas por filtros Servlet/Spring Security, que executam antes ou depois de determinados pontos da cadeia de requisições.

`LoginAttemptFilter.java` intercepta `POST /login`, consulta o serviço de proteção contra tentativas excessivas e redireciona para a página de login quando a combinação de e-mail e IP está bloqueada. Com os valores padrão, três falhas em uma janela de 15 minutos provocam bloqueio de 15 minutos; os valores podem ser configurados por variáveis de ambiente. `LoginFailureHandler.java` registra a falha e redireciona o usuário. `LoginSuccessHandler.java` limpa as falhas registradas e encaminha para a URL solicitada ou para o dashboard.

`CustomUserDetailsService.java` carrega o administrador pelo e-mail, sem diferenciar maiúsculas de minúsculas, e converte seus perfis e permissões em autoridades Spring. `SecurityConfig.java` configura o login, o encoder BCrypt, o gerenciamento de sessões e as regras de acesso. Páginas públicas, recursos estáticos e mídias podem ser acessados sem autenticação; `/admin/**` e `/alterar-senha` exigem sessão autenticada; outras rotas são negadas. As anotações `@PreAuthorize` dos controllers impõem as permissões mais específicas. A configuração também limita o usuário a uma sessão ativa e ativa proteção contra fixação de sessão.

`AlteracaoSenhaObrigatoriaFilter.java` verifica se a conta autenticada precisa trocar a senha e, nesse caso, redireciona para `/alterar-senha`. A senha nova deve ter ao menos 12 caracteres, uma letra maiúscula, uma minúscula, um número e um símbolo; ela é salva com BCrypt. `SessaoUsuarioService.java` permite expirar sessões quando uma conta ou suas permissões são alteradas.

O tratamento global de erro é limitado. `UploadExceptionHandler.java`, anotado com `@ControllerAdvice`, captura especificamente arquivos maiores que o limite e redireciona para o formulário apropriado com uma mensagem. As exceções de regra de negócio são tratadas localmente pelos controllers nos fluxos que precisam exibir mensagens ao usuário. `RegraNegocioException.java` é a exceção de negócio usada pelos services; não existe um handler global geral para todas as exceções.

## 8. Configurações e inicialização

As configurações principais estão em `src/main/resources/`. `application.properties` define conexão com banco, JPA, Flyway, limite de upload, sessão, porta e backup. A conexão pode ser substituída pelas variáveis `DB_URL`, `DB_USERNAME` e `DB_PASSWORD`. O cookie de sessão pode ser marcado como `Secure` com `SESSION_COOKIE_SECURE=true` quando o site está atrás de HTTPS.

`application-local.properties` configura H2 em memória, desliga Flyway e usa `create-drop`, útil para iniciar a interface localmente sem PostgreSQL; os dados desaparecem ao encerrar a aplicação. As migrations versionadas ficam em `src/main/resources/db/migration/`. Elas aplicam mudanças incrementais ao esquema existente. `SuperAdminInitializer` inicializa perfis e permissões, mas não cria a conta principal nem define uma senha. `DataInitializer` cria dados institucionais e exemplos somente quando ainda não existem.

`BackupService.java` é configurável e fica desativado por padrão. Quando ativado, executa `pg_dump` no horário configurado, registra status, tamanho e checksum SHA-256, e remove dumps antigos conforme a retenção definida. O diretório deve ser persistente em produção para que os arquivos sobrevivam a reinicializações da instância.

## 9. Fluxo completo de uma requisição

Como exemplo, considere o envio de `POST /admin/eventos`. O navegador envia os campos do evento e, opcionalmente, uma imagem. A cadeia de filtros do Spring Security verifica a sessão autenticada; em seguida, a autorização confere a permissão `CREATE_CONTENT`. Se o usuário não estiver autenticado ou não tiver permissão, a requisição é interrompida antes de executar a operação de gravação.

Com autorização concedida, Spring MVC direciona a requisição a `EventoController.salvar`. O controller recebe os dados do formulário por `@ModelAttribute` e o arquivo por `MultipartFile`. Ele prepara a entidade, valida os campos e delega a validação e persistência da imagem ao `ImagemService`. Se os dados forem inválidos, o controller armazena uma mensagem temporária e redireciona ao formulário.

Se os dados forem válidos, o controller chama `EventoService.salvar`. O service usa `EventoRepository`, e este delega ao Spring Data JPA/Hibernate a gravação da entidade no PostgreSQL. Depois de salvar, o controller retorna um redirecionamento para `/admin/eventos`; o navegador solicita a lista atualizada. Numa requisição de leitura, o fluxo de retorno é: repository entrega as entidades ao service, o service retorna ao controller, o controller coloca os dados no `Model`, o Thymeleaf renderiza o HTML e Spring MVC envia a resposta ao cliente.

Em resumo, o caminho típico é **requisição HTTP → filtros e segurança → controller → service → repository → banco de dados**. A resposta percorre o caminho inverso até ser transformada em HTML, redirecionamento ou, nos casos específicos, JSON ou conteúdo binário.
