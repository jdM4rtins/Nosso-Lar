package com.nossolaritapetininga.InstitutoNossoLar.model;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Entity
@Table(name = "eventos")
public class Evento extends EntidadeAuditavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imagem;

    private String alt;

    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "data_inicio")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dataInicio;

    @Column(name = "data_fim")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dataFim;

    private String local;

    private String link;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_instituicao")
    private Instituicao instituicao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_midia")
    private Midia midia;

    private boolean ativo;

    public Evento() {
    }

    public Evento(String imagem, String alt, boolean ativo) {
        this.imagem = imagem;
        this.alt = alt;
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }
    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
    public Instituicao getInstituicao() { return instituicao; }
    public void setInstituicao(Instituicao instituicao) { this.instituicao = instituicao; }
    public Midia getMidia() { return midia; }
    public void setMidia(Midia midia) { this.midia = midia; }
}
