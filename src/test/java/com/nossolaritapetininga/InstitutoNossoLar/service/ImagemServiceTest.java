package com.nossolaritapetininga.InstitutoNossoLar.service;

import com.nossolaritapetininga.InstitutoNossoLar.exception.RegraNegocioException;
import com.nossolaritapetininga.InstitutoNossoLar.model.Midia;
import com.nossolaritapetininga.InstitutoNossoLar.repository.MidiaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ImagemServiceTest {

    private MidiaRepository repository;
    private ImagemService service;

    @BeforeEach
    void configurar() {
        repository = mock(MidiaRepository.class);
        service = new ImagemService(repository);
        when(repository.save(any(Midia.class))).thenAnswer(invocacao -> {
            Midia midia = invocacao.getArgument(0);
            if (midia.getId() == null) {
                midia.setId(42L);
            }
            return midia;
        });
    }

    @Test
    void salvaImagemValidaNoBanco() throws Exception {
        BufferedImage imagem = new BufferedImage(20, 10, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        ImageIO.write(imagem, "png", saida);
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo", "foto.png", "application/octet-stream", saida.toByteArray());

        Midia midia = service.salvar(arquivo, "Descrição da foto");

        assertThat(midia.getId()).isEqualTo(42L);
        assertThat(midia.getMimeType()).isEqualTo("image/png");
        assertThat(midia.getConteudo()).isEqualTo(saida.toByteArray());
        assertThat(midia.getLargura()).isEqualTo(20);
        assertThat(midia.getAltura()).isEqualTo(10);
        assertThat(midia.getUrl()).isEqualTo("midias/42");
    }

    @Test
    void rejeitaArquivoQueNaoEImagem() {
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo", "arquivo.png", "image/png", "não é imagem".getBytes());

        assertThatThrownBy(() -> service.salvar(arquivo, "Arquivo inválido"))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("O arquivo enviado não é uma imagem válida.");
    }

    @Test
    void rejeitaArquivoMaiorQueDoisMegabytes() {
        byte[] conteudo = new byte[(int) ImagemService.TAMANHO_MAXIMO + 1];
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo", "grande.png", "image/png", conteudo);

        assertThatThrownBy(() -> service.salvar(arquivo, "Imagem grande"))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("A imagem deve ter no máximo 2 MB.");
    }
}
