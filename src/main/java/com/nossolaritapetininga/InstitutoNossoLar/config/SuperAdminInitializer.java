package com.nossolaritapetininga.InstitutoNossoLar.config;

import com.nossolaritapetininga.InstitutoNossoLar.model.Perfil;
import com.nossolaritapetininga.InstitutoNossoLar.model.Permissao;
import com.nossolaritapetininga.InstitutoNossoLar.model.Usuario;
import com.nossolaritapetininga.InstitutoNossoLar.repository.AdministradorRepository;
import com.nossolaritapetininga.InstitutoNossoLar.repository.PerfilRepository;
import com.nossolaritapetininga.InstitutoNossoLar.repository.PermissaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class SuperAdminInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SuperAdminInitializer.class);

    private static final Map<String, String> PERMISSOES = new LinkedHashMap<>();

    static {
        PERMISSOES.put("VIEW_CONTENT", "Visualizar conteúdo administrativo");
        PERMISSOES.put("UPDATE_CONTENT", "Editar conteúdo existente");
        PERMISSOES.put("CREATE_CONTENT", "Criar conteúdo");
        PERMISSOES.put("DELETE_CONTENT", "Excluir conteúdo");
        PERMISSOES.put("VIEW_USER", "Visualizar usuários");
        PERMISSOES.put("CREATE_EDITOR", "Criar usuários com perfil EDITOR");
        PERMISSOES.put("UPDATE_USER", "Editar usuários");
        PERMISSOES.put("DISABLE_USER", "Desativar usuários");
        PERMISSOES.put("CREATE_ADMIN", "Criar usuários com perfil ADMIN");
        PERMISSOES.put("RESET_USER_PASSWORD", "Redefinir senha de outro usuário");
        PERMISSOES.put("VIEW_PROFILE", "Visualizar perfis e permissões");
        PERMISSOES.put("MANAGE_PROFILES", "Gerenciar perfis");
        PERMISSOES.put("MANAGE_PERMISSIONS", "Gerenciar permissões");
        PERMISSOES.put("ASSIGN_ROLE", "Atribuir perfil a usuário");
        PERMISSOES.put("VIEW_AUDIT_LOG", "Consultar registros de auditoria");
        PERMISSOES.put("CHANGE_OWN_PASSWORD", "Alterar a própria senha");
    }

    private static final Set<String> PERMISSOES_EDITOR = Set.of(
            "VIEW_CONTENT", "UPDATE_CONTENT", "CHANGE_OWN_PASSWORD");

    private static final Set<String> PERMISSOES_ADMIN = Set.of(
            "VIEW_CONTENT", "UPDATE_CONTENT", "CREATE_CONTENT", "DELETE_CONTENT",
            "VIEW_USER", "CREATE_EDITOR", "UPDATE_USER", "DISABLE_USER",
            "CHANGE_OWN_PASSWORD");

    private static final List<ContaInicial> SUPER_ADMINS = List.of(
            new ContaInicial(
                    "Juan Oliveira",
                    "juan_oliveira@nossolar.com",
                    "SUPER_ADMIN_JUAN_OLIVEIRA_PASSWORD"),
            new ContaInicial(
                    "Gabriel Ayres",
                    "gabriel_ayres@nossolar.com",
                    "SUPER_ADMIN_GABRIEL_AYRES_PASSWORD"),
            new ContaInicial(
                    "Juan Raphael",
                    "juan_raphael@nossolar.com",
                    "SUPER_ADMIN_JUAN_RAPHAEL_PASSWORD"));

    private final AdministradorRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final PermissaoRepository permissaoRepository;
    private final PasswordEncoder passwordEncoder;
    private final Environment environment;

    public SuperAdminInitializer(
            AdministradorRepository usuarioRepository,
            PerfilRepository perfilRepository,
            PermissaoRepository permissaoRepository,
            PasswordEncoder passwordEncoder,
            Environment environment) {
        this.usuarioRepository = usuarioRepository;
        this.perfilRepository = perfilRepository;
        this.permissaoRepository = permissaoRepository;
        this.passwordEncoder = passwordEncoder;
        this.environment = environment;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Map<String, Permissao> permissoes = criarPermissoes();
        criarOuAtualizarPerfil("EDITOR", "Edita conteúdos existentes", PERMISSOES_EDITOR, permissoes, false);
        criarOuAtualizarPerfil("ADMIN", "Gerencia conteúdos e usuários EDITOR", PERMISSOES_ADMIN, permissoes, false);
        Perfil superAdmin = criarOuAtualizarPerfil(
                "SUPER_ADMIN",
                "Gerencia usuários, perfis, permissões e auditoria",
                PERMISSOES.keySet(),
                permissoes,
                true);

        int contasConfiguradas = 0;
        for (ContaInicial conta : SUPER_ADMINS) {
            String senhaTemporaria = environment.getProperty(conta.variavelSenha());
            if (senhaTemporaria == null || senhaTemporaria.isBlank()) {
                log.warn("Conta inicial {} não criada: configure a variável {}",
                        conta.email(), conta.variavelSenha());
                continue;
            }

            criarOuPromoverSuperAdmin(conta, senhaTemporaria, superAdmin);
            contasConfiguradas++;
        }

        if (contasConfiguradas == SUPER_ADMINS.size()) {
            desativarAdministradorLegado();
        }
    }

    private Map<String, Permissao> criarPermissoes() {
        Map<String, Permissao> resultado = new LinkedHashMap<>();
        PERMISSOES.forEach((nome, descricao) -> {
            Permissao permissao = permissaoRepository.findByNome(nome).orElseGet(Permissao::new);
            permissao.setNome(nome);
            permissao.setDescricao(descricao);
            resultado.put(nome, permissaoRepository.save(permissao));
        });
        return resultado;
    }

    private Perfil criarOuAtualizarPerfil(
            String nome,
            String descricao,
            Set<String> nomesPermissoes,
            Map<String, Permissao> permissoes,
            boolean forcarPermissoes) {
        Perfil perfil = perfilRepository.findByNome(nome).orElseGet(Perfil::new);
        boolean novoPerfil = perfil.getId() == null;
        perfil.setNome(nome);
        if (novoPerfil || perfil.getDescricao() == null || perfil.getDescricao().isBlank()) {
            perfil.setDescricao(descricao);
        }
        perfil.setAtivo(true);
        if (novoPerfil || forcarPermissoes) {
            perfil.setPermissoes(new LinkedHashSet<>(
                    nomesPermissoes.stream().map(permissoes::get).toList()));
        }
        return perfilRepository.save(perfil);
    }

    private void criarOuPromoverSuperAdmin(
            ContaInicial conta,
            String senhaTemporaria,
            Perfil superAdmin) {
        Usuario usuario = usuarioRepository.findByEmail(conta.email()).orElseGet(Usuario::new);
        boolean novaConta = usuario.getId() == null;

        usuario.setNome(conta.nome());
        usuario.setEmail(conta.email());
        usuario.setAtivo(true);
        usuario.setPerfis(new LinkedHashSet<>(Set.of(superAdmin)));

        if (novaConta) {
            usuario.setSenha(passwordEncoder.encode(senhaTemporaria));
            usuario.setAlterarSenha(true);
        }

        usuarioRepository.save(usuario);
        log.info("Conta SUPER_ADMIN configurada para {}", conta.email());
    }

    private void desativarAdministradorLegado() {
        usuarioRepository.findByEmail("admin@nossolar.com").ifPresent(usuario -> {
            usuario.setAtivo(false);
            usuarioRepository.save(usuario);
            log.info("Conta administrativa legada desativada");
        });
    }

    private record ContaInicial(String nome, String email, String variavelSenha) {
    }
}
