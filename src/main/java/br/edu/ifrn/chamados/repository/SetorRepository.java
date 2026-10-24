package br.edu.ifrn.chamados.repository;

import br.edu.ifrn.chamados.model.Setor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface SetorRepository extends JpaRepository<Setor, Long>, JpaSpecificationExecutor<Setor> {
  Optional<Setor> findBySigla(String sigla);

  boolean existsBySigla(String sigla);

  boolean existsBySiglaAndIdNot(String sigla, Long id);
}
