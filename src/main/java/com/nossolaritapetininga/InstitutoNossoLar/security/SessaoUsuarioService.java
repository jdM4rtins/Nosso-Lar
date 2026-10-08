package com.nossolaritapetininga.InstitutoNossoLar.security;

import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class SessaoUsuarioService {

    private final SessionRegistry sessionRegistry;

    public SessaoUsuarioService(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    public void expirarSessoes(String email) {
        sessionRegistry.getAllPrincipals().stream()
                .filter(UserDetails.class::isInstance)
                .map(UserDetails.class::cast)
                .filter(usuario -> usuario.getUsername().equalsIgnoreCase(email))
                .forEach(usuario -> sessionRegistry.getAllSessions(usuario, false)
                        .forEach(sessao -> sessao.expireNow()));
    }

    public void expirarTodasAsSessoes() {
        sessionRegistry.getAllPrincipals().forEach(principal ->
                sessionRegistry.getAllSessions(principal, false)
                        .forEach(sessao -> sessao.expireNow()));
    }
}
