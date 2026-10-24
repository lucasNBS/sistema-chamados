package br.edu.ifrn.chamados.service;

import br.edu.ifrn.chamados.exception.RegraNegocioException;
import br.edu.ifrn.chamados.model.Chamado;
import br.edu.ifrn.chamados.model.Interacao;
import br.edu.ifrn.chamados.model.Usuario;
import br.edu.ifrn.chamados.model.enums.StatusChamado;
import br.edu.ifrn.chamados.repository.InteracaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InteracaoService {
    private final InteracaoRepository interacaoRepository;
    private final ChamadoService chamadoService;
    private final UsuarioService usuarioService;

    @Transactional
    public Interacao registrarInteracao(
        Long chamadoId,
        Long autorId,
        String texto
    ) {
        exigirTexto(texto);
        Chamado chamado = chamadoService.buscarPorId(chamadoId);
        if (chamado.getStatus() == StatusChamado.RESOLVIDO
                || chamado.getStatus() == StatusChamado.CANCELADO) {
            throw new RegraNegocioException("Chamado encerrado não aceita interações");
        }

        Usuario autor = usuarioService.buscarPorId(autorId);
        boolean autorSolicitante = chamado.getSolicitante().getId().equals(autorId);
        boolean autorTecnico = chamado.getTecnico() != null && chamado.getTecnico().getId().equals(autorId);
        if (!autorSolicitante && !autorTecnico) {
            throw new RegraNegocioException("Autor não participa deste chamado");
        }

        Interacao interacao = new Interacao();
        interacao.setTexto(texto.trim());
        interacao.setDataHora(LocalDateTime.now());
        interacao.setChamado(chamado);
        interacao.setAutor(autor);
        return interacaoRepository.save(interacao);
    }

    @Transactional(readOnly = true)
    public Page<Interacao> listarInteracoes(
        Long chamadoId,
        Pageable pageable
    ) {
        chamadoService.buscarPorId(chamadoId);
        return interacaoRepository.findByChamadoIdOrderByDataHoraAsc(chamadoId, pageable);
    }

    @Transactional(readOnly = true)
    public List<Interacao> listarTodasPorChamado(Long chamadoId) {
        chamadoService.buscarPorId(chamadoId);
        return interacaoRepository.findAllByChamadoIdOrderByDataHoraAsc(chamadoId);
    }

    private void exigirTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("Texto é obrigatório");
        }
    }
}
