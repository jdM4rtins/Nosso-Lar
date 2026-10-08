package com.nossolaritapetininga.InstitutoNossoLar.controller;

import com.nossolaritapetininga.InstitutoNossoLar.exception.RegraNegocioException;
import com.nossolaritapetininga.InstitutoNossoLar.model.Atividade;
import com.nossolaritapetininga.InstitutoNossoLar.model.Midia;
import com.nossolaritapetininga.InstitutoNossoLar.service.AtividadeService;
import com.nossolaritapetininga.InstitutoNossoLar.service.ImagemService;

import org.springframework.stereotype.Controller;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/atividades")
@PreAuthorize("hasAuthority('VIEW_CONTENT')")
public class AtividadeController {

    private final AtividadeService atividadeService;
    private final ImagemService imagemService;

    public AtividadeController(
            AtividadeService atividadeService,
            ImagemService imagemService) {

        this.atividadeService = atividadeService;
        this.imagemService = imagemService;
    }

    @GetMapping
    public String listar(Model model) {

        model.addAttribute(
                "atividades",
                atividadeService.listarTodos()
        );

        return "admin/atividades";
    }

    @GetMapping("/novo")
    @PreAuthorize("hasAuthority('CREATE_CONTENT')")
    public String novo(Model model) {

        model.addAttribute(
                "atividade",
                new Atividade()
        );

        return "admin/nova-atividade";
    }

    @PostMapping({"", "/"})
    @PreAuthorize("hasAuthority('CREATE_CONTENT')")
    public String salvar(
            @ModelAttribute Atividade atividade,
            @RequestParam(value = "arquivo", required = false) MultipartFile arquivo,
            RedirectAttributes redirectAttributes) {

        try {
            Midia midia = imagemService.salvar(arquivo, atividade.getNome());
            atividade.setMidia(midia);
            atividade.setImagem(imagemService.url(midia));
        } catch (RegraNegocioException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
            return "redirect:/admin/atividades/novo";
        }

        atividadeService.salvar(atividade);

        return "redirect:/admin/atividades";
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("hasAuthority('UPDATE_CONTENT')")
    public String editar(
            @PathVariable Long id,
            Model model) {

        Atividade atividade =
                atividadeService.buscarPorId(id);

        if (atividade == null) {
            return "redirect:/admin/atividades";
        }

        model.addAttribute(
                "atividade",
                atividade
        );

        return "admin/editar-atividade";
    }

    @PostMapping("/editar/{id}")
    @PreAuthorize("hasAuthority('UPDATE_CONTENT')")
    public String atualizar(
            @PathVariable Long id,
            @RequestParam(required = false) MultipartFile arquivo,
            @RequestParam String nome,
            @RequestParam(defaultValue = "false") boolean ativo,
            RedirectAttributes redirectAttributes) {

        Atividade atividade =
                atividadeService.buscarPorId(id);

        if (atividade == null) {
            return "redirect:/admin/atividades";
        }

        Midia midiaAntiga = atividade.getMidia();
        if (arquivo != null && !arquivo.isEmpty()) {
            try {
                Midia novaMidia = imagemService.salvar(arquivo, nome);
                atividade.setMidia(novaMidia);
                atividade.setImagem(imagemService.url(novaMidia));
            } catch (RegraNegocioException ex) {
                redirectAttributes.addFlashAttribute("erro", ex.getMessage());
                return "redirect:/admin/atividades/editar/" + id;
            }
        }

        atividade.setNome(nome);
        atividade.setAtivo(ativo);

        atividadeService.salvar(atividade);

        if (arquivo != null && !arquivo.isEmpty()) {
            imagemService.excluir(midiaAntiga);
        }

        return "redirect:/admin/atividades";
    }

    @PostMapping("/excluir/{id}")
    @PreAuthorize("hasAuthority('DELETE_CONTENT')")
    public String excluir(
            @PathVariable Long id) {

        Atividade atividade = atividadeService.buscarPorId(id);
        atividadeService.excluir(id);

        if (atividade != null) {
            imagemService.excluir(atividade.getMidia());
        }

        return "redirect:/admin/atividades";
    }
}
