package com.nossolaritapetininga.InstitutoNossoLar.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ImagemService {

    private final Path pastaImagens =
            Paths.get("uploads").toAbsolutePath().normalize();

    public String salvar(MultipartFile arquivo) throws IOException {

        if (arquivo == null || arquivo.isEmpty()) {
            return null;
        }

        Files.createDirectories(pastaImagens);

        String nomeOriginal = arquivo.getOriginalFilename();

        String extensao = "";

        if (nomeOriginal != null && nomeOriginal.contains(".")) {
            extensao = nomeOriginal.substring(
                    nomeOriginal.lastIndexOf(".")
            );
        }

        String novoNome =
                UUID.randomUUID() + extensao;

        Path destino =
                pastaImagens.resolve(novoNome);

        Files.copy(
                arquivo.getInputStream(),
                destino,
                StandardCopyOption.REPLACE_EXISTING
        );

        return "uploads/" + novoNome;
    }

    public void excluir(String caminhoImagem) throws IOException {

        if (caminhoImagem == null || caminhoImagem.isBlank()) {
                return;
        }

        if (!caminhoImagem.startsWith("uploads/")) {
            return;
        }

        Path arquivo = pastaImagens.resolve(
                caminhoImagem.substring("uploads/".length())
        ).normalize();

        if (!arquivo.startsWith(pastaImagens)) {
            return;
        }

        Files.deleteIfExists(arquivo);
        }
}
