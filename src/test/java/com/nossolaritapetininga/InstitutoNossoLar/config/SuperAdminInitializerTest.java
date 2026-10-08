package com.nossolaritapetininga.InstitutoNossoLar.config;

import com.nossolaritapetininga.InstitutoNossoLar.model.Perfil;
import com.nossolaritapetininga.InstitutoNossoLar.model.Permissao;
import com.nossolaritapetininga.InstitutoNossoLar.model.Usuario;
import com.nossolaritapetininga.InstitutoNossoLar.repository.AdministradorRepository;
import com.nossolaritapetininga.InstitutoNossoLar.repository.PerfilRepository;
import com.nossolaritapetininga.InstitutoNossoLar.repository.PermissaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SuperAdminInitializerTest {

    @Mock
    private AdministradorRepository usuarioRepository;
    @Mock
    private PerfilRepository perfilRepository;
    @Mock
    private PermissaoRepository permissaoRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private Environment environment;

    @Test
    void criaTresSuperAdministradoresSemGravarSenhaEmTextoPuro() throws Exception {
        when(permissaoRepository.findByNome(anyString())).thenReturn(Optional.empty());
        when(permissaoRepository.save(any(Permissao.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));
        when(perfilRepository.findByNome(anyString())).thenReturn(Optional.empty());
        when(perfilRepository.save(any(Perfil.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));
        when(environment.getProperty(anyString())).thenReturn("senha-temporaria");
        when(passwordEncoder.encode("senha-temporaria")).thenReturn("hash-seguro");

        SuperAdminInitializer initializer = new SuperAdminInitializer(
                usuarioRepository,
                perfilRepository,
                permissaoRepository,
                passwordEncoder,
                environment);

        initializer.run(new DefaultApplicationArguments(new String[0]));

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository, times(3)).save(captor.capture());
        List<Usuario> usuarios = captor.getAllValues();

        assertThat(usuarios).extracting(Usuario::getEmail).containsExactlyInAnyOrder(
                "juan_oliveira@nossolar.com",
                "gabriel_ayres@nossolar.com",
                "juan_raphael@nossolar.com");
        assertThat(usuarios).allSatisfy(usuario -> {
            assertThat(usuario.getSenha()).isEqualTo("hash-seguro");
            assertThat(usuario.getAlterarSenha()).isTrue();
            assertThat(usuario.isAtivo()).isTrue();
            assertThat(usuario.getPerfis()).extracting(Perfil::getNome).contains("SUPER_ADMIN");
        });
        assertThat(usuarios.stream()
                .flatMap(usuario -> usuario.getPerfis().stream())
                .filter(perfil -> perfil.getNome().equals("SUPER_ADMIN"))
                .map(Perfil::getPermissoes)
                .map(Set::size))
                .allMatch(total -> total > 0);
    }
}
