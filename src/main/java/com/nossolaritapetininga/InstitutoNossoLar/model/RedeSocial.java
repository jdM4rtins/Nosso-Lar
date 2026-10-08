package com.nossolaritapetininga.InstitutoNossoLar.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "redes_sociais")
public class RedeSocial {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_instituicao", nullable = false)
    private Instituicao instituicao;
    private String plataforma;
    @Column(name = "nome_exibicao")
    private String nomeExibicao;
    private String usuario;
    private String url;
    private boolean ativo = true;
    private Integer ordem;
}
