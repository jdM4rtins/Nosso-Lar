package com.nossolaritapetininga.InstitutoNossoLar.controller;

import com.nossolaritapetininga.InstitutoNossoLar.model.Atividade;
import com.nossolaritapetininga.InstitutoNossoLar.service.AtividadeService;
import com.nossolaritapetininga.InstitutoNossoLar.service.ImagemService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/admin/atividades")
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
    public String novo(Model model) {

        model.addAttribute(
                "atividade",
                new Atividade()
        );

        return "admin/nova-atividade";
    }

    @PostMapping({"", "/"})
    public String salvar(
            @ModelAttribute Atividade atividade,
            @RequestParam(value = "arquivo", required = false) MultipartFile arquivo,
            RedirectAttributes redirectAttributes)
            throws IOException {

        if (arquivo == null || arquivo.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Selecione uma imagem para cadastrar a atividade."
            );
            return "redirect:/admin/atividades/novo";
        }

        String caminhoImagem =
                imagemService.salvar(arquivo);

        if (caminhoImagem != null) {
            atividade.setImagem(caminhoImagem);
        }

        atividadeService.salvar(atividade);

        return "redirect:/admin/atividades";
    }

    @GetMapping("/editar/{id}")
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
    public String atualizar(
            @PathVariable Long id,
            @RequestParam(required = false) MultipartFile arquivo,
            @RequestParam String nome,
            @RequestParam(defaultValue = "false") boolean ativo)
            throws IOException {

        Atividade atividade =
                atividadeService.buscarPorId(id);

        if (atividade == null) {
            return "redirect:/admin/atividades";
        }

        if (arquivo != null && !arquivo.isEmpty()) {

            String novaImagem =
                    imagemService.salvar(arquivo);

            atividade.setImagem(novaImagem);
        }

        atividade.setNome(nome);
        atividade.setAtivo(ativo);

        atividadeService.salvar(atividade);

        return "redirect:/admin/atividades";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(
            @PathVariable Long id) {

        Atividade atividade = atividadeService.buscarPorId(id);
        atividadeService.excluir(id);

        if (atividade != null) {
            try {
                imagemService.excluir(atividade.getImagem());
            } catch (IOException ignored) {
                // O registro foi excluido; uma imagem residual nao bloqueia a operacao.
            }
        }

        return "redirect:/admin/atividades";
    }
}
