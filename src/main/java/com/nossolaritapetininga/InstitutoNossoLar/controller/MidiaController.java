package com.nossolaritapetininga.InstitutoNossoLar.controller;

import com.nossolaritapetininga.InstitutoNossoLar.model.Midia;
import com.nossolaritapetininga.InstitutoNossoLar.service.ImagemService;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@RestController
@RequestMapping("/midias")
public class MidiaController {

    private final ImagemService imagemService;

    public MidiaController(ImagemService imagemService) {
        this.imagemService = imagemService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> exibir(@PathVariable Long id) {
        return imagemService.buscarAtiva(id)
                .map(this::resposta)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ResponseEntity<byte[]> resposta(Midia midia) {
        ContentDisposition disposition = ContentDisposition.inline()
                .filename(midia.getNomeOriginal(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(midia.getMimeType()))
                .contentLength(midia.getTamanho())
                .cacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePublic())
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .header("X-Content-Type-Options", "nosniff")
                .body(midia.getConteudo());
    }
}
