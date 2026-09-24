package com.nossolaritapetininga.InstitutoNossoLar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nossolaritapetininga.InstitutoNossoLar.model.Usuario;

import java.util.Optional;

public interface AdministradorRepository
        extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);
}
