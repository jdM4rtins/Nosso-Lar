package com.nossolaritapetininga.InstitutoNossoLar.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
@Entity @Table(name = "links")
public class Link {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_forma_ajuda")
    private FormaAjuda formaAjuda;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;
    private String texto;
    private String tipo;
    private String target;
    private boolean ativo = true;
    private Integer ordem;
}
