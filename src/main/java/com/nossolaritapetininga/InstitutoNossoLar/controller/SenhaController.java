package com.nossolaritapetininga.InstitutoNossoLar.controller;

import com.nossolaritapetininga.InstitutoNossoLar.service.AdministradorService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SenhaController {

    private final AdministradorService administradorService;

    public SenhaController(AdministradorService administradorService) {
        this.administradorService = administradorService;
    }

    @GetMapping("/alterar-senha")
    public String formulario() {
        return "alterar-senha";
    }

    @PostMapping("/alterar-senha")
    public String alterarSenha(
            Authentication authentication,
            @RequestParam String senhaAtual,
            @RequestParam String novaSenha,
            @RequestParam String confirmacao,
            Model model) {

        String erro = administradorService.alterarPropriaSenha(
                authentication.getName(), senhaAtual, novaSenha, confirmacao);

        if (erro != null) {
            model.addAttribute("erro", erro);
            return "alterar-senha";
        }

        return "redirect:/admin/dashboard?senhaAlterada";
    }
}
