package com.example.auth.controller;

import com.example.auth.model.Usuario;
import com.example.auth.repository.UsuarioRepository;
import com.example.auth.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private EmailService emailService;

    @GetMapping("/perfil/editar")
    public String editarPerfil(HttpSession session, Model model) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) return "redirect:/usuarios/novo";
        model.addAttribute("usuario", usuarioLogado);
        return "editar-perfil";
    }

    @PostMapping("/perfil/salvar")
    public String salvarPerfil(Usuario usuarioAtualizado, HttpSession session) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) return "redirect:/usuarios/novo";
        usuarioAtualizado.setId(usuarioLogado.getId());
        usuarioRepo.save(usuarioAtualizado);
        session.setAttribute("usuarioLogado", usuarioAtualizado);
        emailService.enviarEmailSimples(
                usuarioAtualizado.getEmail(),
                "Perfil Atualizado",
                "Olá " + usuarioAtualizado.getNome() + ", suas informações foram alteradas.");
        return "redirect:/perfil/editar";
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
        return "redirect:/usuarios/novo";
    }
}
