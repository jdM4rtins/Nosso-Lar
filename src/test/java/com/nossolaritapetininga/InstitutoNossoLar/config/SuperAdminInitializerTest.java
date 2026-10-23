package com.nossolaritapetininga.InstitutoNossoLar.config;

import com.nossolaritapetininga.InstitutoNossoLar.model.Perfil;
import com.nossolaritapetininga.InstitutoNossoLar.model.Permissao;
import com.nossolaritapetininga.InstitutoNossoLar.repository.PerfilRepository;
import com.nossolaritapetininga.InstitutoNossoLar.repository.PermissaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.DefaultApplicationArguments;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SuperAdminInitializerTest {

    @Mock
    private PerfilRepository perfilRepository;
    @Mock
    private PermissaoRepository permissaoRepository;

    @Test
    void criaPerfisComAcessosCorrespondentesAHierarquia() throws Exception {
        when(permissaoRepository.findByNome(anyString())).thenReturn(Optional.empty());
        when(permissaoRepository.save(any(Permissao.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));
        when(perfilRepository.findByNome(anyString())).thenReturn(Optional.empty());
        when(perfilRepository.save(any(Perfil.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        SuperAdminInitializer initializer = new SuperAdminInitializer(
                perfilRepository,
                permissaoRepository);

        initializer.run(new DefaultApplicationArguments(new String[0]));

        ArgumentCaptor<Perfil> captor = ArgumentCaptor.forClass(Perfil.class);
        verify(perfilRepository, org.mockito.Mockito.times(3)).save(captor.capture());
        List<Perfil> perfis = captor.getAllValues();
        var permissoesPorPerfil = perfis.stream().collect(Collectors.toMap(
                Perfil::getNome,
                perfil -> perfil.getPermissoes().stream()
                        .map(Permissao::getNome)
                        .collect(Collectors.toSet())));

        assertThat(permissoesPorPerfil.keySet())
                .containsExactlyInAnyOrder("CONSULTA", "EDITOR", "SUPER_ADMIN");
        assertThat(permissoesPorPerfil.get("CONSULTA"))
                .containsExactlyInAnyOrder("VIEW_CONTENT", "CHANGE_OWN_PASSWORD");
        assertThat(permissoesPorPerfil.get("EDITOR"))
                .containsExactlyInAnyOrder(
                        "VIEW_CONTENT", "CREATE_CONTENT", "UPDATE_CONTENT",
                        "DELETE_CONTENT", "CHANGE_OWN_PASSWORD");
        assertThat(permissoesPorPerfil.get("SUPER_ADMIN"))
                .hasSize(16)
                .contains("VIEW_USER", "MANAGE_PERMISSIONS", "DELETE_CONTENT");
    }
}
