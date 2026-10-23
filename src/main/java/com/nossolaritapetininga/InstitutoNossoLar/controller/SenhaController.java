package com.nossolaritapetininga.InstitutoNossoLar.controller;

import com.nossolaritapetininga.InstitutoNossoLar.service.AdministradorService;
import com.nossolaritapetininga.InstitutoNossoLar.service.RecuperacaoSenhaService;
import com.nossolaritapetininga.InstitutoNossoLar.service.ProtecaoAutenticacaoService;
import com.nossolaritapetininga.InstitutoNossoLar.exception.RegraNegocioException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

@Controller
public class SenhaController {

    private static final Pattern EMAIL_VALIDO = Pattern.compile(
            "^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)+$");

    private final AdministradorService administradorService;
    private final RecuperacaoSenhaService recuperacaoSenhaService;
    private final ProtecaoAutenticacaoService protecaoAutenticacaoService;

    public SenhaController(
            AdministradorService administradorService,
            RecuperacaoSenhaService recuperacaoSenhaService,
            ProtecaoAutenticacaoService protecaoAutenticacaoService) {
        this.administradorService = administradorService;
        this.recuperacaoSenhaService = recuperacaoSenhaService;
        this.protecaoAutenticacaoService = protecaoAutenticacaoService;
    }

    @GetMapping("/alterar-senha")
    public String formulario() {
        return "alterar-senha";
    }

    @PostMapping("/alterar-senha")
    public String alterarSenha(
            Authentication authentication,
            @RequestParam String novaSenha,
            @RequestParam String confirmacao,
            Model model) {

        String erro = administradorService.alterarPropriaSenha(
                authentication.getName(), novaSenha, confirmacao);

        if (erro != null) {
            model.addAttribute("erro", erro);
            return "alterar-senha";
        }

        return "redirect:/admin/dashboard?senhaAlterada";
    }

    @GetMapping("/recuperar-senha")
    public String recuperarFormulario() {
        return "recuperar-senha";
    }

    @PostMapping("/recuperar-senha")
    public String solicitarRecuperacao(
            @RequestParam String email,
            HttpServletRequest request,
            Model model) {
        String emailNormalizado = email == null ? "" : email.trim();
        if (!EMAIL_VALIDO.matcher(emailNormalizado).matches()) {
            model.addAttribute("erro", "Informe um endereço de e-mail válido.");
            model.addAttribute("email", emailNormalizado);
            return "recuperar-senha";
        }
        try {
            if (protecaoAutenticacaoService.registrarTentativaRecuperacao(
                    emailNormalizado, request.getRemoteAddr())) {
                recuperacaoSenhaService.solicitar(emailNormalizado);
            }
        } catch (RegraNegocioException ex) {
            // O usuário recebe uma resposta genérica; o detalhe fica no log do servidor.
            org.slf4j.LoggerFactory.getLogger(SenhaController.class)
                    .warn("Não foi possível processar a solicitação de recuperação de senha.", ex);
        }
        model.addAttribute("solicitado", true);
        return "recuperar-senha";
    }

    @GetMapping("/redefinir-senha")
    public String redefinirFormulario(@RequestParam(required = false) String token, Model model) {
        model.addAttribute("token", token);
        model.addAttribute("tokenValido", recuperacaoSenhaService.tokenValido(token));
        return "redefinir-senha";
    }

    @PostMapping("/redefinir-senha")
    public String redefinirSenha(
            @RequestParam String token,
            @RequestParam String novaSenha,
            @RequestParam String confirmacao,
            Model model) {
        try {
            recuperacaoSenhaService.redefinir(token, novaSenha, confirmacao);
            return "redirect:/login?senhaRedefinida";
        } catch (RegraNegocioException ex) {
            model.addAttribute("token", token);
            model.addAttribute("tokenValido", recuperacaoSenhaService.tokenValido(token));
            model.addAttribute("erro", ex.getMessage());
            return "redefinir-senha";
        }
    }
}
