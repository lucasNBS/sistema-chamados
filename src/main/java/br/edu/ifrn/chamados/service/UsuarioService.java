package br.edu.ifrn.chamados.service;

import br.edu.ifrn.chamados.exception.RecursoNaoEncontradoException;
import br.edu.ifrn.chamados.exception.RegraNegocioException;
import br.edu.ifrn.chamados.model.Setor;
import br.edu.ifrn.chamados.model.Usuario;
import br.edu.ifrn.chamados.model.enums.TipoUsuario;
import br.edu.ifrn.chamados.repository.ChamadoRepository;
import br.edu.ifrn.chamados.repository.UsuarioRepository;
import br.edu.ifrn.chamados.specification.FiltroPorNome;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final ChamadoRepository chamadoRepository;
    private final SetorService setorService;

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElseThrow(() ->
            new RecursoNaoEncontradoException("Usuário não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Usuario> listar(
        String nome,
        TipoUsuario tipo,
        Long setorId,
        Pageable pageable
    ) {
        Specification<Usuario> filtro = FiltroPorNome.contem(nome);
        if (tipo != null) {
            filtro = filtro.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("tipo"), tipo));
        }
        if (setorId != null) {
            filtro = filtro.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("setor").get("id"), setorId));
        }
        return usuarioRepository.findAll(filtro, pageable);
    }

    @Transactional
    public Usuario criar(
        String nome,
        String email,
        String matricula,
        TipoUsuario tipo,
        Long setorId
    ) {
        validarDados(nome, email, matricula, tipo);
        if (usuarioRepository.existsByEmail(email.trim())) {
            throw new RegraNegocioException("Email já cadastrado");
        }

        Usuario usuario = new Usuario();
        preencherDados(usuario, nome, email, matricula, tipo, setorId);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario atualizar(
        Long id,
        String nome,
        String email,
        String matricula,
        TipoUsuario tipo,
        Long setorId
    ) {
        validarDados(nome, email, matricula, tipo);
        Usuario usuario = buscarPorId(id);
        if (usuarioRepository.existsByEmailAndIdNot(email.trim(), id)) {
            throw new RegraNegocioException("Email já cadastrado");
        }

        validarAlteracaoDeVinculos(usuario, tipo, setorId);
        preencherDados(usuario, nome, email, matricula, tipo, setorId);
        return usuario;
    }

    private void validarAlteracaoDeVinculos(
        Usuario usuario,
        TipoUsuario tipo,
        Long setorId
    ) {
        Long id = usuario.getId();
        if (usuario.getTipo() != tipo) {
            if (usuario.getTipo() == TipoUsuario.SOLICITANTE && chamadoRepository.existsBySolicitanteId(id)) {
                throw new RegraNegocioException("Não é possível alterar o tipo: usuário possui chamados como solicitante");
            }
            if (usuario.getTipo() == TipoUsuario.TECNICO && chamadoRepository.existsByTecnicoId(id)) {
                throw new RegraNegocioException("Não é possível alterar o tipo: usuário possui chamados atribuídos como técnico");
            }
        }

        if (usuario.getTipo() == TipoUsuario.TECNICO && tipo == TipoUsuario.TECNICO
                && !usuario.getSetor().getId().equals(setorId)
                && chamadoRepository.existsByTecnicoId(id)) {
            throw new RegraNegocioException("Não é possível alterar o setor: técnico possui chamados atribuídos");
        }
    }

    private void preencherDados(
        Usuario usuario,
        String nome,
        String email,
        String matricula,
        TipoUsuario tipo,
        Long setorId
    ) {
        Setor setor = null;
        if (tipo == TipoUsuario.TECNICO) {
            if (setorId == null) {
                throw new RegraNegocioException("Técnico deve pertencer a um setor");
            }
            setor = setorService.buscarPorId(setorId);
        } else if (setorId != null) {
            throw new RegraNegocioException("Solicitante não deve ter setor técnico vinculado");
        }

        usuario.setNome(nome.trim());
        usuario.setEmail(email.trim());
        usuario.setMatricula(matricula.trim());
        usuario.setTipo(tipo);
        usuario.setSetor(setor);
    }

    private void validarDados(
        String nome,
        String email,
        String matricula,
        TipoUsuario tipo
    ) {
        if (nome == null || nome.isBlank() || email == null || email.isBlank()
                || matricula == null || matricula.isBlank() || tipo == null) {
            throw new IllegalArgumentException("Nome, email, matrícula e tipo são obrigatórios");
        }
    }
}
