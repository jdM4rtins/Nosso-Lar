package com.nossolaritapetininga.InstitutoNossoLar.service;

import com.nossolaritapetininga.InstitutoNossoLar.exception.RegraNegocioException;
import com.nossolaritapetininga.InstitutoNossoLar.model.Perfil;
import com.nossolaritapetininga.InstitutoNossoLar.model.Usuario;
import com.nossolaritapetininga.InstitutoNossoLar.repository.AdministradorRepository;
import com.nossolaritapetininga.InstitutoNossoLar.repository.PerfilRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdministradorServiceTest {

    @Mock
    private AdministradorRepository usuarioRepository;
    @Mock
    private PerfilRepository perfilRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private AdministradorService service;

    @BeforeEach
    void configurar() {
        service = new AdministradorService(usuarioRepository, perfilRepository, passwordEncoder);
    }

    @Test
    void adminCriaSomenteEditorComSenhaTemporariaProtegida() {
        Perfil editor = perfil("EDITOR");
        when(usuarioRepository.findByEmailIgnoreCase("editor@nossolar.com"))
                .thenReturn(Optional.empty());
        when(perfilRepository.findByNome("EDITOR")).thenReturn(Optional.of(editor));
        when(passwordEncoder.encode("SenhaTemporaria#1")).thenReturn("hash-seguro");
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        service.criarUsuario(
                "Pessoa Editora", "EDITOR@NOSSOLAR.COM", "SenhaTemporaria#1", "EDITOR", false);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario salvo = captor.getValue();
        assertThat(salvo.getEmail()).isEqualTo("editor@nossolar.com");
        assertThat(salvo.getSenha()).isEqualTo("hash-seguro");
        assertThat(salvo.getAlterarSenha()).isTrue();
        assertThat(salvo.getPerfis()).extracting(Perfil::getNome).containsExactly("EDITOR");
    }

    @Test
    void adminNaoPodeCriarOutroAdmin() {
        assertThatThrownBy(() -> service.criarUsuario(
                "Novo Admin", "admin2@nossolar.com", "SenhaTemporaria#1", "ADMIN", false))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("somente usuários EDITOR");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void usuarioNaoPodeDesativarAPropriaConta() {
        Usuario usuario = usuario("atual@nossolar.com", "SUPER_ADMIN");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> service.alterarStatus(
                1L, "atual@nossolar.com", true))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("conta atual");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void naoDesativaUltimoSuperAdmin() {
        Usuario usuario = usuario("outro@nossolar.com", "SUPER_ADMIN");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.contarAtivosPorPerfil("SUPER_ADMIN")).thenReturn(1L);

        assertThatThrownBy(() -> service.alterarStatus(
                1L, "atual@nossolar.com", true))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("último SUPER_ADMIN");
        verify(usuarioRepository, never()).save(any());
    }

    private Usuario usuario(String email, String nomePerfil) {
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setAtivo(true);
        usuario.setPerfis(new LinkedHashSet<>(Set.of(perfil(nomePerfil))));
        return usuario;
    }

    private Perfil perfil(String nome) {
        Perfil perfil = new Perfil();
        perfil.setNome(nome);
        perfil.setAtivo(true);
        return perfil;
    }
}
