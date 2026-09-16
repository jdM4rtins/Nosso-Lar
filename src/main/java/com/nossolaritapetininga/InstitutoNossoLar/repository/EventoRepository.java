package com.nossolaritapetininga.InstitutoNossoLar.repository;

import com.nossolaritapetininga.InstitutoNossoLar.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findAllByOrderByIdAsc();
}