package com.nossolaritapetininga.InstitutoNossoLar.controller;

import com.nossolaritapetininga.InstitutoNossoLar.model.Evento;
import com.nossolaritapetininga.InstitutoNossoLar.repository.EventoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ConteudoPublicoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventoRepository eventoRepository;

    @Test
    void exibeEventoAtivoNaPaginaPublica() throws Exception {
        eventoRepository.save(evento(
                "Segundo encontro", LocalDateTime.of(2026, 11, 15, 14, 30), true));
        eventoRepository.save(evento(
                "Primeiro encontro", LocalDateTime.of(2026, 11, 10, 9, 0), true));
        eventoRepository.save(evento(
                "Evento não publicado", LocalDateTime.of(2026, 11, 8, 9, 0), false));
        eventoRepository.flush();

        String pagina = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("id=\"eventos\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("15/11/2026 · 14:30")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(pagina).doesNotContain("Evento não publicado");
        assertThat(pagina.indexOf("Primeiro encontro"))
                .isLessThan(pagina.indexOf("Segundo encontro"));
    }

    @Test
    @WithMockUser(authorities = {"VIEW_CONTENT", "CREATE_CONTENT"})
    void renderizaFormulariosDeEventoENoticiaComUpload() throws Exception {
        mockMvc.perform(get("/admin/eventos/novo"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("type=\"datetime-local\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"arquivo\"")));

        mockMvc.perform(get("/admin/noticias/novo"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("enctype=\"multipart/form-data\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"arquivo\"")));
    }

    private Evento evento(String titulo, LocalDateTime inicio, boolean ativo) {
        Evento evento = new Evento();
        evento.setTitulo(titulo);
        evento.setDescricao("Evento aberto para toda a comunidade.");
        evento.setDataInicio(inicio);
        evento.setLocal("Instituto Nosso Lar");
        evento.setImagem("NL_Img/eventoNL.png");
        evento.setAtivo(ativo);
        return evento;
    }
}
