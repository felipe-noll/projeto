package com.example.auth;

import com.example.auth.controller.AuthController;
import com.example.auth.model.Usuario;
import com.example.auth.repository.UsuarioRepository;
import com.example.auth.service.EmailService;
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

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioRepository usuarioRepo;

    @MockBean
    private EmailService emailService;

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
                .param("senha", "senhaErrada"))
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
                .andExpect(status().is3xxRedirection());
    }

    // 5
    @Test
    public void deveFalharSeFaltarParametroLoginNaRequisicao() throws Exception {
        mockMvc.perform(post("/login")
                .param("senha", "1234"))
                .andExpect(status().isBadRequest());
    }

    // 6
    @Test
    public void deveFalharSeFaltarParametroSenhaNaRequisicao() throws Exception {
        mockMvc.perform(post("/login")
                .param("login", "admin"))
                .andExpect(status().isBadRequest());
    }

    // 7
    @Test
    public void deveRedirecionarEditarPerfilSemSessao() throws Exception {
        mockMvc.perform(get("/perfil/editar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    // 8
    @Test
    public void deveCarregarFormularioDeEditarPerfilComSessao() throws Exception {
        mockMvc.perform(get("/perfil/editar")
                .sessionAttr("usuarioLogado", usuarioMock))
                .andExpect(status().isOk())
                .andExpect(view().name("editar-perfil"))
                .andExpect(model().attributeExists("usuario"));
    }

    // 9
    @Test
    public void deveRedirecionarSalvarPerfilSemSessao() throws Exception {
        mockMvc.perform(post("/perfil/salvar")
                .param("nome", "Novo Nome")
                .param("login", "admin")
                .param("email", "admin@email.com")
                .param("senha", "1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    // 10
    @Test
    public void deveSalvarPerfilComSessao() throws Exception {
        when(usuarioRepo.save(any(Usuario.class))).thenReturn(usuarioMock);
        mockMvc.perform(post("/perfil/salvar")
                .sessionAttr("usuarioLogado", usuarioMock)
                .param("nome", "Nome Atualizado")
                .param("login", "admin")
                .param("email", "admin@email.com")
                .param("senha", "1234"))
                .andExpect(status().is3xxRedirection());
        verify(usuarioRepo, times(1)).save(any(Usuario.class));
    }

    // 11
    @Test
    public void deveCarregarFormularioDeNovoUsuario() throws Exception {
        mockMvc.perform(get("/usuarios/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuario-form"));
    }

    // 12
    @Test
    public void deveRegistrarNovoUsuarioERedirecionar() throws Exception {
        mockMvc.perform(post("/registrar")
                .param("nome", "Felipe")
                .param("login", "felipe")
                .param("email", "felipe@email.com")
                .param("senha", "senha123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        verify(usuarioRepo, times(1)).save(any(Usuario.class));
    }

    // 13
    @Test
    public void deveEnviarEmailAoRegistrarUsuario() throws Exception {
        mockMvc.perform(post("/registrar")
                .param("nome", "Felipe")
                .param("login", "felipe")
                .param("email", "felipe@email.com")
                .param("senha", "senha123"))
                .andExpect(status().is3xxRedirection());
        verify(emailService, times(1)).enviarEmailSimples(
                eq("felipe@email.com"), any(), any());
    }

    // 14
    @Test
    public void deveEnviarEmailAoSalvarPerfil() throws Exception {
        when(usuarioRepo.save(any(Usuario.class))).thenReturn(usuarioMock);
        mockMvc.perform(post("/perfil/salvar")
                .sessionAttr("usuarioLogado", usuarioMock)
                .param("nome", "Admin")
                .param("login", "admin")
                .param("email", "admin@email.com")
                .param("senha", "1234"))
                .andExpect(status().is3xxRedirection());
        verify(emailService, times(1)).enviarEmailSimples(
                eq("admin@email.com"), any(), any());
    }

    // 15
    @Test
    public void deveNaoSalvarPerfilSemSessaoAtiva() throws Exception {
        mockMvc.perform(post("/perfil/salvar")
                .param("nome", "Qualquer")
                .param("login", "qualquer")
                .param("email", "q@q.com")
                .param("senha", "1234"))
                .andExpect(redirectedUrl("/"));
        verify(usuarioRepo, never()).save(any(Usuario.class));
    }

    // 16
    @Test
    public void deveNaoEnviarEmailSemSessaoAtiva() throws Exception {
        mockMvc.perform(post("/perfil/salvar")
                .param("nome", "Qualquer")
                .param("login", "qualquer")
                .param("email", "q@q.com")
                .param("senha", "1234"))
                .andExpect(redirectedUrl("/"));
        verify(emailService, never()).enviarEmailSimples(any(), any(), any());
    }

    // 17
    @Test
    public void deveRetornarLoginComErroQuandoUsuarioNaoExiste() throws Exception {
        when(usuarioRepo.findByLogin(any())).thenReturn(Optional.empty());
        mockMvc.perform(post("/login")
                .param("login", "naoexiste")
                .param("senha", "qualquer"))
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("erro"));
    }

    // 18
    @Test
    public void deveRetornarLoginComErroQuandoSenhaVazia() throws Exception {
        when(usuarioRepo.findByLogin("admin")).thenReturn(Optional.of(usuarioMock));
        mockMvc.perform(post("/login")
                .param("login", "admin")
                .param("senha", ""))
                .andExpect(view().name("login"))
                .andExpect(model().attribute("erro", "Login inválido"));
    }

    // 19
    @Test
    public void deveAtualizarSessaoAposSalvarPerfil() throws Exception {
        when(usuarioRepo.save(any(Usuario.class))).thenReturn(usuarioMock);
        mockMvc.perform(post("/perfil/salvar")
                .sessionAttr("usuarioLogado", usuarioMock)
                .param("nome", "Nome Novo")
                .param("login", "admin")
                .param("email", "novo@email.com")
                .param("senha", "1234"))
                .andExpect(status().is3xxRedirection());
    }

    // 20
    @Test
    public void deveCarregarPaginaDeLoginSemErros() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().hasNoErrors());
    }
}
