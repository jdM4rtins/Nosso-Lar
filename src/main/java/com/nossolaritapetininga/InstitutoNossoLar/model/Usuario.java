package com.nossolaritapetininga.InstitutoNossoLar.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "administradores")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "senha", nullable = false)
    private String senhaHash;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(name = "alterar_senha")
    private Boolean alterarSenha = false;

    @Column(name = "ultimo_login")
    private LocalDateTime ultimoLogin;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao = LocalDateTime.now();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuarios_perfis",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_perfil"))
    private Set<Perfil> perfis = new LinkedHashSet<>();

    public String getSenha() {
        return senhaHash;
    }

    public void setSenha(String senha) {
        this.senhaHash = senha;
    }

    public LocalDateTime getDataCadastro() {
        return dataCriacao;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCriacao = dataCadastro;
    }

    @PreUpdate
    void atualizarData() {
        dataAtualizacao = LocalDateTime.now();
    }
}
