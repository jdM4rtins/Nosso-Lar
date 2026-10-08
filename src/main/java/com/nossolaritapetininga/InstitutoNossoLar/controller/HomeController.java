package com.nossolaritapetininga.InstitutoNossoLar.controller;

import com.nossolaritapetininga.InstitutoNossoLar.model.Conteudo;
import com.nossolaritapetininga.InstitutoNossoLar.service.ConteudoService;
import com.nossolaritapetininga.InstitutoNossoLar.service.NoticiaService;
import com.nossolaritapetininga.InstitutoNossoLar.service.AtividadeService;
import com.nossolaritapetininga.InstitutoNossoLar.service.EventoService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ConteudoService conteudoService;
    private final AtividadeService atividadeService;
    private final EventoService eventoService;
    private final NoticiaService noticiaService;

   public HomeController(
            NoticiaService noticiaService,
            ConteudoService conteudoService,
            AtividadeService atividadeService,
            EventoService eventoService) {

        this.conteudoService = conteudoService;
        this.atividadeService = atividadeService;
        this.eventoService = eventoService;
        this.noticiaService = noticiaService;
    }

    @GetMapping("/")
    public String home(Model model) {

        Conteudo inicio =
                conteudoService.buscarPorChave("inicio");

        Conteudo sobre =
            conteudoService.buscarPorChave("sobre");

        Conteudo telefone =
                conteudoService.buscarPorChave("contato_telefone");

        Conteudo whatsapp =
                conteudoService.buscarPorChave("contato_whatsapp");

        Conteudo instagram =
                conteudoService.buscarPorChave("contato_instagram");

        Conteudo facebook =
                conteudoService.buscarPorChave("contato_facebook");

        Conteudo horario =
                conteudoService.buscarPorChave("contato_horario");

        Conteudo endereco =
                conteudoService.buscarPorChave("contato_endereco");

        Conteudo mapa =
                conteudoService.buscarPorChave("contato_mapa");

        model.addAttribute("inicio", inicio);
        model.addAttribute("sobre", sobre);
        model.addAttribute(
                "noticias",
                noticiaService.listarAtivas()
        );

        model.addAttribute(
            "atividades",
            atividadeService.listarTodos()
        );

        model.addAttribute(
                "eventos",
                eventoService.listarAtivosOrdenados()
        );

        model.addAttribute("telefone", telefone);
        model.addAttribute("whatsapp", whatsapp);
        model.addAttribute("instagram", instagram);
        model.addAttribute("facebook", facebook);
        model.addAttribute("horario", horario);
        model.addAttribute("endereco", endereco);
        model.addAttribute("mapa", mapa);

        return "index";
    }

}
