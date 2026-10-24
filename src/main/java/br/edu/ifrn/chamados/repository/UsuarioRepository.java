package br.edu.ifrn.chamados.repository;

import br.edu.ifrn.chamados.model.Usuario;
import br.edu.ifrn.chamados.model.enums.TipoUsuario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {
  Optional<Usuario> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByEmailAndIdNot(String email, Long id);

  boolean existsBySetorId(Long setorId);

  Page<Usuario> findByNomeContainingIgnoreCase(
    String nome,
    Pageable pageable
  );

  Page<Usuario> findByTipo(
    TipoUsuario tipo,
    Pageable pageable
  );

  Page<Usuario> findBySetorId(
    Long setorId,
    Pageable pageable
  );
}
