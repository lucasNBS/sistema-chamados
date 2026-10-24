# Sistema de Chamados do IFRN

API REST para registrar e acompanhar chamados de suporte do IFRN.

Solicitantes abrem chamados por categoria; técnicos do setor responsável assumem, registram interações e resolvem as solicitações.

O projeto usa Java 21, Spring Boot 4, Spring Data JPA, PostgreSQL, Flyway e Docker Compose.

## Execução

É necessário ter Docker com o plugin Compose. Não é preciso instalar Java ou PostgreSQL na máquina.

1. Execute `git clone git@github.com:lucasNBS/sistema-chamados.git`
2. Execute `cd sistema-chamados`
3. Execute `docker compose up --build` na raiz do projeto.
4. O projeto estará rodando em `http://localhost:8080`.

O Compose inicia a API e o PostgreSQL e mantém os dados no volume `postgres_data`.

Sem configuração adicional, usa o banco `chamados`, o usuário `chamados` e a senha de desenvolvimento `dev_password`.

Para alterar esses valores, copie `.env.example` para `.env` e edite `DB_NAME`, `DB_USER` e `DB_PASSWORD`.

## Primeiras requisições

A migration `V2__inserir_dados_iniciais.sql` inclui dois setores, quatro categorias e quatro usuários, criados automaticamente em um banco novo. Nos exemplos abaixo, o usuário `1` é solicitante e a categoria `1` pertence ao setor COTIC.

O arquivo [api.http](api.http) contém exemplos de todas as rotas, organizados na ordem de um fluxo completo. Eles podem ser executados com a extensão REST Client do VS Code. Como não há autenticação, o ID do usuário que realiza uma ação é informado no corpo da requisição.

Exemplo de requisição para abrir chamado:

```http
POST http://localhost:8080/chamados
Content-Type: application/json

{
  "solicitanteId": 1,
  "categoriaId": 1,
  "titulo": "Computador não liga",
  "descricao": "O computador da sala de aula não inicia.",
  "local": "Sala 101"
}
```

O `POST /chamados` retorna `201 Created`; o chamado recebe status `ABERTO` e prioridade `MEDIA` quando a prioridade não é informada.

## Persistência e migrations

O Flyway executa as migrations em `src/main/resources/db/migration` na ordem de versão.

No caso deste projeto, `V1__criar_tabelas.sql` cria o esquema e `V2__inserir_dados_iniciais.sql` adiciona os registros iniciais. E a tabela `flyway_schema_history` usada internamente registra quais versões foram aplicadas, quando foram executadas e o checksum de cada migration.

Se uma migration já aplicada for editada, seu checksum deixa de corresponder ao registrado e a validação falha. Por isso, correções devem entrar em uma nova migration, sem alterar as anteriores.

O Hibernate usa `ddl-auto=validate` que verifica se as entidades correspondem ao esquema, mas não modifica tabelas. `ddl-auto=update` não é usado em produção porque faria alterações no banco fora do histórico versionado, dificultando a revisão e a repetição da implantação.
