package br.edu.ifrn.chamados.controller;

import br.edu.ifrn.chamados.dto.request.UsuarioRequest;
import br.edu.ifrn.chamados.dto.response.ChamadoResponse;
import br.edu.ifrn.chamados.dto.response.PageResponse;
import br.edu.ifrn.chamados.dto.response.UsuarioResponse;
import br.edu.ifrn.chamados.mapper.ApiMapper;
import br.edu.ifrn.chamados.model.enums.StatusChamado;
import br.edu.ifrn.chamados.model.enums.TipoUsuario;
import br.edu.ifrn.chamados.service.ChamadoService;
import br.edu.ifrn.chamados.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final ChamadoService chamadoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse criar(@Valid @RequestBody UsuarioRequest request) {
        return ApiMapper.usuario(usuarioService.criar(
            request.nome(),
            request.email(),
            request.matricula(),
            request.tipo(),
            request.setorId()
        ));
    }

    @GetMapping("/{id}")
    public UsuarioResponse buscar(@PathVariable Long id) {
        return ApiMapper.usuario(usuarioService.buscarPorId(id));
    }

    @GetMapping
    public PageResponse<UsuarioResponse> listar(
        @RequestParam(required = false) String nome,
        @RequestParam(required = false) TipoUsuario tipo,
        @RequestParam(required = false) Long setorId,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        return PageResponse.from(usuarioService.listar(nome, tipo, setorId, pageable).map(ApiMapper::usuario));
    }

    @PutMapping("/{id}")
    public UsuarioResponse atualizar(
        @PathVariable Long id,
        @Valid @RequestBody UsuarioRequest request
    ) {
        return ApiMapper.usuario(usuarioService.atualizar(
            id,
            request.nome(),
            request.email(),
            request.matricula(),
            request.tipo(),
            request.setorId()
        ));
    }

    @GetMapping("/{id}/chamados")
    public PageResponse<ChamadoResponse> chamados(
        @PathVariable Long id,
        @RequestParam(required = false) StatusChamado status,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        return PageResponse.from(chamadoService.listarPorUsuario(id, status, pageable).map(ApiMapper::chamado));
    }
}
