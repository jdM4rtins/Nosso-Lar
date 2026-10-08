package com.nossolaritapetininga.InstitutoNossoLar.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
@Entity @Table(name = "formas_ajuda")
public class FormaAjuda extends EntidadeAuditavel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_instituicao", nullable = false)
    private Instituicao instituicao;
    @Column(nullable = false)
    private String titulo;
    @Column(columnDefinition = "TEXT")
    private String descricao;
    private String tipo;
    private String icone;
    private Integer ordem;
    private boolean ativo = true;
}
