package com.nossolaritapetininga.InstitutoNossoLar.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
@Entity @Table(name = "paginas")
public class Pagina extends EntidadeAuditavel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_instituicao", nullable = false)
    private Instituicao instituicao;
    @Column(nullable = false)
    private String titulo;
    @Column(nullable = false, unique = true)
    private String slug;
    @Column(columnDefinition = "TEXT")
    private String descricao;
    private String status;
    private Integer ordem;
    private boolean publicado;
}
