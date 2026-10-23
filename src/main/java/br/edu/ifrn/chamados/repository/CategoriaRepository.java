package br.edu.ifrn.chamados.repository;

import br.edu.ifrn.chamados.model.Categoria;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
  Page<Categoria> findBySetorId(
    Long setorId,
    Pageable pageable
  );

  Page<Categoria> findByNomeContainingIgnoreCase(
    String nome,
    Pageable pageable
  );

  Page<Categoria> findByNomeContainingIgnoreCaseAndSetorId(
    String nome,
    Long setorId,
    Pageable pageable
  );

  boolean existsBySetorId(Long setorId);
}
