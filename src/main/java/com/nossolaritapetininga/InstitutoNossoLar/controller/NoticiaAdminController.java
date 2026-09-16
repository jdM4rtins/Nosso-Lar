package com.nossolaritapetininga.InstitutoNossoLar.controller;

import com.nossolaritapetininga.InstitutoNossoLar.model.Noticia;
import com.nossolaritapetininga.InstitutoNossoLar.service.NoticiaService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/noticias")
public class NoticiaAdminController {

    private final NoticiaService noticiaService;

    public NoticiaAdminController(NoticiaService noticiaService) {
        this.noticiaService = noticiaService;
    }


    @GetMapping
    public String listar(Model model) {

        model.addAttribute(
                "noticias",
                noticiaService.listarTodas()
        );

        return "admin/noticias";
    }


    @GetMapping("/novo")
    public String novo(Model model) {

        Noticia noticia = new Noticia();

        noticia.setDataPublicacao(LocalDate.now());
        noticia.setAtivo(true);

        model.addAttribute("noticia", noticia);

        return "admin/noticia-form";
    }


    @PostMapping
    public String salvar(@ModelAttribute Noticia noticia) {

        noticiaService.salvar(noticia);

        return "redirect:/admin/noticias";
    }


    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Long id,
            Model model) {

        Noticia noticia = noticiaService
                .buscarPorId(id)
                .orElseThrow();

        model.addAttribute("noticia", noticia);

        return "admin/noticia-form";
    }


    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {

        noticiaService.excluir(id);

        return "redirect:/admin/noticias";
    }
}