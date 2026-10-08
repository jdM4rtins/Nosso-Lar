package com.nossolaritapetininga.InstitutoNossoLar.service;

import com.nossolaritapetininga.InstitutoNossoLar.exception.RegraNegocioException;
import com.nossolaritapetininga.InstitutoNossoLar.model.Midia;
import com.nossolaritapetininga.InstitutoNossoLar.repository.MidiaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImagemService {

    static final long TAMANHO_MAXIMO = 2L * 1024 * 1024;
    static final int DIMENSAO_MAXIMA = 4096;
    static final long TOTAL_PIXELS_MAXIMO = 16_000_000L;

    private static final Map<String, String> MIME_POR_FORMATO = Map.of(
            "jpeg", "image/jpeg",
            "jpg", "image/jpeg",
            "png", "image/png",
            "gif", "image/gif");

    private final MidiaRepository midiaRepository;

    public ImagemService(MidiaRepository midiaRepository) {
        this.midiaRepository = midiaRepository;
    }

    @Transactional
    public Midia salvar(MultipartFile arquivo, String textoAlternativo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new RegraNegocioException("Selecione uma imagem.");
        }
        if (arquivo.getSize() > TAMANHO_MAXIMO) {
            throw new RegraNegocioException("A imagem deve ter no máximo 2 MB.");
        }

        byte[] conteudo;
        try {
            conteudo = arquivo.getBytes();
        } catch (IOException ex) {
            throw new RegraNegocioException("Não foi possível ler a imagem enviada.");
        }

        ImagemIdentificada imagem = identificar(conteudo);
        String nomeOriginal = nomeSeguro(arquivo.getOriginalFilename());

        Midia midia = new Midia();
        midia.setNome(UUID.randomUUID().toString());
        midia.setNomeOriginal(nomeOriginal);
        midia.setTipo("IMAGEM");
        midia.setMimeType(imagem.mimeType());
        midia.setConteudo(conteudo);
        midia.setTamanho((long) conteudo.length);
        midia.setLargura(imagem.largura());
        midia.setAltura(imagem.altura());
        midia.setTextoAlternativo(textoAlternativo);
        midia.setAtivo(true);

        midia = midiaRepository.save(midia);
        midia.setUrl(url(midia));
        return midiaRepository.save(midia);
    }

    @Transactional(readOnly = true)
    public Optional<Midia> buscarAtiva(Long id) {
        return midiaRepository.findById(id)
                .filter(Midia::isAtivo)
                .filter(midia -> midia.getConteudo() != null);
    }

    @Transactional
    public void excluir(Midia midia) {
        if (midia != null && midia.getId() != null) {
            midiaRepository.deleteById(midia.getId());
        }
    }

    public String url(Midia midia) {
        return "midias/" + midia.getId();
    }

    private ImagemIdentificada identificar(byte[] conteudo) {
        try (ImageInputStream entrada = ImageIO.createImageInputStream(new ByteArrayInputStream(conteudo))) {
            if (entrada == null) {
                throw imagemInvalida();
            }

            Iterator<ImageReader> leitores = ImageIO.getImageReaders(entrada);
            if (!leitores.hasNext()) {
                throw imagemInvalida();
            }

            ImageReader leitor = leitores.next();
            try {
                leitor.setInput(entrada, true, true);
                String formato = leitor.getFormatName().toLowerCase(Locale.ROOT);
                String mimeType = MIME_POR_FORMATO.get(formato);
                if (mimeType == null) {
                    throw new RegraNegocioException("Use uma imagem JPEG, PNG ou GIF.");
                }

                int largura = leitor.getWidth(0);
                int altura = leitor.getHeight(0);
                if (largura <= 0 || altura <= 0
                        || largura > DIMENSAO_MAXIMA || altura > DIMENSAO_MAXIMA
                        || (long) largura * altura > TOTAL_PIXELS_MAXIMO) {
                    throw new RegraNegocioException(
                            "A imagem deve ter no máximo 4096 × 4096 pixels.");
                }

                return new ImagemIdentificada(mimeType, largura, altura);
            } finally {
                leitor.dispose();
            }
        } catch (RegraNegocioException ex) {
            throw ex;
        } catch (IOException ex) {
            throw imagemInvalida();
        }
    }

    private RegraNegocioException imagemInvalida() {
        return new RegraNegocioException("O arquivo enviado não é uma imagem válida.");
    }

    private String nomeSeguro(String nomeOriginal) {
        if (nomeOriginal == null || nomeOriginal.isBlank()) {
            return "imagem";
        }
        String nome = nomeOriginal.replace('\\', '/');
        nome = nome.substring(nome.lastIndexOf('/') + 1);
        return nome.length() > 255 ? nome.substring(nome.length() - 255) : nome;
    }

    private record ImagemIdentificada(String mimeType, int largura, int altura) {
    }
}
