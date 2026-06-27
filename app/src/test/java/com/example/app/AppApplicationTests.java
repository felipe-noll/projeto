package com.example.app;

import com.example.app.auth.controller.AuthController;
import com.example.app.auth.repository.UsuarioRepository;
import com.example.app.auth.service.EmailService;
import com.example.app.financeiro.controller.FinanceiroController;
import com.example.app.financeiro.repository.LancamentoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest({AuthController.class, FinanceiroController.class})
class AppApplicationTests {

    @MockBean
    private UsuarioRepository usuarioRepo;

    @MockBean
    private EmailService emailService;

    @MockBean
    private LancamentoRepository lancamentoRepo;

    @Test
    void contextLoads() {
    }
}
