package com.nossolaritapetininga.InstitutoNossoLar.controller;

import com.nossolaritapetininga.InstitutoNossoLar.model.Administrador;
import com.nossolaritapetininga.InstitutoNossoLar.repository.AdministradorRepository;
import com.nossolaritapetininga.InstitutoNossoLar.service.AdministradorService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/administradores")
public class AdministradorController {

    private final AdministradorService administradorService;
    private final AdministradorRepository administradorRepository;

    public AdministradorController(
            AdministradorService administradorService,
            AdministradorRepository administradorRepository) {

        this.administradorService = administradorService;
        this.administradorRepository = administradorRepository;
    }

    @GetMapping
    public String listarAdministradores(Model model) {

        model.addAttribute(
                "administradores",
                administradorService.listarTodos()
        );

        return "admin/administradores";
    }

    @GetMapping("/novo")
    public String novoAdministrador(Model model) {

        model.addAttribute(
                "administrador",
                new Administrador()
        );

        return "admin/novo-administrador";
    }

    @PostMapping
    public String salvarAdministrador(
            @ModelAttribute Administrador administrador,
            Model model) {

        if (administradorRepository
                .findByEmail(administrador.getEmail())
                .isPresent()) {

            model.addAttribute(
                    "erro",
                    "Já existe um administrador com este e-mail."
            );

            return "admin/novo-administrador";
        }

        administradorService.salvar(administrador);

        return "redirect:/admin/administradores";
    }

    @GetMapping("/editar/{id}")
    public String editarAdministrador(
            @PathVariable Long id,
            Model model) {

        Administrador administrador =
                administradorService.buscarPorId(id);

        if (administrador == null) {
            return "redirect:/admin/administradores";
        }

        model.addAttribute(
                "administrador",
                administrador
        );

        return "admin/editar-administrador";
    }

    @PostMapping("/editar/{id}")
    public String atualizarAdministrador(
            @PathVariable Long id,
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam(required = false) String senha) {

        administradorService.editar(
                id,
                nome,
                email,
                senha
        );

        return "redirect:/admin/administradores";
    }

    @PostMapping("/status/{id}")
    public String alterarStatus(@PathVariable Long id) {

        administradorService.alterarStatus(id);

        return "redirect:/admin/administradores";
    }

    @PostMapping("/excluir/{id}")
    public String excluirAdministrador(
            @PathVariable Long id) {

        if (administradorService.listarTodos().size() <= 1) {
            return "redirect:/admin/administradores";
        }

        administradorService.excluir(id);

        return "redirect:/admin/administradores";
    }

}