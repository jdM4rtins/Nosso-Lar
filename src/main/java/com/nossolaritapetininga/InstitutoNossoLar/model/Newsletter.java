package com.nossolaritapetininga.InstitutoNossoLar.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
@Entity @Table(name = "newsletters")
public class Newsletter extends EntidadeAuditavel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_instituicao", nullable = false)
    private Instituicao instituicao;
    @Column(nullable = false)
    private String assunto;
    @Column(columnDefinition = "TEXT")
    private String conteudo;
    @Column(name = "data_envio")
    private LocalDateTime dataEnvio;
    private String status;
}
