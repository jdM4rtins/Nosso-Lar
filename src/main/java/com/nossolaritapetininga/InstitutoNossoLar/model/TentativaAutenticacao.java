package com.nossolaritapetininga.InstitutoNossoLar.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tentativas_autenticacao", uniqueConstraints = @UniqueConstraint(name = "uk_tentativa_chave", columnNames = "chave"))
public class TentativaAutenticacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 320)
    private String chave;

    @Column(nullable = false, length = 30)
    private String tipo;

    @Column(nullable = false)
    private int tentativas;

    @Column(name = "ultima_tentativa", nullable = false)
    private LocalDateTime ultimaTentativa;

    @Column(name = "bloqueado_ate")
    private LocalDateTime bloqueadoAte;
}
