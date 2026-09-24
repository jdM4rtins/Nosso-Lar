package com.nossolaritapetininga.InstitutoNossoLar.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
@Entity
@Table(name = "envios_newsletter", uniqueConstraints =
        @UniqueConstraint(columnNames = {"id_newsletter", "id_inscrito"}))
public class EnvioNewsletter {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_newsletter", nullable = false)
    private Newsletter newsletter;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_inscrito", nullable = false)
    private InscritoNewsletter inscrito;
    private String status;
    @Column(name = "data_envio")
    private LocalDateTime dataEnvio;
    @Column(columnDefinition = "TEXT")
    private String erro;
}
