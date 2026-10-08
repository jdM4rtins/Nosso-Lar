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
@Table(name = "configuracoes_site")
public class ConfiguracaoSite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_instituicao", nullable = false, unique = true)
    private Instituicao instituicao;

    @Column(name = "nome_site")
    private String nomeSite;

    @Column(name = "titulo_site")
    private String tituloSite;

    @Column(name = "cor_primaria", length = 20)
    private String corPrimaria;

    @Column(name = "cor_secundaria", length = 20)
    private String corSecundaria;

    @Column(name = "cor_texto", length = 20)
    private String corTexto;

    @Column(name = "cor_fundo", length = 20)
    private String corFundo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "logo_id")
    private Midia logo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "favicon_id")
    private Midia favicon;

    @Column(name = "meta_description")
    private String metaDescription;

    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao = LocalDateTime.now();

    @PreUpdate
    void atualizarData() {
        dataAtualizacao = LocalDateTime.now();
    }
}
