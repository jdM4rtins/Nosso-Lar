package com.nossolaritapetininga.InstitutoNossoLar.config;

import com.nossolaritapetininga.InstitutoNossoLar.model.Perfil;
import com.nossolaritapetininga.InstitutoNossoLar.model.Permissao;
import com.nossolaritapetininga.InstitutoNossoLar.repository.PerfilRepository;
import com.nossolaritapetininga.InstitutoNossoLar.repository.PermissaoRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Component
public class SuperAdminInitializer implements ApplicationRunner {

    private static final Map<String, String> PERMISSOES = new LinkedHashMap<>();

    static {
        PERMISSOES.put("VIEW_CONTENT", "Visualizar conteúdo administrativo");
        PERMISSOES.put("UPDATE_CONTENT", "Editar conteúdo existente");
        PERMISSOES.put("CREATE_CONTENT", "Criar conteúdo");
        PERMISSOES.put("DELETE_CONTENT", "Excluir conteúdo");
        PERMISSOES.put("VIEW_USER", "Visualizar usuários");
        PERMISSOES.put("CREATE_EDITOR", "Criar usuários");
        PERMISSOES.put("UPDATE_USER", "Editar usuários");
        PERMISSOES.put("DISABLE_USER", "Desativar usuários");
        PERMISSOES.put("CREATE_ADMIN", "Criar usuários administrativos");
        PERMISSOES.put("RESET_USER_PASSWORD", "Redefinir senha de outro usuário");
        PERMISSOES.put("VIEW_PROFILE", "Visualizar perfis e permissões");
        PERMISSOES.put("MANAGE_PROFILES", "Gerenciar perfis");
        PERMISSOES.put("MANAGE_PERMISSIONS", "Gerenciar permissões");
        PERMISSOES.put("ASSIGN_ROLE", "Atribuir perfil a usuário");
        PERMISSOES.put("VIEW_AUDIT_LOG", "Consultar registros de auditoria");
        PERMISSOES.put("CHANGE_OWN_PASSWORD", "Alterar a própria senha");
    }

    private static final Set<String> PERMISSOES_CONSULTA = Set.of(
            "VIEW_CONTENT", "CHANGE_OWN_PASSWORD");

    private static final Set<String> PERMISSOES_EDITOR = Set.of(
            "VIEW_CONTENT", "CREATE_CONTENT", "UPDATE_CONTENT", "DELETE_CONTENT",
            "CHANGE_OWN_PASSWORD");

    private final PerfilRepository perfilRepository;
    private final PermissaoRepository permissaoRepository;
    public SuperAdminInitializer(
            PerfilRepository perfilRepository,
            PermissaoRepository permissaoRepository) {
        this.perfilRepository = perfilRepository;
        this.permissaoRepository = permissaoRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Map<String, Permissao> permissoes = criarPermissoes();
        criarOuAtualizarPerfil("CONSULTA", "Consulta o conteúdo sem alterá-lo", PERMISSOES_CONSULTA, permissoes, false);
        criarOuAtualizarPerfil("EDITOR", "Cria, edita e exclui conteúdo", PERMISSOES_EDITOR, permissoes, false);
        criarOuAtualizarPerfil(
                "SUPER_ADMIN",
                "Acesso completo ao sistema",
                PERMISSOES.keySet(),
                permissoes,
                true);
        desativarPerfilAdminLegado();
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

    private void desativarPerfilAdminLegado() {
        perfilRepository.findByNome("ADMIN").ifPresent(perfil -> {
            perfil.setAtivo(false);
            perfil.setPermissoes(new LinkedHashSet<>());
            perfilRepository.save(perfil);
        });
    }
}
