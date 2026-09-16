package com.nossolaritapetininga.InstitutoNossoLar.repository;

import com.nossolaritapetininga.InstitutoNossoLar.model.Atividade;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AtividadeRepository
        extends JpaRepository<Atividade, Long> {

    List<Atividade> findAllByOrderByIdAsc();
}