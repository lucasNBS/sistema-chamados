package br.edu.ifrn.chamados.controller;

import br.edu.ifrn.chamados.dto.request.SetorRequest;
import br.edu.ifrn.chamados.dto.response.PageResponse;
import br.edu.ifrn.chamados.dto.response.SetorResponse;
import br.edu.ifrn.chamados.mapper.ApiMapper;
import br.edu.ifrn.chamados.model.Setor;
import br.edu.ifrn.chamados.service.SetorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/setores")
@RequiredArgsConstructor
public class SetorController {
    private final SetorService setorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SetorResponse criar(@Valid @RequestBody SetorRequest request) {
        Setor setor = new Setor();
        setor.setNome(request.nome());
        setor.setSigla(request.sigla());
        return ApiMapper.setor(setorService.criar(setor));
    }

    @GetMapping("/{id}")
    public SetorResponse buscar(@PathVariable Long id) {
        return ApiMapper.setor(setorService.buscarPorId(id));
    }

    @GetMapping
    public PageResponse<SetorResponse> listar(
        @RequestParam(required = false) String nome,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        return PageResponse.from(setorService.listar(nome, pageable).map(ApiMapper::setor));
    }

    @PutMapping("/{id}")
    public SetorResponse atualizar(
        @PathVariable Long id,
        @Valid @RequestBody SetorRequest request
    ) {
        Setor setor = new Setor();
        setor.setNome(request.nome());
        setor.setSigla(request.sigla());
        return ApiMapper.setor(setorService.atualizar(id, setor));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        setorService.excluir(id);
    }
}
