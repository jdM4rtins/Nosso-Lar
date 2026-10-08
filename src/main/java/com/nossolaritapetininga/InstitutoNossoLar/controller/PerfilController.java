package com.nossolaritapetininga.InstitutoNossoLar.controller;

import com.nossolaritapetininga.InstitutoNossoLar.exception.RegraNegocioException;
import com.nossolaritapetininga.InstitutoNossoLar.model.Perfil;
import com.nossolaritapetininga.InstitutoNossoLar.model.Permissao;
import com.nossolaritapetininga.InstitutoNossoLar.service.PerfilService;
import com.nossolaritapetininga.InstitutoNossoLar.security.SessaoUsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/perfis")
@PreAuthorize("hasRole('SUPER_ADMIN') and hasAuthority('MANAGE_PERMISSIONS')")
public class PerfilController {

    private final PerfilService perfilService;
    private final SessaoUsuarioService sessaoUsuarioService;

    public PerfilController(
            PerfilService perfilService,
            SessaoUsuarioService sessaoUsuarioService) {
        this.perfilService = perfilService;
        this.sessaoUsuarioService = sessaoUsuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("perfis", perfilService.listarPerfis());
        return "admin/perfis";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Perfil perfil = perfilService.buscarPerfil(id);
        model.addAttribute("perfil", perfil);
        model.addAttribute("permissoes", perfilService.listarPermissoes());
        model.addAttribute("selecionadas", perfil.getPermissoes().stream()
                .map(Permissao::getId).toList());
        return "admin/editar-perfil";
    }

    @PostMapping("/editar/{id}")
    public String atualizar(
            @PathVariable Long id,
            @RequestParam(required = false) String descricao,
            @RequestParam(required = false, name = "permissoes") List<Long> permissoes,
            RedirectAttributes redirectAttributes) {
        try {
            perfilService.atualizarPermissoes(id, descricao, permissoes);
            sessaoUsuarioService.expirarTodasAsSessoes();
            redirectAttributes.addFlashAttribute("sucesso", "Permissões atualizadas.");
        } catch (RegraNegocioException exception) {
            redirectAttributes.addFlashAttribute("erro", exception.getMessage());
        }
        return "redirect:/admin/perfis";
    }
}
