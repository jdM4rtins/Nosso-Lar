package com.nossolaritapetininga.InstitutoNossoLar;

import com.nossolaritapetininga.InstitutoNossoLar.model.Usuario;
import com.nossolaritapetininga.InstitutoNossoLar.model.Atividade;
import com.nossolaritapetininga.InstitutoNossoLar.model.Conteudo;
import com.nossolaritapetininga.InstitutoNossoLar.model.Evento;
import com.nossolaritapetininga.InstitutoNossoLar.model.Noticia;
import com.nossolaritapetininga.InstitutoNossoLar.repository.AdministradorRepository;
import com.nossolaritapetininga.InstitutoNossoLar.repository.AtividadeRepository;
import com.nossolaritapetininga.InstitutoNossoLar.repository.ConteudoRepository;
import com.nossolaritapetininga.InstitutoNossoLar.repository.EventoRepository;
import com.nossolaritapetininga.InstitutoNossoLar.repository.NoticiaRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner criarAdministrador(
            AdministradorRepository repository,
            ConteudoRepository conteudos,
            AtividadeRepository atividades,
            EventoRepository eventos,
            NoticiaRepository noticias,
            PasswordEncoder passwordEncoder) {

        return args -> {

            if (repository.findByEmail(
                    "admin@nossolar.com"
            ).isEmpty()) {

                Usuario administrador =
                        new Usuario();

                administrador.setNome("Administrador");
                administrador.setEmail(
                        "admin@nossolar.com"
                );

                administrador.setSenha(
                        passwordEncoder.encode("123456")
                );

                administrador.setAtivo(true);

                repository.save(administrador);

                System.out.println(
                    "Administrador criado com sucesso!"
                );
            }

            criarConteudosIniciais(conteudos);

            if (atividades.count() == 0) {
                atividades.save(new Atividade("Artesanato", "NL_Img/artesanatoNL.png", true));
                atividades.save(new Atividade("Estudos", "NL_Img/estudoNL.png", true));
                atividades.save(new Atividade("Natação", "NL_Img/nataçãoNL.png", true));
                atividades.save(new Atividade("Esportes", "NL_Img/skateNL.png", true));
            }

            if (eventos.count() == 0) {
                eventos.save(new Evento("/NL_Img/eventoNL.png", "Atividades do Instituto Nosso Lar", true));
            }

            if (noticias.count() == 0) {
                Noticia noticia = new Noticia();
                noticia.setTitulo("Bem-vindos ao novo site do Instituto Nosso Lar");
                noticia.setResumo("Acompanhe por aqui as atividades, acontecimentos e formas de fazer parte da nossa rede de cuidado.");
                noticia.setConteudo("Este espaço reúne novidades do Instituto Nosso Lar. Entre em contato para saber mais sobre nossas atividades e como apoiar crianças e adolescentes.");
                noticia.setImagem("NL_Img/eventoNL.png");
                noticia.setDataPublicacao(LocalDate.now());
                noticia.setAtivo(true);
                noticias.save(noticia);
            }
        };
    }

    private void criarConteudosIniciais(ConteudoRepository conteudos) {
        criarConteudoSeAusente(conteudos, "inicio", "Acolher, cuidar e criar novas possibilidades.", "Uma rede de cuidado para que crianças e adolescentes construam caminhos com segurança, afeto e esperança.", null, null, null);
        criarConteudoSeAusente(conteudos, "sobre", "Um lar de cuidado e oportunidades.", "O Instituto Nosso Lar, em Itapetininga, é uma organização da sociedade civil dedicada à promoção do bem-estar social e ao apoio de crianças e adolescentes em situação de vulnerabilidade.", "Com acolhimento, solidariedade e parceria, oferecemos atividades educativas, culturais e esportivas para fortalecer vínculos e revelar talentos.", "NL_Img/referencia1.png", null);
        criarConteudoSeAusente(conteudos, "contato_telefone", "Telefone", "(15) 99781-5294", null, null, null);
        criarConteudoSeAusente(conteudos, "contato_whatsapp", "WhatsApp", "(15) 99781-5294", null, null, "https://wa.me/5515997815294");
        criarConteudoSeAusente(conteudos, "contato_instagram", "Instagram", "@instituicao_nossolar", null, null, "https://www.instagram.com/instituicao_nossolar/");
        criarConteudoSeAusente(conteudos, "contato_facebook", "Facebook", "Instituto Nosso Lar", null, null, "https://www.facebook.com/");
        criarConteudoSeAusente(conteudos, "contato_horario", "Horário de funcionamento", "Domingo a domingo<br>Atendimento: 24h", null, null, null);
        criarConteudoSeAusente(conteudos, "contato_endereco", "Endereço", "Rua João Evangelista, 646 — Centro, Itapetininga/SP", null, null, null);
    }

    private void criarConteudoSeAusente(ConteudoRepository conteudos, String chave, String titulo, String texto, String textoExtra, String imagem, String link) {
        if (conteudos.findByChave(chave).isPresent()) return;
        Conteudo conteudo = new Conteudo();
        conteudo.setChave(chave);
        conteudo.setTitulo(titulo);
        conteudo.setTexto(texto);
        conteudo.setTextoExtra(textoExtra);
        conteudo.setImagem(imagem);
        conteudo.setLink(link);
        conteudo.setAtivo(true);
        conteudos.save(conteudo);
    }
}
