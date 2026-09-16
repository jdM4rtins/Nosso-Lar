package com.nossolaritapetininga.InstitutoNossoLar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nossolaritapetininga.InstitutoNossoLar.model.Administrador;

import java.util.Optional;

public interface AdministradorRepository
        extends JpaRepository<Administrador, Long> {

    Optional<Administrador> findByEmail(String email);
}