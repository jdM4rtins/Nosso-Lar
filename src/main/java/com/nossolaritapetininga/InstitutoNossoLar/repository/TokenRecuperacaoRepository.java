package com.nossolaritapetininga.InstitutoNossoLar.repository;

import com.nossolaritapetininga.InstitutoNossoLar.model.TokenRecuperacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TokenRecuperacaoRepository extends JpaRepository<TokenRecuperacao, Long> {

    Optional<TokenRecuperacao> findByTokenHash(String tokenHash);

    @Modifying
    @Query("delete from TokenRecuperacao t where t.usuario.id = :usuarioId and t.dataUtilizacao is null")
    void invalidarPendentes(@Param("usuarioId") Long usuarioId);

    @Modifying
    @Query("delete from TokenRecuperacao t where t.dataExpiracao < :limite or t.dataUtilizacao is not null")
    int removerInutilizados(@Param("limite") LocalDateTime limite);
}
