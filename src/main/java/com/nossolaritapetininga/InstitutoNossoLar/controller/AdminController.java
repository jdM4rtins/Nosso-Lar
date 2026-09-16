package com.nossolaritapetininga.InstitutoNossoLar.controller;

import com.nossolaritapetininga.InstitutoNossoLar.service.AtividadeService;
import com.nossolaritapetininga.InstitutoNossoLar.service.ConteudoService;
import com.nossolaritapetininga.InstitutoNossoLar.service.EventoService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AtividadeService atividadeService;
    private final EventoService eventoService;
    private final ConteudoService conteudoService;

    public AdminController(
            AtividadeService atividadeService,
            EventoService eventoService,
            ConteudoService conteudoService) {

        this.atividadeService = atividadeService;
        this.eventoService = eventoService;
        this.conteudoService = conteudoService;
    }

    @GetMapping
    public String dashboard(Model model) {

        model.addAttribute(
                "quantidadeAtividades",
                atividadeService.listarTodos().size()
        );

        model.addAttribute(
                "quantidadeEventos",
                eventoService.listarTodos().size()
        );

        model.addAttribute(
                "quantidadeConteudos",
                conteudoService.listarTodos().size()
        );

        return "admin/dashboard";
    }
}