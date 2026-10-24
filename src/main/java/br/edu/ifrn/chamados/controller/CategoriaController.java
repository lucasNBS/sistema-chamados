package br.edu.ifrn.chamados.controller;

import br.edu.ifrn.chamados.dto.request.CategoriaRequest;
import br.edu.ifrn.chamados.dto.response.CategoriaResponse;
import br.edu.ifrn.chamados.dto.response.PageResponse;
import br.edu.ifrn.chamados.mapper.ApiMapper;
import br.edu.ifrn.chamados.service.CategoriaService;

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
@RequestMapping
@RequiredArgsConstructor
public class CategoriaController {
    private final CategoriaService categoriaService;

    @PostMapping("/categorias")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoriaResponse criar(@Valid @RequestBody CategoriaRequest request) {
        return ApiMapper.categoria(categoriaService.criar(
            request.nome(),
            request.descricao(),
            request.setorId()
        ));
    }

    @GetMapping("/categorias/{id}")
    public CategoriaResponse buscar(@PathVariable Long id) {
        return ApiMapper.categoria(categoriaService.buscarPorId(id));
    }

    @GetMapping("/categorias")
    public PageResponse<CategoriaResponse> listar(
        @RequestParam(required = false) String nome,
        @RequestParam(required = false) Long setorId,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        return PageResponse.from(categoriaService.listar(nome, setorId, pageable).map(ApiMapper::categoria));
    }

    @GetMapping("/setores/{id}/categorias")
    public PageResponse<CategoriaResponse> porSetor(
        @PathVariable Long id,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        return PageResponse.from(categoriaService.listarPorSetor(id, pageable).map(ApiMapper::categoria));
    }

    @PutMapping("/categorias/{id}")
    public CategoriaResponse atualizar(
        @PathVariable Long id,
        @Valid @RequestBody CategoriaRequest request
    ) {
        return ApiMapper.categoria(categoriaService.atualizar(
            id,
            request.nome(),
            request.descricao(),
            request.setorId()
        ));
    }

    @DeleteMapping("/categorias/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        categoriaService.excluir(id);
    }
}
