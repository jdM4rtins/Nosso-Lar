package com.nossolaritapetininga.InstitutoNossoLar.controller;

import com.nossolaritapetininga.InstitutoNossoLar.service.EventoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AcessibilidadeController {

    private final EventoService eventoService;

    public AcessibilidadeController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @GetMapping("/acessibilidade")
    public String acessibilidade(Model model) {
        model.addAttribute("temEventos", !eventoService.listarAtivosOrdenados().isEmpty());
        return "acessibilidade";
    }
}
