package com.nossolaritapetininga.InstitutoNossoLar.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
@Entity @Table(name = "secoes_midias")
public class SecaoMidia {
    @EmbeddedId
    private SecaoMidiaId id;
    @MapsId("idSecao")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_secao", nullable = false)
    private Secao secao;
    @MapsId("idMidia")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_midia", nullable = false)
    private Midia midia;
    private String tipo;
    private Integer ordem;
}
