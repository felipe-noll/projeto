package com.example.app.financeiro.controller;

import com.example.app.auth.model.Usuario;
import com.example.app.financeiro.model.Lancamento;
import com.example.app.financeiro.repository.LancamentoRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class FinanceiroController {

    @Autowired
    private LancamentoRepository lancamentoRepo;

    @GetMapping("/lancamentos")
    public String listar(
            @RequestParam(required = false) String descricao,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) String tipoLancamento,
            @RequestParam(required = false) String situacao,
            HttpSession session,
            Model model) {
        String string;

        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null)
            return "redirect:/";

        if (descricao != null && descricao.trim().isEmpty())
            descricao = null;
        if (tipoLancamento != null && tipoLancamento.trim().isEmpty())
            tipoLancamento = null;
        if (situacao != null && situacao.trim().isEmpty())
            situacao = null;

        if (descricao != null || dataInicio != null || dataFim != null || tipoLancamento != null || situacao != null) {
            model.addAttribute("lista", lancamentoRepo.findComFiltros(
                    usuarioLogado.getId(), descricao, dataInicio, dataFim, tipoLancamento, situacao));
        } else {
            model.addAttribute("lista", lancamentoRepo.findByUsuarioId(usuarioLogado.getId()));
        }
        return "lancamentos";
    }

    @GetMapping("/lancamentos/novo")
    public String novoLancamento(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null)
            return "redirect:/";
        model.addAttribute("lancamento", new Lancamento());
        return "form-lancamento";
    }

    @PostMapping("/lancamentos/salvar")
    public String salvarLancamento(Lancamento lancamento, HttpSession session) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null)
            return "redirect:/";
        lancamento.setUsuario(usuarioLogado);
        lancamentoRepo.save(lancamento);
        return "redirect:/lancamentos";
    }

    @GetMapping("/lancamentos/editar/{id}")
    public String editarLancamento(@PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null)
            return "redirect:/";
        Optional<Lancamento> lancamento = lancamentoRepo.findById(id);
        if (lancamento.isPresent()) {
            model.addAttribute("lancamento", lancamento.get());
            return "form-lancamento";
        }
        return "redirect:/lancamentos";
    }

    @GetMapping("/lancamentos/excluir/{id}")
    public String excluirLancamento(@PathVariable Long id) {
        lancamentoRepo.deleteById(id);
        return "redirect:/lancamentos";
    }

    @GetMapping("/lancamentos/pdf")
    public void exportarPdf(HttpSession session, HttpServletResponse response) throws IOException {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            response.sendRedirect("/");
            return;
        }
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=lancamentos.pdf");
        List<Lancamento> lancamentos = lancamentoRepo.findByUsuarioId(usuarioLogado.getId());
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        document.add(new Paragraph("Relatório de Lançamentos - " + usuarioLogado.getNome()));
        document.add(new Paragraph(" "));
        for (Lancamento l : lancamentos) {
            document.add(new Paragraph(
                    l.getDescricao() + " | R$ " + l.getValor() + " | " +
                            l.getDataLancamento() + " | " + l.getTipoLancamento() + " | " + l.getSituacao()));
        }
        document.close();
    }
}
