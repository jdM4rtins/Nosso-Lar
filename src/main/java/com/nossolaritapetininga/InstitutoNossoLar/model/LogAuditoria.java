package com.nossolaritapetininga.InstitutoNossoLar.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "logs_auditoria")
public class LogAuditoria {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
    @Column(nullable = false)
    private String entidade;
    @Column(name = "id_registro")
    private Long idRegistro;
    @Column(nullable = false)
    private String acao;
    @Column(name = "dados_anteriores", columnDefinition = "jsonb")
    private String dadosAnteriores;
    @Column(name = "dados_novos", columnDefinition = "jsonb")
    private String dadosNovos;
    private String ip;
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();
}
