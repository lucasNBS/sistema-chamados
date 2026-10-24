package br.edu.ifrn.chamados.service;

import br.edu.ifrn.chamados.exception.RecursoNaoEncontradoException;
import br.edu.ifrn.chamados.exception.RegraNegocioException;
import br.edu.ifrn.chamados.model.Categoria;
import br.edu.ifrn.chamados.model.Chamado;
import br.edu.ifrn.chamados.model.Usuario;
import br.edu.ifrn.chamados.model.enums.Prioridade;
import br.edu.ifrn.chamados.model.enums.StatusChamado;
import br.edu.ifrn.chamados.model.enums.TipoUsuario;
import br.edu.ifrn.chamados.repository.CategoriaRepository;
import br.edu.ifrn.chamados.repository.ChamadoRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChamadoService {
    private final ChamadoRepository chamadoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public Chamado buscarPorId(Long id) {
        return chamadoRepository.findById(id).orElseThrow(() ->
            new RecursoNaoEncontradoException("Chamado não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Chamado> listar(
        StatusChamado status,
        Prioridade prioridade,
        Long setorId,
        Long categoriaId,
        Long solicitanteId,
        Long tecnicoId,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        Pageable pageable
    ) {
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("dataInicio deve ser anterior ou igual a dataFim");
        }

        Specification<Chamado> filtro = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        if (status != null) {
            filtro = filtro.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status));
        }
        if (prioridade != null) {
            filtro = filtro.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("prioridade"), prioridade));
        }
        if (setorId != null) {
            filtro = filtro.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("categoria").get("setor").get("id"), setorId));
        }
        if (categoriaId != null) {
            filtro = filtro.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("categoria").get("id"), categoriaId));
        }
        if (solicitanteId != null) {
            filtro = filtro.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("solicitante").get("id"), solicitanteId));
        }
        if (tecnicoId != null) {
            filtro = filtro.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("tecnico").get("id"), tecnicoId));
        }
        if (dataInicio != null) {
            filtro = filtro.and((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("dataAbertura"), dataInicio));
        }
        if (dataFim != null) {
            filtro = filtro.and((root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("dataAbertura"), dataFim));
        }
        return chamadoRepository.findAll(filtro, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Chamado> listarPorUsuario(
        Long usuarioId,
        StatusChamado status,
        Pageable pageable
    ) {
        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        String participante = usuario.getTipo() == TipoUsuario.SOLICITANTE ? "solicitante" : "tecnico";
        Specification<Chamado> filtro = (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get(participante).get("id"), usuarioId);

        if (status != null) {
            filtro = filtro.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status));
        }
        return chamadoRepository.findAll(filtro, pageable);
    }

    @Transactional
    public Chamado abrir(
        Long solicitanteId,
        Long categoriaId,
        String titulo,
        String descricao,
        String local,
        Prioridade prioridade
    ) {
        exigirTexto(titulo, "Título");
        exigirTexto(descricao, "Descrição");
        exigirTexto(local, "Local");
        Usuario solicitante = usuarioService.buscarPorId(solicitanteId);
        if (solicitante.getTipo() != TipoUsuario.SOLICITANTE) {
            throw new RegraNegocioException("Somente solicitantes podem abrir chamados");
        }
        Categoria categoria = categoriaRepository.findById(categoriaId).orElseThrow(() ->
            new RecursoNaoEncontradoException("Categoria não encontrada: " + categoriaId));

        Chamado chamado = new Chamado();
        chamado.setTitulo(titulo.trim());
        chamado.setDescricao(descricao.trim());
        chamado.setLocal(local.trim());
        chamado.setPrioridade(prioridade == null ? Prioridade.MEDIA : prioridade);
        chamado.setStatus(StatusChamado.ABERTO);
        chamado.setDataAbertura(LocalDateTime.now());
        chamado.setSolicitante(solicitante);
        chamado.setCategoria(categoria);
        return chamadoRepository.save(chamado);
    }

    @Transactional
    public Chamado assumir(
        Long chamadoId,
        Long tecnicoId
    ) {
        Chamado chamado = buscarPorId(chamadoId);
        exigirStatus(chamado, StatusChamado.ABERTO);
        Usuario tecnico = buscarTecnicoValido(tecnicoId);
        if (!tecnico.getSetor().getId().equals(chamado.getCategoria().getSetor().getId())) {
            throw new RegraNegocioException("Técnico não pertence ao setor da categoria");
        }

        chamado.setTecnico(tecnico);
        chamado.setStatus(StatusChamado.EM_ATENDIMENTO);
        return chamado;
    }

    @Transactional
    public Chamado resolver(
        Long chamadoId,
        Long tecnicoId,
        String solucao
    ) {
        Chamado chamado = buscarPorId(chamadoId);
        exigirStatus(chamado, StatusChamado.EM_ATENDIMENTO);
        exigirTexto(solucao, "Solução");
        if (!chamado.getTecnico().getId().equals(tecnicoId)) {
            throw new RegraNegocioException("Apenas o técnico atribuído pode resolver");
        }

        chamado.setSolucao(solucao.trim());
        chamado.setDataFechamento(LocalDateTime.now());
        chamado.setStatus(StatusChamado.RESOLVIDO);
        return chamado;
    }

    @Transactional
    public Chamado cancelar(
        Long chamadoId,
        Long solicitanteId
    ) {
        Chamado chamado = buscarPorId(chamadoId);
        exigirStatus(chamado, StatusChamado.ABERTO);
        if (!chamado.getSolicitante().getId().equals(solicitanteId)) {
            throw new RegraNegocioException("Apenas o solicitante pode cancelar");
        }

        chamado.setStatus(StatusChamado.CANCELADO);
        chamado.setDataFechamento(LocalDateTime.now());
        return chamado;
    }

    private Usuario buscarTecnicoValido(Long id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        if (usuario.getTipo() != TipoUsuario.TECNICO || usuario.getSetor() == null) {
            throw new RegraNegocioException("Usuário não é técnico de um setor");
        }
        return usuario;
    }

    private void exigirStatus(
        Chamado chamado,
        StatusChamado esperado
    ) {
        if (chamado.getStatus() != esperado) {
            throw new RegraNegocioException("Transição inválida a partir de " + chamado.getStatus());
        }
    }

    private void exigirTexto(
        String valor,
        String campo
    ) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " é obrigatório");
        }
    }
}
