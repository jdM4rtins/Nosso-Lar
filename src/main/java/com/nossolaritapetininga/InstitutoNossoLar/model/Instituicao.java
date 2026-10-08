package com.nossolaritapetininga.InstitutoNossoLar.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "instituicoes")
public class Instituicao extends EntidadeAuditavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "nome_fantasia")
    private String nomeFantasia;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(unique = true, length = 18)
    private String cnpj;

    @Column(nullable = false)
    private boolean ativo = true;
}
