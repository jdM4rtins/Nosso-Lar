package com.nossolaritapetininga.InstitutoNossoLar.controller;

import com.nossolaritapetininga.InstitutoNossoLar.model.Conteudo;
import com.nossolaritapetininga.InstitutoNossoLar.service.ConteudoService;

import org.springframework.stereotype.Controller;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/conteudos")
@PreAuthorize("hasAuthority('VIEW_CONTENT')")
public class ConteudoController {

    private final ConteudoService conteudoService;

    public ConteudoController(ConteudoService conteudoService) {
        this.conteudoService = conteudoService;
    }

    @GetMapping
    public String listar(Model model) {

        model.addAttribute(
                "conteudos",
                conteudoService.listarTodos()
        );

        return "admin/conteudos";
    }

    @GetMapping("/novo")
    @PreAuthorize("hasAuthority('CREATE_CONTENT')")
    public String novo(Model model) {

        model.addAttribute(
                "conteudo",
                new Conteudo()
        );

        return "admin/novo-conteudo";
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_CONTENT')")
    public String salvar(
            @ModelAttribute Conteudo conteudo) {

        conteudoService.salvar(conteudo);

        return "redirect:/admin/conteudos";
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("hasAuthority('UPDATE_CONTENT')")
    public String editar(
            @PathVariable Long id,
            Model model) {

        Conteudo conteudo =
                conteudoService.buscarPorId(id);

        if (conteudo == null) {
            return "redirect:/admin/conteudos";
        }

        model.addAttribute(
                "conteudo",
                conteudo
        );

        return "admin/editar-conteudo";
    }

    @PostMapping("/editar/{id}")
    @PreAuthorize("hasAuthority('UPDATE_CONTENT')")
    public String atualizar(
            @PathVariable Long id,
            @RequestParam String chave,
            @RequestParam String titulo,
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) String textoExtra,
            @RequestParam(required = false) String imagem,
            @RequestParam(required = false) String link,
            @RequestParam(defaultValue = "false") boolean ativo) {

        conteudoService.atualizar(
                id,
                chave,
                titulo,
                texto,
                textoExtra,
                imagem,
                link,
                ativo
        );

        return "redirect:/admin/conteudos";
    }
}
