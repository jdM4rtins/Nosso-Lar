package com.nossolaritapetininga.InstitutoNossoLar.service;

import com.nossolaritapetininga.InstitutoNossoLar.model.Usuario;
import com.nossolaritapetininga.InstitutoNossoLar.model.Perfil;
import com.nossolaritapetininga.InstitutoNossoLar.repository.AdministradorRepository;
import com.nossolaritapetininga.InstitutoNossoLar.repository.PerfilRepository;
import com.nossolaritapetininga.InstitutoNossoLar.exception.RegraNegocioException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

@Service
public class AdministradorService {

    private static final String DOMINIO_USUARIO = "nossolar.gustavocardilho.com.br";

    private final AdministradorRepository repository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;

    public AdministradorService(
            AdministradorRepository repository,
            PerfilRepository perfilRepository,
            PasswordEncoder passwordEncoder) {

        this.repository = repository;
        this.perfilRepository = perfilRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listarTodos() {
        return repository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional
    public Usuario criarUsuario(
            String nome,
            String emailLocal,
            String senhaTemporaria,
            String perfilSolicitado,
            boolean atorSuperAdmin) {

        validarNome(nome);
        validarEmailLocal(emailLocal);
        validarPerfilPermitido(perfilSolicitado, atorSuperAdmin);
        validarSenhaForte(senhaTemporaria);

        String emailNormalizado = normalizarEmail(emailLocal) + "@" + DOMINIO_USUARIO;
        if (repository.findByEmailIgnoreCase(emailNormalizado).isPresent()) {
            throw new RegraNegocioException("Já existe um usuário com este e-mail.");
        }

        Perfil perfil = buscarPerfilAtivo(perfilSolicitado);
        Usuario usuario = new Usuario();
        usuario.setNome(nome.trim());
        usuario.setEmail(emailNormalizado);
        usuario.setSenha(passwordEncoder.encode(senhaTemporaria));
        usuario.setAtivo(true);
        usuario.setAlterarSenha(true);
        usuario.setPerfis(new LinkedHashSet<>(Set.of(perfil)));
        return repository.save(usuario);
    }

    @Transactional
    public Usuario editarUsuario(
            Long id,
            String nome,
            String email,
            String perfilSolicitado,
            String emailAtor,
            boolean atorSuperAdmin) {

        Usuario usuario = buscarObrigatorio(id);
        validarGerenciamento(usuario, emailAtor, atorSuperAdmin);
        validarNomeEEmail(nome, email);
        validarPerfilPermitido(perfilSolicitado, atorSuperAdmin);

        String emailNormalizado = normalizarEmail(email);
        if (repository.existsByEmailIgnoreCaseAndIdNot(emailNormalizado, id)) {
            throw new RegraNegocioException("Já existe outro usuário com este e-mail.");
        }

        Perfil perfil = buscarPerfilAtivo(perfilSolicitado);
        usuario.setNome(nome.trim());
        usuario.setEmail(emailNormalizado);
        usuario.setPerfis(new LinkedHashSet<>(Set.of(perfil)));
        return repository.save(usuario);
    }

    @Transactional
    public void alterarStatus(
            Long id,
            String emailAtor,
            boolean atorSuperAdmin) {

        Usuario usuario = buscarObrigatorio(id);
        validarGerenciamento(usuario, emailAtor, atorSuperAdmin);

        if (usuario.isAtivo()
                && temPerfil(usuario, "SUPER_ADMIN")
                && repository.contarAtivosPorPerfil("SUPER_ADMIN") <= 1) {
            throw new RegraNegocioException("Não é possível desativar o último SUPER_ADMIN ativo.");
        }

        usuario.setAtivo(!usuario.isAtivo());
        repository.save(usuario);
    }

    public List<Perfil> perfisPermitidos(boolean atorSuperAdmin) {
        List<String> nomes = atorSuperAdmin
                ? List.of("CONSULTA", "EDITOR", "SUPER_ADMIN")
                : List.of();
        return perfilRepository.findAll().stream()
                .filter(Perfil::isAtivo)
                .filter(perfil -> nomes.contains(perfil.getNome()))
                .toList();
    }

    public String perfilPrincipal(Usuario usuario) {
        return usuario.getPerfis().stream()
                .map(Perfil::getNome)
                .findFirst()
                .orElse("CONSULTA");
    }

    @Transactional
    public String alterarPropriaSenha(
            String email,
            String novaSenha,
            String confirmacao) {

        Usuario usuario = repository.findByEmail(email).orElse(null);
        if (usuario == null) {
            return "Não foi possível localizar a conta.";
        }

        if (!novaSenha.equals(confirmacao)) {
            return "A confirmação não corresponde à nova senha.";
        }

        if (!senhaForte(novaSenha)) {
            return "A nova senha deve ter pelo menos 12 caracteres, com letra maiúscula, letra minúscula, número e símbolo.";
        }

        if (passwordEncoder.matches(novaSenha, usuario.getSenha())) {
            return "A nova senha deve ser diferente da senha atual.";
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuario.setAlterarSenha(false);
        repository.save(usuario);
        return null;
    }

    private boolean senhaForte(String senha) {
        return senha != null
                && senha.length() >= 12
                && senha.chars().anyMatch(Character::isUpperCase)
                && senha.chars().anyMatch(Character::isLowerCase)
                && senha.chars().anyMatch(Character::isDigit)
                && senha.chars().anyMatch(caractere -> !Character.isLetterOrDigit(caractere));
    }

    private void validarSenhaForte(String senha) {
        if (!senhaForte(senha)) {
            throw new RegraNegocioException(
                    "A senha temporária deve ter pelo menos 12 caracteres, com maiúscula, minúscula, número e símbolo.");
        }
    }

    private void validarNomeEEmail(String nome, String email) {
        validarNome(nome);
        if (email == null || !email.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new RegraNegocioException("Informe um e-mail válido.");
        }
    }

    private void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("Informe o nome do usuário.");
        }
    }

    private void validarEmailLocal(String emailLocal) {
        if (emailLocal == null
                || emailLocal.length() > 64
                || !emailLocal.matches("^[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*$")) {
            throw new RegraNegocioException(
                    "Informe somente a parte do e-mail antes do @, sem espaços ou outro domínio.");
        }
    }

    private void validarPerfilPermitido(String perfil, boolean atorSuperAdmin) {
        if (perfil == null || perfil.isBlank()) {
            throw new RegraNegocioException("Selecione um perfil.");
        }
        if (!atorSuperAdmin) {
            throw new RegraNegocioException("Somente SUPER_ADMIN pode criar ou alterar usuários.");
        }
        if (!Set.of("CONSULTA", "EDITOR", "SUPER_ADMIN").contains(perfil)) {
            throw new RegraNegocioException("Perfil inválido.");
        }
    }

    private void validarGerenciamento(
            Usuario usuario,
            String emailAtor,
            boolean atorSuperAdmin) {
        if (usuario.getEmail().equalsIgnoreCase(emailAtor)) {
            throw new RegraNegocioException(
                    "Use a opção de alterar a própria senha; a conta atual não pode ser alterada por esta tela.");
        }
        if (!atorSuperAdmin) {
            throw new RegraNegocioException("Somente SUPER_ADMIN pode gerenciar usuários.");
        }
    }

    private boolean temPerfil(Usuario usuario, String nome) {
        return usuario.getPerfis().stream().anyMatch(perfil -> perfil.getNome().equals(nome));
    }

    private Perfil buscarPerfilAtivo(String nome) {
        return perfilRepository.findByNome(nome)
                .filter(Perfil::isAtivo)
                .orElseThrow(() -> new RegraNegocioException("Perfil não encontrado ou inativo."));
    }

    private Usuario buscarObrigatorio(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado."));
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
