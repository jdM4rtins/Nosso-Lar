package com.nossolaritapetininga.InstitutoNossoLar.service;

import com.nossolaritapetininga.InstitutoNossoLar.exception.RegraNegocioException;
import com.nossolaritapetininga.InstitutoNossoLar.model.Perfil;
import com.nossolaritapetininga.InstitutoNossoLar.model.Permissao;
import com.nossolaritapetininga.InstitutoNossoLar.repository.PerfilRepository;
import com.nossolaritapetininga.InstitutoNossoLar.repository.PermissaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class PerfilService {

    private static final Set<String> PERFIS_SISTEMA = Set.of("EDITOR", "ADMIN", "SUPER_ADMIN");

    private final PerfilRepository perfilRepository;
    private final PermissaoRepository permissaoRepository;

    public PerfilService(
            PerfilRepository perfilRepository,
            PermissaoRepository permissaoRepository) {
        this.perfilRepository = perfilRepository;
        this.permissaoRepository = permissaoRepository;
    }

    public List<Perfil> listarPerfis() {
        return perfilRepository.findAll();
    }

    public List<Permissao> listarPermissoes() {
        return permissaoRepository.findAll().stream()
                .sorted((a, b) -> a.getNome().compareTo(b.getNome()))
                .toList();
    }

    public Perfil buscarPerfil(Long id) {
        return perfilRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Perfil não encontrado."));
    }

    @Transactional
    public void atualizarPermissoes(Long id, String descricao, List<Long> permissoesIds) {
        Perfil perfil = buscarPerfil(id);
        if (!PERFIS_SISTEMA.contains(perfil.getNome())) {
            throw new RegraNegocioException("Somente os perfis oficiais podem ser administrados.");
        }
        if ("SUPER_ADMIN".equals(perfil.getNome())) {
            throw new RegraNegocioException(
                    "As permissões de SUPER_ADMIN são protegidas para evitar bloqueio administrativo.");
        }

        List<Long> ids = permissoesIds == null ? List.of() : permissoesIds;
        List<Permissao> permissoes = new ArrayList<>(permissaoRepository.findAllById(ids));
        if (permissoes.size() != new LinkedHashSet<>(ids).size()) {
            throw new RegraNegocioException("Uma ou mais permissões selecionadas não existem.");
        }

        permissaoRepository.findByNome("CHANGE_OWN_PASSWORD").ifPresent(permissao -> {
            if (!permissoes.contains(permissao)) {
                permissoes.add(permissao);
            }
        });
        perfil.setDescricao(descricao == null ? "" : descricao.trim());
        perfil.setPermissoes(new LinkedHashSet<>(permissoes));
        perfilRepository.save(perfil);
    }
}
