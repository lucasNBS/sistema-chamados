package br.edu.ifrn.chamados.specification;

import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public final class FiltroPorNome {
    private FiltroPorNome() {
    }

    public static <T> Specification<T> contem(String nome) {
        if (nome == null || nome.isBlank()) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        }

        String trecho = nome.trim().toLowerCase(Locale.ROOT);
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.gt(
                criteriaBuilder.locate(criteriaBuilder.lower(root.get("nome")), trecho),
                0
            );
    }
}
