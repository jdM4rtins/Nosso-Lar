package com.nossolaritapetininga.InstitutoNossoLar.service;

import com.nossolaritapetininga.InstitutoNossoLar.model.TokenRecuperacao;
import com.nossolaritapetininga.InstitutoNossoLar.model.Usuario;
import com.nossolaritapetininga.InstitutoNossoLar.repository.AdministradorRepository;
import com.nossolaritapetininga.InstitutoNossoLar.repository.TokenRecuperacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecuperacaoSenhaServiceTest {

    @Mock AdministradorRepository usuarioRepository;
    @Mock TokenRecuperacaoRepository tokenRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock ResendEmailService emailService;

    private RecuperacaoSenhaService service;

    @BeforeEach
    void configurar() {
        service = new RecuperacaoSenhaService(usuarioRepository, tokenRepository, passwordEncoder,
                emailService, Duration.ofHours(1));
    }

    @Test
    void enviaTokenSemSalvarSenhaEmTextoPuro() {
        Usuario usuario = new Usuario();
        usuario.setId(7L);
        usuario.setNome("Pessoa");
        usuario.setEmail("pessoa@nossolar.com");
        usuario.setAtivo(true);
        when(usuarioRepository.findByEmailIgnoreCase("pessoa@nossolar.com")).thenReturn(Optional.of(usuario));
        when(tokenRepository.save(any(TokenRecuperacao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.solicitar("PESSOA@NOSSOLAR.COM");

        ArgumentCaptor<TokenRecuperacao> token = ArgumentCaptor.forClass(TokenRecuperacao.class);
        verify(tokenRepository).save(token.capture());
        assertThat(token.getValue().getTokenHash()).isNotBlank().doesNotContain("pessoa");
        verify(emailService).enviarRecuperacao(any(), any(), any(), any(Long.class));
    }

    @Test
    void redefineSenhaEInvalidaToken() {
        Usuario usuario = new Usuario();
        usuario.setId(7L);
        usuario.setAtivo(true);
        TokenRecuperacao token = new TokenRecuperacao();
        token.setUsuario(usuario);
        token.setDataExpiracao(LocalDateTime.now().plusHours(1));
        when(tokenRepository.findByTokenHash(any())).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("NovaSenhaSegura#1")).thenReturn("novo-hash");

        service.redefinir("token-de-teste", "NovaSenhaSegura#1", "NovaSenhaSegura#1");

        assertThat(usuario.getSenha()).isEqualTo("novo-hash");
        assertThat(token.getDataUtilizacao()).isNotNull();
        verify(tokenRepository).invalidarPendentes(7L);
    }
}
