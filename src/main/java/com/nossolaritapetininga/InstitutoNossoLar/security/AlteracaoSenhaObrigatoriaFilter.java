package com.nossolaritapetininga.InstitutoNossoLar.security;

import com.nossolaritapetininga.InstitutoNossoLar.repository.AdministradorRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AlteracaoSenhaObrigatoriaFilter extends OncePerRequestFilter {

    private final AdministradorRepository usuarioRepository;

    public AlteracaoSenhaObrigatoriaFilter(AdministradorRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)
                && deveVerificar(request.getRequestURI())) {

            boolean deveAlterar = usuarioRepository.findByEmail(authentication.getName())
                    .map(usuario -> Boolean.TRUE.equals(usuario.getAlterarSenha()))
                    .orElse(false);

            if (deveAlterar) {
                response.sendRedirect(request.getContextPath() + "/alterar-senha");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean deveVerificar(String caminho) {
        return !caminho.equals("/alterar-senha")
                && !caminho.equals("/logout")
                && !caminho.startsWith("/css/")
                && !caminho.startsWith("/js/")
                && !caminho.startsWith("/img/")
                && !caminho.startsWith("/NL_Img/");
    }
}
