package com.example.demo;

import com.example.demo.Repositories.LancamentoRepository;
import com.example.demo.Repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppController.class)
public class AppControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioRepository usuarioRepo;

    @MockBean
    private LancamentoRepository lancamentoRepo;

    private Usuario usuarioMock;

    @MockBean
    private com.example.demo.Services.EmailService emailService;

    @BeforeEach
    public void setup() {
        // Preparamos um utilizador padrão para ser usado nos testes que exigem login
        usuarioMock = new Usuario();
        usuarioMock.setId(1);
        usuarioMock.setLogin("admin");
        usuarioMock.setSenha("1234");
        usuarioMock.setNome("Administrador");
    }

    // ==========================================
    // 1. TESTES DE LOGIN E TELA INICIAL
    // ==========================================

    @Test
    public void deveCarregarPaginaDeLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test // (Teste Original 1)
    public void deveDarErroComLoginInvalido() throws Exception {
        when(usuarioRepo.findByLogin("admin")).thenReturn(Optional.empty());

        mockMvc.perform(post("/login")
                .param("login", "admin")
                .param("senha", "errada"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("erro", "Login inválido"));
    }

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

    @Test // (Teste Original 2)
    public void deveLogarComSucesso() throws Exception {
        when(usuarioRepo.findByLogin("admin")).thenReturn(Optional.of(usuarioMock));

        mockMvc.perform(post("/login")
                .param("login", "admin")
                .param("senha", "1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/lancamentos"));
    }

    @Test
    public void deveFalharSeFaltarParametroLoginNaRequisicao() throws Exception {
        mockMvc.perform(post("/login")
                .param("senha", "1234")) // Sem o parâmetro "login"
                .andExpect(status().isBadRequest()); // Erro 400
    }

    @Test
    public void deveFalharSeFaltarParametroSenhaNaRequisicao() throws Exception {
        mockMvc.perform(post("/login")
                .param("login", "admin")) // Sem o parâmetro "senha"
                .andExpect(status().isBadRequest()); // Erro 400
    }

    // ==========================================
    // 2. TESTES DE LISTAGEM E FILTROS
    // ==========================================

    @Test
    public void deveRedirecionarListagemSeNaoHouverUsuarioNaSessao() throws Exception {
        mockMvc.perform(get("/lancamentos"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void deveListarTodosOsLancamentosDoUsuarioLogadoSemFiltros() throws Exception {
        when(lancamentoRepo.findByUsuarioId(1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/lancamentos").sessionAttr("usuarioLogado", usuarioMock))
                .andExpect(status().isOk())
                .andExpect(view().name("lancamentos"))
                .andExpect(model().attributeExists("lista"));

        verify(lancamentoRepo, times(1)).findByUsuarioId(1);
    }

    @Test
    public void deveFiltrarLancamentosPorDescricao() throws Exception {
        mockMvc.perform(get("/lancamentos")
                .sessionAttr("usuarioLogado", usuarioMock)
                .param("descricao", "Conta de Luz"))
                .andExpect(status().isOk());

        verify(lancamentoRepo, times(1)).findComFiltros(eq(1), eq("Conta de Luz"), isNull(), isNull(), isNull());
    }

    @Test
    public void deveFiltrarLancamentosPorSituacao() throws Exception {
        mockMvc.perform(get("/lancamentos")
                .sessionAttr("usuarioLogado", usuarioMock)
                .param("situacao", "PENDENTE"))
                .andExpect(status().isOk());

        verify(lancamentoRepo, times(1)).findComFiltros(eq(1), isNull(), isNull(), isNull(), eq("PENDENTE"));
    }

    @Test
    public void deveFiltrarLancamentosPorDataEStatus() throws Exception {
        mockMvc.perform(get("/lancamentos")
                .sessionAttr("usuarioLogado", usuarioMock)
                .param("dataLancamento", "2026-05-10")
                .param("situacao", "CONCLUIDO"))
                .andExpect(status().isOk());

        verify(lancamentoRepo, times(1)).findComFiltros(eq(1), isNull(), eq(LocalDate.parse("2026-05-10")), isNull(),
                eq("CONCLUIDO"));
    }

    @Test
    public void deveLimparStringsVaziasDosFiltrosEBuscarApenasPorUsuario() throws Exception {
        // Simula o HTML enviando valores vazios (quando o utilizador clica em Filtrar
        // sem preencher)
        mockMvc.perform(get("/lancamentos")
                .sessionAttr("usuarioLogado", usuarioMock)
                .param("descricao", "")
                .param("situacao", ""))
                .andExpect(status().isOk());

        // Deve limpar os vazios e chamar o método sem filtros específicos
        verify(lancamentoRepo, times(1)).findByUsuarioId(1);
    }

    // ==========================================
    // 3. TESTES DE CRIAÇÃO E EDIÇÃO (CRUD)
    // ==========================================

    @Test
    public void deveCarregarAViewDeNovoLancamentoComObjetoVazio() throws Exception {
        mockMvc.perform(get("/lancamentos/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("form-lancamento"))
                .andExpect(model().attributeExists("lancamento"));
    }

    @Test
    public void deveRedirecionarParaLoginAoTentarSalvarSemSessao() throws Exception {
        mockMvc.perform(post("/lancamentos/salvar")
                .param("descricao", "Teste")
                .param("valor", "100.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/")); // Volta para a Home/Login
    }

    @Test
    public void deveSalvarNovoLancamentoComUsuarioDaSessaoERedirecionar() throws Exception {
        mockMvc.perform(post("/lancamentos/salvar")
                .sessionAttr("usuarioLogado", usuarioMock)
                .param("descricao", "Salário")
                .param("valor", "5000.00")
                .param("tipoLancamento", "RECEITA")
                .param("situacao", "CONCLUIDO")
                .param("dataLancamento", "2026-04-15"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/lancamentos"));

        verify(lancamentoRepo, times(1)).save(any(Lancamento.class));
    }

    @Test
    public void deveCarregarTelaDeEdicaoQuandoLancamentoExiste() throws Exception {
        Lancamento lancamentoMock = new Lancamento();
        lancamentoMock.setId(10L);
        when(lancamentoRepo.findById(10L)).thenReturn(Optional.of(lancamentoMock));

        mockMvc.perform(get("/lancamentos/editar/10"))
                .andExpect(status().isOk())
                .andExpect(view().name("form-lancamento"))
                .andExpect(model().attributeExists("lancamento"));
    }

    @Test
    public void deveRedirecionarParaListagemQuandoLancamentoNaoExisteNaEdicao() throws Exception {
        when(lancamentoRepo.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/lancamentos/editar/99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/lancamentos"));
    }

    @Test
    public void deveChamarDelecaoEVoltarParaListagemAoExcluir() throws Exception {
        mockMvc.perform(get("/lancamentos/excluir/5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/lancamentos"));

        verify(lancamentoRepo, times(1)).deleteById(5L);
    }

    // ==========================================
    // 4. TESTE DE EXPORTAÇÃO DE PDF
    // ==========================================

    @Test
    public void deveGerarArquivoPdfComOsLancamentos() throws Exception {
        Lancamento l1 = new Lancamento();
        l1.setDescricao("Internet");
        l1.setValor(new BigDecimal("100.00"));

        when(lancamentoRepo.findAll()).thenReturn(List.of(l1));

        mockMvc.perform(get("/lancamentos/pdf"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=lancamentos.pdf"));
    }
}