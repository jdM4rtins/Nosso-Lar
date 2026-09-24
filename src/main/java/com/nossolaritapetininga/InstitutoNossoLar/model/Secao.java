package com.nossolaritapetininga.InstitutoNossoLar.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
@Entity @Table(name = "secoes")
public class Secao extends EntidadeAuditavel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pagina", nullable = false)
    private Pagina pagina;
    private String titulo;
    private String subtitulo;
    private String tipo;
    @Column(columnDefinition = "TEXT")
    private String conteudo;
    private Integer ordem;
    private boolean ativo = true;
}
