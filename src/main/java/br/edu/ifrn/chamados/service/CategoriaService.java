package br.edu.ifrn.chamados.service;

import br.edu.ifrn.chamados.exception.RecursoNaoEncontradoException;
import br.edu.ifrn.chamados.exception.RegraNegocioException;
import br.edu.ifrn.chamados.model.Categoria;
import br.edu.ifrn.chamados.model.Setor;
import br.edu.ifrn.chamados.repository.CategoriaRepository;
import br.edu.ifrn.chamados.repository.ChamadoRepository;
import br.edu.ifrn.chamados.specification.FiltroPorNome;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final ChamadoRepository chamadoRepository;
    private final SetorService setorService;

    @Transactional(readOnly = true)
    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id).orElseThrow(() ->
            new RecursoNaoEncontradoException("Categoria não encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Categoria> listar(
        String nome,
        Long setorId,
        Pageable pageable
    ) {
        Specification<Categoria> filtro = FiltroPorNome.contem(nome);
        if (setorId != null) {
            filtro = filtro.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("setor").get("id"), setorId));
        }

        return categoriaRepository.findAll(filtro, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Categoria> listarPorSetor(
        Long setorId,
        Pageable pageable
    ) {
        setorService.buscarPorId(setorId);
        return categoriaRepository.findBySetorId(setorId, pageable);
    }

    @Transactional
    public Categoria criar(
        String nome,
        String descricao,
        Long setorId
    ) {
        validarNome(nome);
        Setor setor = setorService.buscarPorId(setorId);

        Categoria categoria = new Categoria();
        categoria.setNome(nome.trim());
        categoria.setDescricao(descricao);
        categoria.setSetor(setor);
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public Categoria atualizar(
        Long id,
        String nome,
        String descricao,
        Long setorId
    ) {
        validarNome(nome);
        Categoria categoria = buscarPorId(id);
        Setor setor = setorService.buscarPorId(setorId);

        boolean mudouDeSetor = !categoria.getSetor().getId().equals(setorId);
        if (mudouDeSetor && chamadoRepository.existsByCategoriaId(id)) {
            throw new RegraNegocioException("Categoria com chamados não pode mudar de setor");
        }

        categoria.setNome(nome.trim());
        categoria.setDescricao(descricao);
        categoria.setSetor(setor);
        return categoria;
    }

    @Transactional
    public void excluir(Long id) {
        Categoria categoria = buscarPorId(id);
        if (chamadoRepository.existsByCategoriaId(id)) {
            throw new RegraNegocioException("Categoria possui chamados vinculados");
        }
        categoriaRepository.delete(categoria);
    }

    private void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome da categoria é obrigatório");
        }
    }
}
