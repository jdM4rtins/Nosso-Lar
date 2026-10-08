package com.nossolaritapetininga.InstitutoNossoLar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nossolaritapetininga.InstitutoNossoLar.model.Usuario;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdministradorRepository
        extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    @Query("""
            select count(distinct u.id)
            from Usuario u join u.perfis p
            where u.ativo = true and p.nome = :perfil
            """)
    long contarAtivosPorPerfil(@Param("perfil") String perfil);
}
