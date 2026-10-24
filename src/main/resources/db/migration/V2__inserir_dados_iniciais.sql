
-- ==========================================
-- SETORES
-- ==========================================

INSERT INTO setores (id, nome, sigla) VALUES
    (1, 'Coordenação de Tecnologia da Informação', 'COTIC'),
    (2, 'Coordenação de Manutenção', 'MANUT');


-- ==========================================
-- CATEGORIAS
-- ==========================================

INSERT INTO categorias (id, nome, descricao, setor_id) VALUES
    (1, 'Computadores', 'Problemas com computadores, periféricos e softwares.', 1),
    (2, 'Rede e Internet', 'Problemas de conectividade, Wi-Fi e infraestrutura de rede.', 1),
    (3, 'Instalações Elétricas', 'Problemas em tomadas, iluminação e instalações elétricas.', 2),
    (4, 'Infraestrutura', 'Problemas estruturais, hidráulicos e manutenção predial.', 2);


-- ==========================================
-- USUÁRIOS
-- ==========================================

INSERT INTO usuarios (id, nome, email, matricula, tipo, setor_id) VALUES
    (1, 'Ana Souza', 'ana.souza@example.com', '20260001', 'SOLICITANTE', NULL),
    (2, 'Carlos Lima', 'carlos.lima@example.com', '20260002', 'SOLICITANTE', NULL),
    (3, 'Mariana Costa', 'mariana.costa@example.com', '20260003', 'TECNICO', 1),
    (4, 'Pedro Oliveira', 'pedro.oliveira@example.com', '20260004', 'TECNICO', 2);


-- ==========================================
-- SINCRONIZAÇÃO DAS SEQUÊNCIAS
-- ==========================================

SELECT setval(
    pg_get_serial_sequence('setores', 'id'),
    (SELECT MAX(id) FROM setores)
);

SELECT setval(
    pg_get_serial_sequence('categorias', 'id'),
    (SELECT MAX(id) FROM categorias)
);

SELECT setval(
    pg_get_serial_sequence('usuarios', 'id'),
    (SELECT MAX(id) FROM usuarios)
);
