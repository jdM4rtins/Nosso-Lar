package com.nossolaritapetininga.InstitutoNossoLar.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
@Entity @Table(name = "contratantes")
public class Contratante {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_instituicao", nullable = false)
    private Instituicao instituicao;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_midia")
    private Midia midia;
    @Column(nullable = false)
    private String nome;
    private String url;
    private Integer ordem;
    private boolean ativo = true;
}
