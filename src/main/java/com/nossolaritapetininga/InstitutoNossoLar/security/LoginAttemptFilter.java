package com.nossolaritapetininga.InstitutoNossoLar.security;

import com.nossolaritapetininga.InstitutoNossoLar.service.ProtecaoAutenticacaoService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class LoginAttemptFilter extends OncePerRequestFilter {

    private final ProtecaoAutenticacaoService protecao;

    public LoginAttemptFilter(ProtecaoAutenticacaoService protecao) {
        this.protecao = protecao;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        if ("POST".equalsIgnoreCase(request.getMethod())
                && "/login".equals(request.getServletPath())) {
            String email = request.getParameter("username");
            if (!protecao.podeTentarLogin(email, request.getRemoteAddr())) {
                response.sendRedirect(request.getContextPath() + "/login?bloqueado");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
