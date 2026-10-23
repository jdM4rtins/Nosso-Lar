package com.nossolaritapetininga.InstitutoNossoLar.service;

import java.time.LocalDateTime;

public record HistoricoRedefinicaoSenha(
        LocalDateTime dataHora,
        String usuarioAfetado,
        String responsavel,
        String ip) {
}
