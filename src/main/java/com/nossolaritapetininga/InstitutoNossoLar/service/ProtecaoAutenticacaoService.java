package com.nossolaritapetininga.InstitutoNossoLar.service;

import com.nossolaritapetininga.InstitutoNossoLar.model.TentativaAutenticacao;
import com.nossolaritapetininga.InstitutoNossoLar.repository.TentativaAutenticacaoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class ProtecaoAutenticacaoService {

    private final TentativaAutenticacaoRepository repository;
    private final int maxAttempts;
    private final Duration blockDuration;
    private final Duration attemptWindow;

    public ProtecaoAutenticacaoService(
            TentativaAutenticacaoRepository repository,
            @Value("${app.auth.max-attempts:3}") int maxAttempts,
            @Value("${app.auth.block-duration:PT15M}") Duration blockDuration,
            @Value("${app.auth.attempt-window:PT15M}") Duration attemptWindow) {
        this.repository = repository;
        this.maxAttempts = Math.max(1, maxAttempts);
        this.blockDuration = blockDuration;
        this.attemptWindow = attemptWindow;
    }

    @Transactional(readOnly = true)
    public boolean podeTentarLogin(String email, String ip) {
        return podeTentar(chave("LOGIN", email, ip));
    }

    @Transactional
    public void registrarFalhaLogin(String email, String ip) {
        registrarFalha(chave("LOGIN", email, ip), "LOGIN");
    }

    @Transactional
    public void registrarSucessoLogin(String email, String ip) {
        repository.findByChave(chave("LOGIN", email, ip)).ifPresent(repository::delete);
    }

    @Transactional
    public boolean registrarTentativaRecuperacao(String email, String ip) {
        String chave = chave("RECUPERACAO", email, ip);
        if (!podeTentar(chave)) {
            return false;
        }
        registrarFalha(chave, "RECUPERACAO");
        return true;
    }

    private boolean podeTentar(String chave) {
        return repository.findByChave(chave)
                .map(this::normalizarJanela)
                .map(tentativa -> tentativa.getBloqueadoAte() == null
                        || tentativa.getBloqueadoAte().isBefore(LocalDateTime.now()))
                .orElse(true);
    }

    private TentativaAutenticacao normalizarJanela(TentativaAutenticacao tentativa) {
        if (tentativa.getUltimaTentativa() != null
                && tentativa.getUltimaTentativa().plus(attemptWindow).isBefore(LocalDateTime.now())) {
            tentativa.setTentativas(0);
            tentativa.setBloqueadoAte(null);
        }
        return tentativa;
    }

    private void registrarFalha(String chave, String tipo) {
        TentativaAutenticacao tentativa = repository.findByChave(chave)
                .map(this::normalizarJanela)
                .orElseGet(TentativaAutenticacao::new);
        tentativa.setChave(chave);
        tentativa.setTipo(tipo);
        tentativa.setTentativas(tentativa.getTentativas() + 1);
        tentativa.setUltimaTentativa(LocalDateTime.now());
        if (tentativa.getTentativas() >= maxAttempts) {
            tentativa.setBloqueadoAte(LocalDateTime.now().plus(blockDuration));
        }
        repository.save(tentativa);
    }

    private String chave(String tipo, String email, String ip) {
        String identificador = email == null || email.isBlank()
                ? "desconhecido" : email.trim().toLowerCase(Locale.ROOT);
        String origem = ip == null || ip.isBlank() ? "desconhecido" : ip.trim();
        return tipo + ":" + identificador + ":" + origem;
    }
}
