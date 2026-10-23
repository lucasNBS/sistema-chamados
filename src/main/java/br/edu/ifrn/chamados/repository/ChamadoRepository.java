package br.edu.ifrn.chamados.repository;

import br.edu.ifrn.chamados.model.Chamado;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChamadoRepository extends JpaRepository<Chamado, Long>, JpaSpecificationExecutor<Chamado> {
  Page<Chamado> findBySolicitanteId(
    Long solicitanteId,
    Pageable pageable
  );

  Page<Chamado> findByTecnicoId(
    Long tecnicoId,
    Pageable pageable
  );

  boolean existsByCategoriaId(Long categoriaId);

  boolean existsByCategoria_Setor_Id(Long setorId);
}
