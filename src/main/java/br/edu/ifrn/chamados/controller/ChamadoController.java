package br.edu.ifrn.chamados.controller;

import br.edu.ifrn.chamados.dto.request.AssumirChamadoRequest;
import br.edu.ifrn.chamados.dto.request.CancelarChamadoRequest;
import br.edu.ifrn.chamados.dto.request.ChamadoRequest;
import br.edu.ifrn.chamados.dto.request.InteracaoRequest;
import br.edu.ifrn.chamados.dto.request.ResolverChamadoRequest;
import br.edu.ifrn.chamados.dto.response.ChamadoDetalheResponse;
import br.edu.ifrn.chamados.dto.response.ChamadoResponse;
import br.edu.ifrn.chamados.dto.response.InteracaoResponse;
import br.edu.ifrn.chamados.dto.response.PageResponse;
import br.edu.ifrn.chamados.mapper.ApiMapper;
import br.edu.ifrn.chamados.model.enums.Prioridade;
import br.edu.ifrn.chamados.model.enums.StatusChamado;
import br.edu.ifrn.chamados.service.ChamadoService;
import br.edu.ifrn.chamados.service.InteracaoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/chamados")
@RequiredArgsConstructor
public class ChamadoController {
    private final ChamadoService chamadoService;
    private final InteracaoService interacaoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChamadoResponse criar(@Valid @RequestBody ChamadoRequest request) {
        return ApiMapper.chamado(chamadoService.abrir(
            request.solicitanteId(),
            request.categoriaId(),
            request.titulo(),
            request.descricao(),
            request.local(),
            request.prioridade()
        ));
    }

    @GetMapping
    public PageResponse<ChamadoResponse> listar(
        @RequestParam(required = false) StatusChamado status,
        @RequestParam(required = false) Prioridade prioridade,
        @RequestParam(required = false) Long setorId,
        @RequestParam(required = false) Long categoriaId,
        @RequestParam(required = false) Long solicitanteId,
        @RequestParam(required = false) Long tecnicoId,
        @RequestParam(required = false) LocalDateTime dataInicio,
        @RequestParam(required = false) LocalDateTime dataFim,
        @PageableDefault(size = 10, sort = "dataAbertura", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return PageResponse.from(chamadoService.listar(
            status,
            prioridade,
            setorId,
            categoriaId,
            solicitanteId,
            tecnicoId,
            dataInicio,
            dataFim,
            pageable
        ).map(ApiMapper::chamado));
    }

    @GetMapping("/{id}")
    public ChamadoDetalheResponse buscar(@PathVariable Long id) {
        ChamadoResponse chamado = ApiMapper.chamado(chamadoService.buscarPorId(id));
        List<InteracaoResponse> interacoes = interacaoService.listarTodasPorChamado(id).stream()
            .map(ApiMapper::interacao)
            .toList();
        return new ChamadoDetalheResponse(chamado, interacoes);
    }

    @PatchMapping("/{id}/assumir")
    public ChamadoResponse assumir(
        @PathVariable Long id,
        @Valid @RequestBody AssumirChamadoRequest request
    ) {
        return ApiMapper.chamado(chamadoService.assumir(id, request.tecnicoId()));
    }

    @PatchMapping("/{id}/resolver")
    public ChamadoResponse resolver(
        @PathVariable Long id,
        @Valid @RequestBody ResolverChamadoRequest request
    ) {
        return ApiMapper.chamado(chamadoService.resolver(id, request.tecnicoId(), request.solucao()));
    }

    @PatchMapping("/{id}/cancelar")
    public ChamadoResponse cancelar(
        @PathVariable Long id,
        @Valid @RequestBody CancelarChamadoRequest request
    ) {
        return ApiMapper.chamado(chamadoService.cancelar(id, request.solicitanteId()));
    }

    @PostMapping("/{id}/interacoes")
    @ResponseStatus(HttpStatus.CREATED)
    public InteracaoResponse interagir(
        @PathVariable Long id,
        @Valid @RequestBody InteracaoRequest request
    ) {
        return ApiMapper.interacao(interacaoService.registrarInteracao(id, request.autorId(), request.texto()));
    }
}
