package br.edu.ifrn.chamados.repository;

import br.edu.ifrn.chamados.model.Interacao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InteracaoRepository extends JpaRepository<Interacao, Long> {
  Page<Interacao> findByChamadoIdOrderByDataHoraAsc(
    Long chamadoId,
    Pageable pageable
  );

  @EntityGraph(attributePaths = "autor")
  List<Interacao> findAllByChamadoIdOrderByDataHoraAsc(Long chamadoId);
}
