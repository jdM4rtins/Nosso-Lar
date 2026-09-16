package com.nossolaritapetininga.InstitutoNossoLar.repository;

import com.nossolaritapetininga.InstitutoNossoLar.model.Conteudo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConteudoRepository
        extends JpaRepository<Conteudo, Long> {

    Optional<Conteudo> findByChave(String chave);
}