package com.example.app;

import com.example.app.auth.controller.AuthController;
import com.example.app.auth.model.Usuario;
import com.example.app.auth.repository.UsuarioRepository;
import com.example.app.auth.service.EmailService;
import com.example.app.financeiro.controller.FinanceiroController;
import com.example.app.financeiro.repository.LancamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({AuthController.class, FinanceiroController.class})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioRepository usuarioRepo;

    @MockBean
    private EmailService emailService;

    @MockBean
    private LancamentoRepository lancamentoRepo;

    private Usuario usuarioMock;

    @BeforeEach
    public void setup() {
        usuarioMock = new Usuario();
        usuarioMock.setId(1);
        usuarioMock.setLogin("admin");
        usuarioMock.setSenha("1234");
        usuarioMock.setNome("Administrador");
        usuarioMock.setEmail("admin@email.com");
    }

    // 1
    @Test
    public void deveCarregarPaginaDeLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    // 2
    @Test
    public void deveDarErroComLoginInvalido() throws Exception {
        when(usuarioRepo.findByLogin("invalido")).thenReturn(Optional.empty());
        mockMvc.perform(post("/login")
                .param("login", "invalido")
                .param("senha", "errada"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("erro", "Login inválido"));
    }

    // 3
    @Test
    public void deveDarErroComSenhaIncorreta() throws Exception {
        when(usuarioRepo.findByLogin("admin")).thenReturn(Optional.of(usuarioMock));
        mockMvc.perform(post("/login")
                .param("login", "admin")
                .param("senha", "errada"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("erro", "Login inválido"));
    }

    // 4
    @Test
    public void deveLogarComSucesso() throws Exception {
        when(usuarioRepo.findByLogin("admin")).thenReturn(Optional.of(usuarioMock));
        mockMvc.perform(post("/login")
                .param("login", "admin")
                .param("senha", "1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/lancamentos"));
    }

    // 5
    @Test
    public void deveFalharSeFaltarParametroLogin() throws Exception {
        mockMvc.perform(post("/login").param("senha", "1234"))
                .andExpect(status().isBadRequest());
    }

    // 6
    @Test
    public void deveFalharSeFaltarParametroSenha() throws Exception {
        mockMvc.perform(post("/login").param("login", "admin"))
                .andExpect(status().isBadRequest());
    }

    // 7
    @Test
    public void deveCarregarFormularioDeNovoUsuario() throws Exception {
        mockMvc.perform(get("/usuarios/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuario-form"));
    }

    // 8
    @Test
    public void deveRegistrarUsuarioERedirecionar() throws Exception {
        mockMvc.perform(post("/registrar")
                .param("nome", "Felipe")
                .param("login", "felipe")
                .param("email", "felipe@email.com")
                .param("senha", "senha123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        verify(usuarioRepo, times(1)).save(any(Usuario.class));
    }

    // 9
    @Test
    public void deveEnviarEmailAoRegistrar() throws Exception {
        mockMvc.perform(post("/registrar")
                .param("nome", "Felipe")
                .param("login", "felipe")
                .param("email", "felipe@email.com")
                .param("senha", "senha123"))
                .andExpect(status().is3xxRedirection());
        verify(emailService, times(1)).enviarEmailSimples(eq("felipe@email.com"), any(), any());
    }

    // 10
    @Test
    public void deveRedirecionarEditarPerfilSemSessao() throws Exception {
        mockMvc.perform(get("/perfil/editar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    // 11
    @Test
    public void deveCarregarEditarPerfilComSessao() throws Exception {
        mockMvc.perform(get("/perfil/editar").sessionAttr("usuarioLogado", usuarioMock))
                .andExpect(status().isOk())
                .andExpect(view().name("editar-perfil"))
                .andExpect(model().attributeExists("usuario"));
    }

    // 12
    @Test
    public void deveRedirecionarSalvarPerfilSemSessao() throws Exception {
        mockMvc.perform(post("/perfil/salvar")
                .param("nome", "Admin").param("login", "admin")
                .param("email", "admin@email.com").param("senha", "1234"))
                .andExpect(redirectedUrl("/"));
        verify(usuarioRepo, never()).save(any(Usuario.class));
    }

    // 13
    @Test
    public void deveSalvarPerfilComSessao() throws Exception {
        when(usuarioRepo.save(any(Usuario.class))).thenReturn(usuarioMock);
        mockMvc.perform(post("/perfil/salvar")
                .sessionAttr("usuarioLogado", usuarioMock)
                .param("nome", "Admin").param("login", "admin")
                .param("email", "admin@email.com").param("senha", "1234"))
                .andExpect(status().is3xxRedirection());
        verify(usuarioRepo, times(1)).save(any(Usuario.class));
    }

    // 14
    @Test
    public void deveEnviarEmailAoSalvarPerfil() throws Exception {
        when(usuarioRepo.save(any(Usuario.class))).thenReturn(usuarioMock);
        mockMvc.perform(post("/perfil/salvar")
                .sessionAttr("usuarioLogado", usuarioMock)
                .param("nome", "Admin").param("login", "admin")
                .param("email", "admin@email.com").param("senha", "1234"))
                .andExpect(status().is3xxRedirection());
        verify(emailService, times(1)).enviarEmailSimples(eq("admin@email.com"), any(), any());
    }

    // 15
    @Test
    public void deveNaoEnviarEmailSemSessao() throws Exception {
        mockMvc.perform(post("/perfil/salvar")
                .param("nome", "Admin").param("login", "admin")
                .param("email", "admin@email.com").param("senha", "1234"))
                .andExpect(redirectedUrl("/"));
        verify(emailService, never()).enviarEmailSimples(any(), any(), any());
    }

    // 16
    @Test
    public void deveRedirecionarListagemSemSessao() throws Exception {
        mockMvc.perform(get("/lancamentos"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    // 17
    @Test
    public void deveRedirecionarNovoLancamentoSemSessao() throws Exception {
        mockMvc.perform(get("/lancamentos/novo"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    // 18
    @Test
    public void deveCarregarFormNovoLancamentoComSessao() throws Exception {
        mockMvc.perform(get("/lancamentos/novo").sessionAttr("usuarioLogado", usuarioMock))
                .andExpect(status().isOk())
                .andExpect(view().name("form-lancamento"));
    }

    // 19
    @Test
    public void deveRetornarLoginComErroQuandoUsuarioNaoExiste() throws Exception {
        when(usuarioRepo.findByLogin(any())).thenReturn(Optional.empty());
        mockMvc.perform(post("/login")
                .param("login", "naoexiste")
                .param("senha", "qualquer"))
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("erro"));
    }

    // 20
    @Test
    public void deveInvalidarSessaoAoFazerLogout() throws Exception {
        mockMvc.perform(get("/logout").sessionAttr("usuarioLogado", usuarioMock))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}
