package br.edu.ifrn.chamados.service;

import br.edu.ifrn.chamados.exception.RecursoNaoEncontradoException;
import br.edu.ifrn.chamados.exception.RegraNegocioException;
import br.edu.ifrn.chamados.model.Setor;
import br.edu.ifrn.chamados.repository.CategoriaRepository;
import br.edu.ifrn.chamados.repository.SetorRepository;
import br.edu.ifrn.chamados.repository.UsuarioRepository;
import br.edu.ifrn.chamados.specification.FiltroPorNome;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SetorService {
    private final SetorRepository setorRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public Setor buscarPorId(Long id) {
        return setorRepository.findById(id).orElseThrow(() ->
            new RecursoNaoEncontradoException("Setor não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Setor> listar(
        String nome,
        Pageable pageable
    ) {
        return setorRepository.findAll(FiltroPorNome.contem(nome), pageable);
    }

    @Transactional
    public Setor criar(Setor dados) {
        validarDados(dados);
        String sigla = dados.getSigla().trim();
        if (setorRepository.existsBySigla(sigla)) {
            throw new RegraNegocioException("Sigla de setor já cadastrada");
        }

        Setor setor = new Setor();
        setor.setNome(dados.getNome().trim());
        setor.setSigla(sigla);
        return setorRepository.save(setor);
    }

    @Transactional
    public Setor atualizar(
        Long id,
        Setor dados
    ) {
        validarDados(dados);
        Setor setor = buscarPorId(id);
        String sigla = dados.getSigla().trim();
        if (setorRepository.existsBySiglaAndIdNot(sigla, id)) {
            throw new RegraNegocioException("Sigla de setor já cadastrada");
        }

        setor.setNome(dados.getNome().trim());
        setor.setSigla(sigla);
        return setor;
    }

    @Transactional
    public void excluir(Long id) {
        Setor setor = buscarPorId(id);
        if (categoriaRepository.existsBySetorId(id) || usuarioRepository.existsBySetorId(id)) {
            throw new RegraNegocioException("Setor possui categorias ou usuários vinculados");
        }
        setorRepository.delete(setor);
    }

    private void validarDados(Setor dados) {
        if (dados == null || dados.getNome() == null || dados.getNome().isBlank()
                || dados.getSigla() == null || dados.getSigla().isBlank()) {
            throw new IllegalArgumentException("Nome e sigla são obrigatórios");
        }
    }
}
