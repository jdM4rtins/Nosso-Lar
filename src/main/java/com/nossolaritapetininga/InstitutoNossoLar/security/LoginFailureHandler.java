package com.nossolaritapetininga.InstitutoNossoLar.security;

import com.nossolaritapetininga.InstitutoNossoLar.service.ProtecaoAutenticacaoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final ProtecaoAutenticacaoService protecao;

    public LoginFailureHandler(ProtecaoAutenticacaoService protecao) {
        this.protecao = protecao;
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        protecao.registrarFalhaLogin(request.getParameter("username"), request.getRemoteAddr());
        response.sendRedirect(request.getContextPath() + "/login?erro");
    }
}
