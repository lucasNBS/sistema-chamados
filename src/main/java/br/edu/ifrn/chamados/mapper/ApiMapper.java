package br.edu.ifrn.chamados.mapper;

import br.edu.ifrn.chamados.dto.response.CategoriaResponse;
import br.edu.ifrn.chamados.dto.response.ChamadoResponse;
import br.edu.ifrn.chamados.dto.response.InteracaoResponse;
import br.edu.ifrn.chamados.dto.response.SetorResponse;
import br.edu.ifrn.chamados.dto.response.UsuarioResponse;
import br.edu.ifrn.chamados.model.Categoria;
import br.edu.ifrn.chamados.model.Chamado;
import br.edu.ifrn.chamados.model.Interacao;
import br.edu.ifrn.chamados.model.Setor;
import br.edu.ifrn.chamados.model.Usuario;

public final class ApiMapper {
    private ApiMapper() {
    }

    public static SetorResponse setor(Setor setor) {
        return new SetorResponse(setor.getId(), setor.getNome(), setor.getSigla());
    }

    public static CategoriaResponse categoria(Categoria categoria) {
        return new CategoriaResponse(
            categoria.getId(),
            categoria.getNome(),
            categoria.getDescricao(),
            categoria.getSetor().getId()
        );
    }

    public static UsuarioResponse usuario(Usuario usuario) {
        return new UsuarioResponse(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getMatricula(),
            usuario.getTipo(),
            usuario.getSetor() == null ? null : usuario.getSetor().getId()
        );
    }

    public static ChamadoResponse chamado(Chamado chamado) {
        return new ChamadoResponse(
            chamado.getId(),
            chamado.getTitulo(),
            chamado.getDescricao(),
            chamado.getLocal(),
            chamado.getPrioridade(),
            chamado.getStatus(),
            chamado.getDataAbertura(),
            chamado.getDataFechamento(),
            chamado.getSolucao(),
            chamado.getSolicitante().getId(),
            chamado.getCategoria().getId(),
            chamado.getTecnico() == null ? null : chamado.getTecnico().getId()
        );
    }

    public static InteracaoResponse interacao(Interacao interacao) {
        return new InteracaoResponse(
            interacao.getId(),
            interacao.getTexto(),
            interacao.getDataHora(),
            interacao.getAutor().getId()
        );
    }
}
