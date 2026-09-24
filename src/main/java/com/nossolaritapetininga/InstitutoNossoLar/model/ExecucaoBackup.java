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
@Table(name = "execucoes_backup")
public class ExecucaoBackup {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
    private String tipo;
    private String status;
    @Column(name = "local_arquivo")
    private String localArquivo;
    private String checksum;
    private Long tamanho;
    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;
    @Column(name = "data_fim")
    private LocalDateTime dataFim;
    @Column(name = "mensagem_erro", columnDefinition = "TEXT")
    private String mensagemErro;
}
