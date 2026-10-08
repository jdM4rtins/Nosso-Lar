package com.nossolaritapetininga.InstitutoNossoLar.service;

import com.nossolaritapetininga.InstitutoNossoLar.exception.RegraNegocioException;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ResendEmailService {

    private final String apiKey;
    private final String remetente;
    private final String publicUrl;

    public ResendEmailService(
            @Value("${app.resend.api-key:}") String apiKey,
            @Value("${app.resend.from:noreply@nossolar.com}") String remetente,
            @Value("${app.public-url:http://localhost:8080}") String publicUrl) {
        this.apiKey = apiKey;
        this.remetente = remetente;
        this.publicUrl = publicUrl.replaceAll("/$", "");
    }

    public void enviarRecuperacao(String email, String nome, String token, long minutosValidade) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new RegraNegocioException("O envio de e-mails ainda não está configurado.");
        }
        String link = publicUrl + "/redefinir-senha?token=" + token;
        CreateEmailOptions opcoes = CreateEmailOptions.builder()
                .from(remetente)
                .to(email)
                .subject("Recuperação de senha — Instituto Nosso Lar")
                .text("Olá, " + nome + ",\n\n"
                        + "Use o link abaixo para criar uma nova senha (válido por " + minutosValidade + " minutos):\n"
                        + link + "\n\nSe você não solicitou a alteração, ignore esta mensagem.")
                .html("<p>Olá, " + escape(nome) + ",</p>"
                        + "<p>Use o link abaixo para criar uma nova senha. Ele é válido por " + minutosValidade + " minutos:</p>"
                        + "<p><a href=\"" + link + "\">Criar nova senha</a></p>"
                        + "<p>Se você não solicitou a alteração, ignore esta mensagem.</p>")
                .build();
        try {
            new Resend(apiKey).emails().send(opcoes);
        } catch (ResendException ex) {
            throw new RegraNegocioException("Não foi possível enviar o e-mail de recuperação.");
        }
    }

    private String escape(String valor) {
        return valor == null ? "" : valor.replace("&", "&amp;")
                .replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
