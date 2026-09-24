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
@Table(name = "midias")
public class Midia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    private String nome;

    @Column(name = "nome_original")
    private String nomeOriginal;

    private String caminho;

    private String url;

    private String tipo;

    @Column(name = "mime_type")
    private String mimeType;

    private Long tamanho;

    private Integer largura;

    private Integer altura;

    @Column(name = "texto_alternativo")
    private String textoAlternativo;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(name = "data_upload", nullable = false)
    private LocalDateTime dataUpload = LocalDateTime.now();
}
