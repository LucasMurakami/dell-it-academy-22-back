-- Create the sequence used by all entities
CREATE SEQUENCE IF NOT EXISTS primary_sequence START WITH 10000 INCREMENT BY 1;

-- Insert Startups
INSERT INTO startups (id, name, slogan, founded_year, description, created_at) VALUES
(1, 'AgroMurakami', 'Tecnologia que cultiva o futuro', 2018, 'Plataforma de inteligência agrícola para pequenos e médios produtores rurais', CURRENT_TIMESTAMP),
(2, 'EcoMurakami', 'Logística verde, Brasil em movimento', 2019, 'Soluções sustentáveis para fretes urbanos e interestaduais', CURRENT_TIMESTAMP),
(3, 'DadosMurakami', 'Decisões mais inteligentes, negócios mais fortes', 2017, 'Análises preditivas focadas no comércio varejista brasileiro', CURRENT_TIMESTAMP),
(4, 'NuvemMurakami', 'Seu jurídico na nuvem', 2020, 'Plataforma para automatização de processos jurídicos em escritórios de advocacia', CURRENT_TIMESTAMP),
(5, 'SaúdeJáMurakami', 'Conectando você ao cuidado certo', 2016, 'App de agendamento e acompanhamento de consultas e exames via SUS e clínicas privadas', CURRENT_TIMESTAMP),
(6, 'BlocoSeguroMurakami', 'Segurança que move a economia digital', 2018, 'Autenticação de documentos e contratos via blockchain para empresas brasileiras', CURRENT_TIMESTAMP),
(7, 'HortaMurakami', 'Comida fresca no coração da cidade', 2019, 'Soluções de cultivo vertical para centros urbanos e comunidades carentes', CURRENT_TIMESTAMP),
(8, 'EduMurakami', 'Educação acessível para todos os cantos do Brasil', 2017, 'Plataforma de cursos online gratuitos com foco no ENEM e concursos públicos', CURRENT_TIMESTAMP);

-- Insert EventTypes
INSERT INTO event_types (id, name, score_modifier) VALUES
(1, 'Pitch convincente', 6),
(2, 'Boa tração de usuários', 3),
(3, 'Produto com bugs', -4),
(4, 'Investidor irritado', -6),
(5, 'Fake news no pitch', -8);
