package com.nossolaritapetininga.InstitutoNossoLar.controller;

import com.nossolaritapetininga.InstitutoNossoLar.exception.RegraNegocioException;
import com.nossolaritapetininga.InstitutoNossoLar.model.Midia;
import com.nossolaritapetininga.InstitutoNossoLar.model.Noticia;
import com.nossolaritapetininga.InstitutoNossoLar.service.ImagemService;
import com.nossolaritapetininga.InstitutoNossoLar.service.NoticiaService;

import org.springframework.stereotype.Controller;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/noticias")
@PreAuthorize("hasAuthority('VIEW_CONTENT')")
public class NoticiaAdminController {

    private final NoticiaService noticiaService;
    private final ImagemService imagemService;

    public NoticiaAdminController(NoticiaService noticiaService, ImagemService imagemService) {
        this.noticiaService = noticiaService;
        this.imagemService = imagemService;
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
    public String salvar(
            @ModelAttribute Noticia noticia,
            @RequestParam(value = "arquivo", required = false) MultipartFile arquivo,
            RedirectAttributes redirectAttributes) {

        try {
            noticia.setId(null);
            noticia.setInstituicao(null);
            noticia.setMidia(null);
            noticia.setImagem(null);
            validar(noticia);
            if (arquivo != null && !arquivo.isEmpty()) {
                Midia midia = imagemService.salvar(arquivo, noticia.getTitulo());
                noticia.setMidia(midia);
                noticia.setImagem(imagemService.url(midia));
            }
        } catch (RegraNegocioException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
            return "redirect:/admin/noticias/novo";
        }

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
            @ModelAttribute Noticia dados,
            @RequestParam(value = "arquivo", required = false) MultipartFile arquivo,
            @RequestParam(defaultValue = "false") boolean removerImagem,
            RedirectAttributes redirectAttributes) {

        Noticia noticia = noticiaService.buscarPorId(id).orElseThrow();
        Midia midiaAntiga = noticia.getMidia();

        try {
            validar(dados);
            if (arquivo != null && !arquivo.isEmpty()) {
                Midia novaMidia = imagemService.salvar(arquivo, dados.getTitulo());
                noticia.setMidia(novaMidia);
                noticia.setImagem(imagemService.url(novaMidia));
            } else if (removerImagem) {
                noticia.setMidia(null);
                noticia.setImagem(null);
            }
        } catch (RegraNegocioException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
            return "redirect:/admin/noticias/editar/" + id;
        }

        noticia.setTitulo(dados.getTitulo());
        noticia.setResumo(dados.getResumo());
        noticia.setConteudo(dados.getConteudo());
        noticia.setDataPublicacao(dados.getDataPublicacao());
        noticia.setAtivo(dados.isAtivo());
        noticiaService.salvar(noticia);

        if ((arquivo != null && !arquivo.isEmpty()) || removerImagem) {
            imagemService.excluir(midiaAntiga);
        }

        return "redirect:/admin/noticias";
    }

    @PostMapping("/excluir/{id}")
    @PreAuthorize("hasAuthority('DELETE_CONTENT')")
    public String excluir(@PathVariable Long id) {

        Noticia noticia = noticiaService.buscarPorId(id).orElse(null);
        noticiaService.excluir(id);

        if (noticia != null) {
            imagemService.excluir(noticia.getMidia());
        }

        return "redirect:/admin/noticias";
    }

    private void validar(Noticia noticia) {
        if (noticia.getTitulo() == null || noticia.getTitulo().isBlank()) {
            throw new RegraNegocioException("Informe o título da notícia.");
        }
    }
}
