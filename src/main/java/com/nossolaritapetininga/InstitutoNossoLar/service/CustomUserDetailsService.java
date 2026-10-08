package com.nossolaritapetininga.InstitutoNossoLar.service;

import com.nossolaritapetininga.InstitutoNossoLar.model.Usuario;
import com.nossolaritapetininga.InstitutoNossoLar.repository.AdministradorRepository;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AdministradorRepository repository;

    public CustomUserDetailsService(AdministradorRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Usuario administrador = repository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Administrador não encontrado"
                        )
                );

        Set<SimpleGrantedAuthority> autoridades = new LinkedHashSet<>();

        administrador.getPerfis().stream()
                .filter(perfil -> perfil.isAtivo())
                .forEach(perfil -> {
                    autoridades.add(new SimpleGrantedAuthority("ROLE_" + perfil.getNome()));
                    perfil.getPermissoes().forEach(permissao ->
                            autoridades.add(new SimpleGrantedAuthority(permissao.getNome())));
                });

        return User.builder()
                .username(administrador.getEmail())
                .password(administrador.getSenha())
                .authorities(autoridades)
                .disabled(!administrador.isAtivo())
                .build();
    }
}
