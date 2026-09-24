package com.nossolaritapetininga.InstitutoNossoLar.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
@Entity @Table(name = "doacoes")
public class Doacao {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_instituicao", nullable = false)
    private Instituicao instituicao;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qr_code_midia_id")
    private Midia qrCodeMidia;
    @Column(name = "chave_pix", nullable = false)
    private String chavePix;
    @Column(name = "tipo_chave_pix", nullable = false)
    private String tipoChavePix;
    @Column(name = "nome_beneficiario")
    private String nomeBeneficiario;
    private String cidade;
    private boolean ativo = true;
    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao = LocalDateTime.now();
}
