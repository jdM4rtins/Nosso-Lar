package com.nossolaritapetininga.InstitutoNossoLar.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter @EqualsAndHashCode @NoArgsConstructor @AllArgsConstructor
@Embeddable
public class SecaoMidiaId implements Serializable {
    @Column(name = "id_secao")
    private Long idSecao;
    @Column(name = "id_midia")
    private Long idMidia;
}
