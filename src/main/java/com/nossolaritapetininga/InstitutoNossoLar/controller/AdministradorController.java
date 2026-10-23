package com.nossolaritapetininga.InstitutoNossoLar.controller;

import com.nossolaritapetininga.InstitutoNossoLar.exception.RegraNegocioException;
import com.nossolaritapetininga.InstitutoNossoLar.model.Usuario;
import com.nossolaritapetininga.InstitutoNossoLar.service.AdministradorService;
import com.nossolaritapetininga.InstitutoNossoLar.security.SessaoUsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/administradores")
@PreAuthorize("hasRole('SUPER_ADMIN') and hasAuthority('VIEW_USER')")
public class AdministradorController {

    private static final Logger log = LoggerFactory.getLogger(AdministradorController.class);

    private final AdministradorService administradorService;
    private final SessaoUsuarioService sessaoUsuarioService;

    public AdministradorController(
            AdministradorService administradorService,
            SessaoUsuarioService sessaoUsuarioService) {
        this.administradorService = administradorService;
        this.sessaoUsuarioService = sessaoUsuarioService;
    }

    @GetMapping
    public String listarAdministradores(Model model, Authentication authentication) {
        List<Usuario> usuarios = administradorService.listarTodos();
        model.addAttribute("administradores", usuarios);
        model.addAttribute("emailAtual", authentication.getName());
        model.addAttribute("superAdmin", isSuperAdmin(authentication));
        model.addAttribute("idsGerenciaveis", usuarios.stream()
                .filter(usuario -> podeGerenciarNaTela(usuario, authentication))
                .map(Usuario::getId)
                .toList());
        return "admin/administradores";
    }

    @GetMapping("/historico-senhas")
    @PreAuthorize("hasRole('SUPER_ADMIN') and hasAuthority('VIEW_AUDIT_LOG')")
    public String historicoRedefinicoes(Model model) {
        try {
            model.addAttribute("historico", administradorService.listarHistoricoRedefinicoes());
        } catch (RuntimeException exception) {
            log.error("Falha ao carregar o histórico de redefinições de senha.", exception);
            model.addAttribute("historico", List.of());
            model.addAttribute("erro", "Não foi possível carregar o histórico. Consulte os logs do serviço.");
        }
        return "admin/historico-redefinicoes-senha";
    }

    @GetMapping("/novo")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String novoAdministrador(Model model, Authentication authentication) {
        prepararFormularioNovo(model, authentication);
        return "admin/novo-administrador";
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String salvarAdministrador(
            @RequestParam String nome,
            @RequestParam String emailLocal,
            @RequestParam String senha,
            @RequestParam String perfil,
            Authentication authentication,
            Model model) {
        try {
            administradorService.criarUsuario(
                    nome, emailLocal, senha, perfil, isSuperAdmin(authentication));
            return "redirect:/admin/administradores?criado";
        } catch (RegraNegocioException exception) {
            model.addAttribute("erro", exception.getMessage());
            model.addAttribute("nome", nome);
            model.addAttribute("emailLocal", emailLocal);
            model.addAttribute("perfilSelecionado", perfil);
            prepararFormularioNovo(model, authentication);
            return "admin/novo-administrador";
        }
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') and hasAuthority('UPDATE_USER')")
    public String editarAdministrador(
            @PathVariable Long id,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        Usuario usuario = administradorService.buscarPorId(id);
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("erro", "Usuário não encontrado.");
            return "redirect:/admin/administradores";
        }
        if (!podeGerenciarNaTela(usuario, authentication)) {
            redirectAttributes.addFlashAttribute("erro", "Você não pode alterar esse usuário.");
            return "redirect:/admin/administradores";
        }
        model.addAttribute("administrador", usuario);
        model.addAttribute("perfis", administradorService.perfisPermitidos(isSuperAdmin(authentication)));
        model.addAttribute("perfilSelecionado", administradorService.perfilPrincipal(usuario));
        return "admin/editar-administrador";
    }

    @PostMapping("/editar/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') and hasAuthority('UPDATE_USER')")
    public String atualizarAdministrador(
            @PathVariable Long id,
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam String perfil,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            Usuario antesDaAlteracao = administradorService.buscarPorId(id);
            administradorService.editarUsuario(
                    id, nome, email, perfil,
                    authentication.getName(), isSuperAdmin(authentication));
            if (antesDaAlteracao != null) {
                sessaoUsuarioService.expirarSessoes(antesDaAlteracao.getEmail());
            }
            sessaoUsuarioService.expirarSessoes(email);
            redirectAttributes.addFlashAttribute("sucesso", "Usuário atualizado.");
        } catch (RegraNegocioException exception) {
            redirectAttributes.addFlashAttribute("erro", exception.getMessage());
        }
        return "redirect:/admin/administradores";
    }

    @PostMapping("/status/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') and hasAuthority('DISABLE_USER')")
    public String alterarStatus(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = administradorService.buscarPorId(id);
            administradorService.alterarStatus(
                    id, authentication.getName(), isSuperAdmin(authentication));
            if (usuario != null) {
                sessaoUsuarioService.expirarSessoes(usuario.getEmail());
            }
        } catch (RegraNegocioException exception) {
            redirectAttributes.addFlashAttribute("erro", exception.getMessage());
        }
        return "redirect:/admin/administradores";
    }

    @PostMapping("/senha-temporaria/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') and hasAuthority('RESET_USER_PASSWORD')")
    @ResponseBody
    public ResponseEntity<Map<String, String>> redefinirSenhaTemporaria(
            @PathVariable Long id,
            Authentication authentication,
            HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noStore());
        headers.setPragma("no-cache");
        try {
            Usuario usuario = administradorService.buscarPorId(id);
            if (usuario == null) {
                throw new RegraNegocioException("Usuário não encontrado.");
            }
            String senhaTemporaria = administradorService.redefinirSenhaTemporaria(
                    id, authentication.getName(), request.getRemoteAddr(), request.getHeader("User-Agent"));
            sessaoUsuarioService.expirarSessoes(usuario.getEmail());
            return new ResponseEntity<>(Map.of(
                    "usuario", usuario.getEmail(),
                    "senhaTemporaria", senhaTemporaria), headers, HttpStatus.OK);
        } catch (RegraNegocioException exception) {
            return new ResponseEntity<>(Map.of("erro", exception.getMessage()), headers, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException exception) {
            log.error("Falha ao redefinir a senha temporária do usuário {}.", id, exception);
            return new ResponseEntity<>(Map.of(
                    "erro", "Não foi possível redefinir a senha agora. Consulte os logs do serviço."),
                    headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void prepararFormularioNovo(Model model, Authentication authentication) {
        model.addAttribute("perfis", administradorService.perfisPermitidos(isSuperAdmin(authentication)));
    }

    private boolean isSuperAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_SUPER_ADMIN"));
    }

    private boolean podeGerenciarNaTela(Usuario usuario, Authentication authentication) {
        if (usuario.getEmail().equalsIgnoreCase(authentication.getName())) {
            return false;
        }
        return isSuperAdmin(authentication);
    }
}
