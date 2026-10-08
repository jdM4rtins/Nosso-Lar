package com.nossolaritapetininga.InstitutoNossoLar.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.regex.Pattern;

@ControllerAdvice
public class UploadExceptionHandler {

    private static final Pattern EDICAO_ATIVIDADE =
            Pattern.compile("/admin/atividades/editar/\\d+");
    private static final Pattern EDICAO_EVENTO =
            Pattern.compile("/admin/eventos/editar/\\d+");
    private static final Pattern EDICAO_NOTICIA =
            Pattern.compile("/admin/noticias/editar/\\d+");

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String uploadMuitoGrande(
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("erro", "A imagem deve ter no máximo 2 MB.");
        String caminho = request.getRequestURI();

        if (EDICAO_ATIVIDADE.matcher(caminho).matches()
                || EDICAO_EVENTO.matcher(caminho).matches()
                || EDICAO_NOTICIA.matcher(caminho).matches()) {
            return "redirect:" + caminho;
        }
        if (caminho.startsWith("/admin/atividades")) {
            return "redirect:/admin/atividades/novo";
        }
        if (caminho.startsWith("/admin/eventos")) {
            return "redirect:/admin/eventos/novo";
        }
        if (caminho.startsWith("/admin/noticias")) {
            return "redirect:/admin/noticias/novo";
        }
        return "redirect:/admin/dashboard";
    }
}
