package com.nossolaritapetininga.InstitutoNossoLar.service;

import com.nossolaritapetininga.InstitutoNossoLar.model.Administrador;
import com.nossolaritapetininga.InstitutoNossoLar.repository.AdministradorRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdministradorService {

    private final AdministradorRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AdministradorService(
            AdministradorRepository repository,
            PasswordEncoder passwordEncoder) {

        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Administrador> listarTodos() {
        return repository.findAll();
    }

    public Administrador buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Administrador salvar(Administrador administrador) {

        administrador.setSenha(
                passwordEncoder.encode(administrador.getSenha())
        );

        return repository.save(administrador);
    }

    public Administrador editar(
            Long id,
            String nome,
            String email,
            String senha) {

        Administrador administrador =
                repository.findById(id).orElse(null);

        if (administrador == null) {
            return null;
        }

        administrador.setNome(nome);
        administrador.setEmail(email);

        /*
         * Só altera a senha se uma nova senha
         * tiver sido informada.
         */
        if (senha != null && !senha.isBlank()) {

            administrador.setSenha(
                    passwordEncoder.encode(senha)
            );
        }

        return repository.save(administrador);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }

    public void alterarStatus(Long id) {

        Administrador administrador =
                repository.findById(id).orElse(null);

        if (administrador != null) {

            administrador.setAtivo(
                    !administrador.isAtivo()
            );

            repository.save(administrador);
        }
    }
}