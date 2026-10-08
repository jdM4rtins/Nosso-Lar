package com.nossolaritapetininga.InstitutoNossoLar.controller;

import com.nossolaritapetininga.InstitutoNossoLar.model.Noticia;
import com.nossolaritapetininga.InstitutoNossoLar.service.NoticiaService;

import org.springframework.stereotype.Controller;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/noticias")
@PreAuthorize("hasAuthority('VIEW_CONTENT')")
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
    @PreAuthorize("hasAuthority('CREATE_CONTENT')")
    public String novo(Model model) {

        Noticia noticia = new Noticia();

        noticia.setDataPublicacao(LocalDate.now());
        noticia.setAtivo(true);

        model.addAttribute("noticia", noticia);

        return "admin/noticia-form";
    }


    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_CONTENT')")
    public String salvar(@ModelAttribute Noticia noticia) {

        noticiaService.salvar(noticia);

        return "redirect:/admin/noticias";
    }


    @GetMapping("/editar/{id}")
    @PreAuthorize("hasAuthority('UPDATE_CONTENT')")
    public String editar(
            @PathVariable Long id,
            Model model) {

        Noticia noticia = noticiaService
                .buscarPorId(id)
                .orElseThrow();

        model.addAttribute("noticia", noticia);

        return "admin/noticia-form";
    }


    @PostMapping("/editar/{id}")
    @PreAuthorize("hasAuthority('UPDATE_CONTENT')")
    public String atualizar(
            @PathVariable Long id,
            @ModelAttribute Noticia dados) {

        Noticia noticia = noticiaService.buscarPorId(id).orElseThrow();
        noticia.setTitulo(dados.getTitulo());
        noticia.setResumo(dados.getResumo());
        noticia.setConteudo(dados.getConteudo());
        noticia.setImagem(dados.getImagem());
        noticia.setDataPublicacao(dados.getDataPublicacao());
        noticia.setAtivo(dados.isAtivo());
        noticiaService.salvar(noticia);
        return "redirect:/admin/noticias";
    }

    @PostMapping("/excluir/{id}")
    @PreAuthorize("hasAuthority('DELETE_CONTENT')")
    public String excluir(@PathVariable Long id) {

        noticiaService.excluir(id);

        return "redirect:/admin/noticias";
    }
}
