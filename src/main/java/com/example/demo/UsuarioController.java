package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.demo.Repositories.UsuarioRepository;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @GetMapping("/novo")
    public String formNovoUsuario() {
        return "usuario-form";
    }

    @PostMapping("/salvar")
    public String salvarUsuario(Usuario usuario) {
        usuarioRepo.save(usuario);
        return "redirect:/"; // Volta para o login após criar a conta
    }
}