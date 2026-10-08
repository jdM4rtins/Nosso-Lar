package com.nossolaritapetininga.InstitutoNossoLar.repository;

import com.nossolaritapetininga.InstitutoNossoLar.model.TentativaAutenticacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TentativaAutenticacaoRepository extends JpaRepository<TentativaAutenticacao, Long> {
    Optional<TentativaAutenticacao> findByChave(String chave);
}
