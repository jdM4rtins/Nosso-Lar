package com.nossolaritapetininga.InstitutoNossoLar.service;

import com.nossolaritapetininga.InstitutoNossoLar.model.Administrador;
import com.nossolaritapetininga.InstitutoNossoLar.repository.AdministradorRepository;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AdministradorRepository repository;

    public CustomUserDetailsService(AdministradorRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Administrador administrador = repository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Administrador não encontrado"
                        )
                );

        return User.builder()
                .username(administrador.getEmail())
                .password(administrador.getSenha())
                .roles("ADMIN")
                .disabled(!administrador.isAtivo())
                .build();
    }
}