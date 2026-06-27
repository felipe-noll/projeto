package com.example.app.auth.controller;

import com.example.app.auth.model.Usuario;
import com.example.app.auth.repository.UsuarioRepository;
import com.example.app.auth.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private EmailService emailService;

    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String login,
                        @RequestParam String senha,
                        HttpSession session,
                        Model model) {
        Optional<Usuario> user = usuarioRepo.findByLogin(login);
        if (user.isPresent() && user.get().getSenha().equals(senha)) {
            session.setAttribute("usuarioLogado", user.get());
            return "redirect:/lancamentos";
        }
        model.addAttribute("erro", "Login inválido");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/usuarios/novo")
    public String formNovoUsuario() {
        return "usuario-form";
    }

    @PostMapping("/registrar")
    public String registrarUsuario(Usuario usuario) {
        usuarioRepo.save(usuario);
        emailService.enviarEmailSimples(
                usuario.getEmail(),
                "Bem-vindo!",
                "Olá " + usuario.getNome() + ", sua conta foi criada com sucesso!");
        return "redirect:/";
    }

    @GetMapping("/perfil/editar")
    public String editarPerfil(HttpSession session, Model model) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) return "redirect:/";
        model.addAttribute("usuario", usuarioLogado);
        return "editar-perfil";
    }

    @PostMapping("/perfil/salvar")
    public String salvarPerfil(Usuario usuarioAtualizado, HttpSession session) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) return "redirect:/";
        usuarioAtualizado.setId(usuarioLogado.getId());
        usuarioRepo.save(usuarioAtualizado);
        session.setAttribute("usuarioLogado", usuarioAtualizado);
        emailService.enviarEmailSimples(
                usuarioAtualizado.getEmail(),
                "Perfil Atualizado",
                "Olá " + usuarioAtualizado.getNome() + ", suas informações foram alteradas.");
        return "redirect:/lancamentos";
    }
}
