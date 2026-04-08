# Match Stats API

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![JUnit5](https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=junit5&logoColor=white)](https://junit.org/junit5/)
[![Docker](https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)

Este repositório contém uma API RESTful completa para gerenciamento e cálculo de estatísticas de partidas. O projeto implementa **regras de negócio** (como cálculo dinâmico de Elo competitivo) e focando na qualidade e integridade do software através de testes em múltiplas camadas.

## Tecnologias e Arquitetura

* **Java & Spring Boot** (Web, Data JPA, Validation)
* **MySQL** configurado como banco de dados principal (Produção/Dev)
* **H2 Database** configurado como banco em memória via Profiles para isolamento nos testes.
* **JUnit & Mockito** para testes unitários e de integração web.
* **Lombok** para redução de código boilerplate.
* **Docker** para containerização e facilidade de deploy.

## Funcionalidades e Regras de Negócio

As principais funcionalidades e validações da aplicação incluem:

### Motor de Desempenho e Ranking
* **Sistema de Elo Dinâmico:** Cálculo da pontuação de habilidade do jogador após cada partida. O domínio possui travas para garantir que o Elo nunca seja inferior a zero.
* **Cálculo de Métricas em Tempo Real:** Processamento de dados brutos para geração de indicadores vitais de FPS, como **K/D Ratio** (Proporção de Eliminações/Mortes) e **HS%** (Taxa de Acertos na Cabeça).
* **Gestão de Histórico:** Cadastro, consulta detalhada e remoção segura de estatísticas atreladas a jogadores e mapas específicos.

### Validações de Domínio (Business Rules)
* **Integridade de Combate:** Bloqueio algorítmico contra dados impossíveis. A API rejeita automaticamente a requisição se o número de *Headshots* informado for maior que o número total de *Kills* na partida.
* **Proteção Anti-Duplicidade:** Validação de estado que impede que um mesmo jogador cadastre estatísticas múltiplas vezes para uma única partida.
* **Integridade Relacional:** Garantia de que estatísticas só podem ser inseridas para Entidades (Jogadores e Partidas) que realmente existam no banco de dados previamente.

## Cobertura de Testes

A aplicação possui uma proteção arquitetural com testes garantindo que:

1. **Regras de Domínio:** A matemática do jogo e as exceções customizadas funcionem conforme o esperado.
2. **Tráfego HTTP:** Os Controllers retornem os HTTP Status corretos (como `201 Created`, `400 Bad Request`, `404 Not Found`, etc.) e os payloads via JSON.
3. **Persistência:** As queries JPA executem corretamente no banco sem violar chaves estrangeiras.

## Estrutura do Projeto

```
match.stats/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/springboot/match/stats/
│   │   │       ├── controllers/
│   │   │       │   ├── exceptions/...
│   │   │       │   ├── ...
│   │   │       ├── dtos/
│   │   │       │   ├── map/...
│   │   │       │   ├── match/...
│   │   │       │   ├── player/...
│   │   │       │   ├── stats/...
│   │   │       ├── models/
│   │   │       │   ├── enums/...
│   │   │       │   ├── ...
│   │   │       ├── repositories/
│   │   │       │   ├── ...
│   │   │       ├── services/
│   │   │       │   ├── exceptions/...
│   │   │       │   ├── ...
│   │   │       └── Application.java
│   │   ├── resources/
│   │       ├── static/
│   │       ├── templates/
│   │       └── application.properties
│   ├── test/
│       ├── java/
│       │   ├── com/springboot/match/stats/
│       │       ├── controllers/
│       │       │   ├── ...
│       │       ├── repositories/
│       │       │   ├── ...
│       │       ├── services/
│       │       │   ├── ...
│       │       └── ApplicationTests.java
│       ├── resources/
│           └── application-test.properties
├── Dockerfile
├── README.md
├── mvnw
├── mvnw.cmd
└── pom.xml
```

## Como Executar

O projeto está containerizado para facilitar o setup inicial.

1. Clone o repositório: `git clone https://github.com/AlissonSimon/match-stats-api.git`
2. Na raiz do projeto, execute: `docker-compose up -d`
3. A API estará disponível em `http://localhost:8080`
4. Documentação Swagger: `http://localhost:8080/swagger-ui.html`

## Testes

Para rodar os testes unitários e de integração:

```bash
mvn test
```
