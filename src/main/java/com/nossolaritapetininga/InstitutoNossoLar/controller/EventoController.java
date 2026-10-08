package com.nossolaritapetininga.InstitutoNossoLar.controller;

import com.nossolaritapetininga.InstitutoNossoLar.exception.RegraNegocioException;
import com.nossolaritapetininga.InstitutoNossoLar.model.Evento;
import com.nossolaritapetininga.InstitutoNossoLar.model.Midia;
import com.nossolaritapetininga.InstitutoNossoLar.service.EventoService;
import com.nossolaritapetininga.InstitutoNossoLar.service.ImagemService;

import org.springframework.stereotype.Controller;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/eventos")
@PreAuthorize("hasAuthority('VIEW_CONTENT')")
public class EventoController {

    private final EventoService eventoService;
    private final ImagemService imagemService;

    public EventoController(EventoService eventoService, ImagemService imagemService) {
        this.eventoService = eventoService;
        this.imagemService = imagemService;
    }

    @GetMapping
    public String listar(Model model) {

        model.addAttribute(
                "eventos",
                eventoService.listarTodos()
        );

        return "admin/eventos";
    }

    @GetMapping("/novo")
    @PreAuthorize("hasAuthority('CREATE_CONTENT')")
    public String novo(Model model) {

        model.addAttribute(
                "evento",
                new Evento()
        );

        return "admin/novo-evento";
    }

    @PostMapping({"", "/"})
    @PreAuthorize("hasAuthority('CREATE_CONTENT')")
    public String salvar(
            @ModelAttribute Evento evento,
            @RequestParam(value = "arquivo", required = false) MultipartFile arquivo,
            RedirectAttributes redirectAttributes) {

        try {
            Midia midia = imagemService.salvar(arquivo, evento.getAlt());
            evento.setMidia(midia);
            evento.setImagem(imagemService.url(midia));
        } catch (RegraNegocioException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
            return "redirect:/admin/eventos/novo";
        }

        eventoService.salvar(evento);

        return "redirect:/admin/eventos";
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("hasAuthority('UPDATE_CONTENT')")
    public String editar(
            @PathVariable Long id,
            Model model) {

        Evento evento = eventoService.buscarPorId(id);

        if (evento == null) {
            return "redirect:/admin/eventos";
        }

        model.addAttribute("evento", evento);

        return "admin/editar-evento";
    }

    @PostMapping("/editar/{id}")
    @PreAuthorize("hasAuthority('UPDATE_CONTENT')")
    public String atualizar(
            @PathVariable Long id,
            @RequestParam(required = false) MultipartFile arquivo,
            @RequestParam(required = false) String alt,
            @RequestParam(defaultValue = "false") boolean ativo,
            RedirectAttributes redirectAttributes) {

        Evento evento = eventoService.buscarPorId(id);

        if (evento == null) {
            return "redirect:/admin/eventos";
        }

        Midia midiaAntiga = evento.getMidia();

        if (arquivo != null && !arquivo.isEmpty()) {
            try {
                Midia novaMidia = imagemService.salvar(arquivo, alt);
                evento.setMidia(novaMidia);
                evento.setImagem(imagemService.url(novaMidia));
            } catch (RegraNegocioException ex) {
                redirectAttributes.addFlashAttribute("erro", ex.getMessage());
                return "redirect:/admin/eventos/editar/" + id;
            }
        }

        evento.setAlt(alt);
        evento.setAtivo(ativo);

        eventoService.salvar(evento);

        if (arquivo != null && !arquivo.isEmpty()) {
            imagemService.excluir(midiaAntiga);
        }

        return "redirect:/admin/eventos";
    }

    @PostMapping("/excluir/{id}")
    @PreAuthorize("hasAuthority('DELETE_CONTENT')")
    public String excluir(
            @PathVariable Long id) {

        Evento evento = eventoService.buscarPorId(id);
        eventoService.excluir(id);

        if (evento != null) {
            imagemService.excluir(evento.getMidia());
        }

        return "redirect:/admin/eventos";
    }
}
