package com.nossolaritapetininga.InstitutoNossoLar.security;

import com.nossolaritapetininga.InstitutoNossoLar.service.ProtecaoAutenticacaoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ProtecaoAutenticacaoService protecao;
    private final SavedRequestAwareAuthenticationSuccessHandler delegate = new SavedRequestAwareAuthenticationSuccessHandler();

    public LoginSuccessHandler(ProtecaoAutenticacaoService protecao) {
        this.protecao = protecao;
        delegate.setDefaultTargetUrl("/admin/dashboard");
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        protecao.registrarSucessoLogin(authentication.getName(), request.getRemoteAddr());
        delegate.onAuthenticationSuccess(request, response, authentication);
    }
}
