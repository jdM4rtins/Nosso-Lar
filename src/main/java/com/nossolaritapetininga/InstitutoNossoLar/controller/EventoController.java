package com.nossolaritapetininga.InstitutoNossoLar.controller;

import com.nossolaritapetininga.InstitutoNossoLar.model.Evento;
import com.nossolaritapetininga.InstitutoNossoLar.service.EventoService;
import com.nossolaritapetininga.InstitutoNossoLar.service.ImagemService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Controller
@RequestMapping("/admin/eventos")
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
    public String novo(Model model) {

        model.addAttribute(
                "evento",
                new Evento()
        );

        return "admin/novo-evento";
    }

    @PostMapping({"", "/"})
    public String salvar(
            @ModelAttribute Evento evento,
            @RequestParam("arquivo") MultipartFile arquivo)
            throws IOException {

        String caminhoImagem =
                imagemService.salvar(arquivo);

        if (caminhoImagem != null) {
            evento.setImagem(caminhoImagem);
        }

        eventoService.salvar(evento);

        return "redirect:/admin/eventos";
    }

    @GetMapping("/editar/{id}")
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
    public String atualizar(
            @PathVariable Long id,
            @RequestParam(required = false) MultipartFile arquivo,
            @RequestParam(required = false) String alt,
            @RequestParam(defaultValue = "false") boolean ativo)
            throws IOException {

        Evento evento = eventoService.buscarPorId(id);

        if (evento == null) {
            return "redirect:/admin/eventos";
        }

        String imagemAntiga = evento.getImagem();

        if (arquivo != null && !arquivo.isEmpty()) {

            String novaImagem =
                    imagemService.salvar(arquivo);

            evento.setImagem(novaImagem);

            imagemService.excluir(imagemAntiga);
        }

        evento.setAlt(alt);
        evento.setAtivo(ativo);

        eventoService.salvar(evento);

        return "redirect:/admin/eventos";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(
            @PathVariable Long id) {

        eventoService.excluir(id);

        return "redirect:/admin/eventos";
    }
}
