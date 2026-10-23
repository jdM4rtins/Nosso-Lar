package com.nossolaritapetininga.InstitutoNossoLar.repository;

import java.time.LocalDateTime;

public interface HistoricoRedefinicaoSenhaProjection {
    LocalDateTime getDataHora();
    String getUsuarioAfetado();
    String getResponsavel();
    String getIp();
}
