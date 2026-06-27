package com.example.demo.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.demo.Lancamento;
import java.time.LocalDate;
import java.util.List;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    List<Lancamento> findByUsuarioId(Integer usuarioId);

    @Query("SELECT l FROM Lancamento l WHERE l.usuario.id = :usuarioId AND " +
            "(:descricao IS NULL OR l.descricao LIKE %:descricao%) AND " +
            "(cast(:dataInicio as date) IS NULL OR l.dataLancamento >= :dataInicio) AND " +
            "(cast(:dataFim as date) IS NULL OR l.dataLancamento <= :dataFim) AND " +
            "(:tipoLancamento IS NULL OR l.tipoLancamento = :tipoLancamento) AND " +
            "(:situacao IS NULL OR l.situacao = :situacao)")
    List<Lancamento> findComFiltros(
            @Param("usuarioId") Integer usuarioId,
            @Param("descricao") String descricao,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            @Param("tipoLancamento") String tipoLancamento,
            @Param("situacao") String situacao);
}
