package com.nossolaritapetininga.InstitutoNossoLar.repository;

import com.nossolaritapetininga.InstitutoNossoLar.model.LogAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LogAuditoriaRepository extends JpaRepository<LogAuditoria, Long> {
    @Query(value = """
            SELECT l.data_hora AS "dataHora",
                   COALESCE(target.nome || ' (' || target.email || ')',
                            'Usuário removido (ID ' || COALESCE(l.id_registro::text, 'desconhecido') || ')') AS "usuarioAfetado",
                   COALESCE(actor.email, 'Administrador removido') AS "responsavel",
                   COALESCE(l.ip, '—') AS "ip"
            FROM logs_auditoria l
            LEFT JOIN administradores actor ON actor.id = l.id_usuario
            LEFT JOIN administradores target ON target.id = l.id_registro
            WHERE l.acao = :acao
            ORDER BY l.data_hora DESC
            LIMIT 100
            """, nativeQuery = true)
    List<HistoricoRedefinicaoSenhaProjection> buscarHistoricoRedefinicoes(@Param("acao") String acao);
}
