package com.nossolaritapetininga.InstitutoNossoLar.service;

import com.nossolaritapetininga.InstitutoNossoLar.model.TentativaAutenticacao;
import com.nossolaritapetininga.InstitutoNossoLar.repository.TentativaAutenticacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProtecaoAutenticacaoServiceTest {

    @Mock
    private TentativaAutenticacaoRepository repository;
    private final Map<String, TentativaAutenticacao> dados = new HashMap<>();
    private ProtecaoAutenticacaoService service;

    @BeforeEach
    void configurar() {
        service = new ProtecaoAutenticacaoService(repository, 3, Duration.ofMinutes(15), Duration.ofMinutes(15));
        when(repository.findByChave(any())).thenAnswer(invocation ->
                Optional.ofNullable(dados.get(invocation.getArgument(0))));
        when(repository.save(any(TentativaAutenticacao.class))).thenAnswer(invocation -> {
            TentativaAutenticacao tentativa = invocation.getArgument(0);
            dados.put(tentativa.getChave(), tentativa);
            return tentativa;
        });
    }

    @Test
    void bloqueiaDepoisDaTerceiraFalha() {
        assertThat(service.podeTentarLogin("admin@nossolar.com", "127.0.0.1")).isTrue();
        service.registrarFalhaLogin("admin@nossolar.com", "127.0.0.1");
        service.registrarFalhaLogin("admin@nossolar.com", "127.0.0.1");
        service.registrarFalhaLogin("admin@nossolar.com", "127.0.0.1");

        assertThat(service.podeTentarLogin("admin@nossolar.com", "127.0.0.1")).isFalse();
        assertThat(dados).hasSize(1);
        assertThat(dados.values().iterator().next().getTentativas()).isEqualTo(3);
    }
}
