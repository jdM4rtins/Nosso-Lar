package com.nossolaritapetininga.InstitutoNossoLar.repository;

import com.nossolaritapetininga.InstitutoNossoLar.model.Noticia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticiaRepository extends JpaRepository<Noticia, Long> {

    List<Noticia> findByAtivoTrueOrderByDataPublicacaoDesc();

}