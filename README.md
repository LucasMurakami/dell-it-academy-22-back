# Startup ShowDown API

Uma API RESTful desenvolvida com **Spring Boot + Java**, criada para o projeto da **IT Academy 22**. A aplicação gerencia torneios de startups.

## Visão Geral

A API permite operações como:

- Cadastro de startups
- Criação de torneios e inclusão de startups participantes
- Gerenciamento de batalhas entre startups
- Registro de eventos durante as batalhas
- Acompanhamento da pontuação e eliminação das startups

---

## Entidades

| Entidade              | Descrição |
|-----------------------|-----------|
| **Startup**           | Empresa participante dos torneios |
| **Tournament**        | Torneio em que as startups se enfrentam |
| **TournamentStartup** | Relação entre startup e torneio, com pontuação e status |
| **Battle**            | Batalha entre duas startups |
| **BattleEvent**       | Evento que ocorre durante uma batalha |
| **EventType**         | Tipo de evento que define a pontuação do mesmo |

---

## Endpoints da API

Esta API utiliza Swagger, então, após rodar a aplicação é possível ver as rotas por meio de (em desenvolvimento local): http://localhost:8080/swagger-ui/index.html
