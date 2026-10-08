package com.nossolaritapetininga.InstitutoNossoLar.model;

import jakarta.persistence.*;

@Entity
@Table(name = "atividades")
public class Atividade extends EntidadeAuditavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String imagem;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    private Integer ordem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_instituicao")
    private Instituicao instituicao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_midia")
    private Midia midia;

    private boolean ativo;

    public Atividade() {
    }

    public Atividade(String nome, String imagem, boolean ativo) {
        this.nome = nome;
        this.imagem = imagem;
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Integer getOrdem() { return ordem; }
    public void setOrdem(Integer ordem) { this.ordem = ordem; }
    public Instituicao getInstituicao() { return instituicao; }
    public void setInstituicao(Instituicao instituicao) { this.instituicao = instituicao; }
    public Midia getMidia() { return midia; }
    public void setMidia(Midia midia) { this.midia = midia; }
}
