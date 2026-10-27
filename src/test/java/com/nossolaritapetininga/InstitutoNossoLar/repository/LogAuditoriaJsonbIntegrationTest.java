package com.nossolaritapetininga.InstitutoNossoLar.repository;

import com.nossolaritapetininga.InstitutoNossoLar.model.LogAuditoria;
import com.nossolaritapetininga.InstitutoNossoLar.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.annotations.ColumnTransformer;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class LogAuditoriaJsonbIntegrationTest {

    @Autowired
    private LogAuditoriaRepository logAuditoriaRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    void todaColunaJsonbPossuiConversaoExplicitaNoMapeamento() {
        var camposJsonb = entityManagerFactory.getMetamodel().getEntities().stream()
                .flatMap(entidade -> Arrays.stream(entidade.getJavaType().getDeclaredFields()))
                .filter(campo -> {
                    Column coluna = campo.getAnnotation(Column.class);
                    return coluna != null && coluna.columnDefinition().equalsIgnoreCase("jsonb");
                })
                .toList();

        assertThat(camposJsonb).isNotEmpty();
        assertThat(camposJsonb).allSatisfy(this::validarMapeamentoJsonb);
    }

    @Test
    void persisteJsonbComValoresENulosEConsultaHistorico() {
        Usuario ator = new Usuario();
        ator.setNome("Administrador de teste");
        ator.setEmail("auditoria-teste@nossolar.com.br");
        ator.setSenha("hash-de-teste");
        ator = administradorRepository.saveAndFlush(ator);

        LogAuditoria comJson = novaAuditoria(ator);
        comJson.setDadosAnteriores("{\"ativo\":false}");
        comJson.setDadosNovos("{\"ativo\":true}");
        comJson = logAuditoriaRepository.saveAndFlush(comJson);

        LogAuditoria semJson = novaAuditoria(ator);
        semJson = logAuditoriaRepository.saveAndFlush(semJson);

        LogAuditoria lidoComJson = logAuditoriaRepository.findById(comJson.getId()).orElseThrow();
        LogAuditoria lidoSemJson = logAuditoriaRepository.findById(semJson.getId()).orElseThrow();

        assertThat(lidoComJson.getDadosAnteriores()).isNotNull();
        assertThat(lidoComJson.getDadosNovos()).isNotNull();
        assertThat(lidoSemJson.getDadosAnteriores()).isNull();
        assertThat(lidoSemJson.getDadosNovos()).isNull();

        HistoricoRedefinicaoSenhaProjection evento = logAuditoriaRepository
                .buscarHistoricoRedefinicoes("REDEFINICAO_SENHA_SUPER_ADMIN")
                .getFirst();
        assertThat(evento.getUsuarioAfetado())
                .isEqualTo("Administrador de teste (auditoria-teste@nossolar.com.br)");
        assertThat(evento.getResponsavel())
                .isEqualTo("auditoria-teste@nossolar.com.br");
    }

    private LogAuditoria novaAuditoria(Usuario ator) {
        LogAuditoria log = new LogAuditoria();
        log.setUsuario(ator);
        log.setEntidade("administradores");
        log.setIdRegistro(ator.getId());
        log.setAcao("REDEFINICAO_SENHA_SUPER_ADMIN");
        return log;
    }

    private void validarMapeamentoJsonb(Field campo) {
        ColumnTransformer conversor = campo.getAnnotation(ColumnTransformer.class);
        assertThat(conversor)
                .as("Campo JSONB %s deve declarar conversão JDBC explícita", campo.getName())
                .isNotNull();
        assertThat(conversor.write().toLowerCase())
                .as("A escrita de %s deve converter o parâmetro para jsonb", campo.getName())
                .contains("::jsonb");
        assertThat(conversor.read().toLowerCase())
                .as("A leitura de %s deve converter jsonb para texto", campo.getName())
                .contains("::text");
    }
}
