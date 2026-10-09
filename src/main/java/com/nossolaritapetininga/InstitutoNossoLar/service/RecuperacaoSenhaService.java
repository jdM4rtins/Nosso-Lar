package com.nossolaritapetininga.InstitutoNossoLar.service;

import com.nossolaritapetininga.InstitutoNossoLar.exception.RegraNegocioException;
import com.nossolaritapetininga.InstitutoNossoLar.model.TokenRecuperacao;
import com.nossolaritapetininga.InstitutoNossoLar.model.Usuario;
import com.nossolaritapetininga.InstitutoNossoLar.repository.AdministradorRepository;
import com.nossolaritapetininga.InstitutoNossoLar.repository.TokenRecuperacaoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Locale;

@Service
public class RecuperacaoSenhaService {

    private final AdministradorRepository usuarioRepository;
    private final TokenRecuperacaoRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResendEmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Duration tokenExpiration;

    public RecuperacaoSenhaService(
            AdministradorRepository usuarioRepository,
            TokenRecuperacaoRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            ResendEmailService emailService,
            @Value("${app.recovery-token-expiration:PT1H}") Duration tokenExpiration) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.tokenExpiration = tokenExpiration;
    }

    @Transactional
    public void solicitar(String email) {
        if (email == null || email.isBlank()) {
            return;
        }
        usuarioRepository.findByEmailIgnoreCase(email.trim().toLowerCase(Locale.ROOT))
                .filter(Usuario::isAtivo)
                .ifPresent(this::criarEEnviarToken);
    }

    @Transactional(readOnly = true)
    public boolean tokenValido(String token) {
        return tokenRepository.findByTokenHash(hash(token))
                .filter(t -> t.getDataUtilizacao() == null)
                .filter(t -> t.getDataExpiracao().isAfter(LocalDateTime.now()))
                .map(t -> t.getUsuario().isAtivo())
                .orElse(false);
    }

    @Transactional
    public void redefinir(String token, String novaSenha, String confirmacao) {
        validarSenha(novaSenha, confirmacao);
        TokenRecuperacao recuperacao = tokenRepository.findByTokenHash(hash(token))
                .filter(t -> t.getDataUtilizacao() == null)
                .filter(t -> t.getDataExpiracao().isAfter(LocalDateTime.now()))
                .filter(t -> t.getUsuario().isAtivo())
                .orElseThrow(() -> new RegraNegocioException("O link de recuperação é inválido ou expirou."));

        Usuario usuario = recuperacao.getUsuario();
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuario.setAlterarSenha(false);
        usuarioRepository.save(usuario);
        recuperacao.setDataUtilizacao(LocalDateTime.now());
        tokenRepository.save(recuperacao);
        tokenRepository.invalidarPendentes(usuario.getId());
    }

    private void criarEEnviarToken(Usuario usuario) {
        tokenRepository.invalidarPendentes(usuario.getId());
        String token = tokenAleatorio();
        TokenRecuperacao recuperacao = new TokenRecuperacao();
        recuperacao.setUsuario(usuario);
        recuperacao.setTokenHash(hash(token));
        recuperacao.setDataCriacao(LocalDateTime.now());
        recuperacao.setDataExpiracao(LocalDateTime.now().plus(tokenExpiration));
        tokenRepository.save(recuperacao);

        emailService.enviarRecuperacao(usuario.getEmail(), usuario.getNome(), token, tokenExpiration.toMinutes());
    }

    private String tokenAleatorio() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String token) {
        if (token == null || token.isBlank()) {
            return "";
        }
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 não disponível", ex);
        }
    }

    private void validarSenha(String senha, String confirmacao) {
        if (senha == null || senha.length() < 12
                || senha.chars().noneMatch(Character::isUpperCase)
                || senha.chars().noneMatch(Character::isLowerCase)
                || senha.chars().noneMatch(Character::isDigit)
                || senha.chars().allMatch(Character::isLetterOrDigit)) {
            throw new RegraNegocioException("A senha deve ter pelo menos 12 caracteres, incluindo maiúscula, minúscula, número e símbolo.");
        }
        if (!senha.equals(confirmacao)) {
            throw new RegraNegocioException("A confirmação não corresponde à nova senha.");
        }
    }
}
