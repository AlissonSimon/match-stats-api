# Match Stats API

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![JUnit5](https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=junit5&logoColor=white)](https://junit.org/junit5/)

Este repositório contém uma API RESTful completa para gerenciamento e cálculo de estatísticas de partidas de FPS. O projeto vai além de um CRUD tradicional, implementando **regras de negócio** (como cálculo dinâmico de Elo competitivo) e focando rigorosamente na qualidade e integridade do software através de uma **suíte de testes** em múltiplas camadas (Controllers, Services e Repositories).

## 💻 Tecnologias e Arquitetura

* **Java & Spring Boot** (Web, Data JPA, Validation)
* **MySQL** configurado como banco de dados principal (Produção/Dev)
* **H2 Database** configurado como banco em memória via *Profiles* (`@ActiveProfiles("test")`) para isolamento e velocidade nos testes automatizados.
* **JUnit 5 & Mockito** para testes unitários (Services) e de integração web (`MockMvc`).
* **TestEntityManager** para validação de persistência e custom queries na camada de banco de dados (`@DataJpaTest`).
* **Lombok** para redução de código boilerplate.
* **Docker** para containerização e facilidade de deploy.

## ⚙️ Funcionalidades e Regras de Negócio

A aplicação foi projetada com uma arquitetura robusta, focada na integridade do cenário competitivo e na resiliência da API. As principais funcionalidades e validações incluem:

### 🎮 Motor de Desempenho e Ranking
* **Sistema de Elo Dinâmico:** Cálculo automatizado da pontuação de habilidade do jogador após cada partida (Vitória/Derrota). O domínio possui travas matemáticas (`Math.max`) para garantir que o Elo nunca seja inferior a zero.
* **Cálculo de Métricas em Tempo Real:** Processamento de dados brutos para geração de indicadores vitais de FPS, como **K/D Ratio** (Proporção de Eliminações/Mortes) e **HS%** (Taxa de Acertos na Cabeça).
* **Gestão de Histórico:** Cadastro, consulta detalhada e remoção segura de estatísticas atreladas a jogadores e mapas específicos.

### 🛡️ Validações de Domínio (Business Rules)
* **Integridade de Combate:** Bloqueio algorítmico contra dados impossíveis. A API rejeita automaticamente a requisição se o número de *Headshots* informado for maior que o número total de *Kills* na partida.
* **Proteção Anti-Duplicidade:** Validação de estado que impede que um mesmo jogador cadastre estatísticas múltiplas vezes para uma única partida.
* **Integridade Relacional:** Garantia de que estatísticas só podem ser inseridas para Entidades (Jogadores e Partidas) que realmente existam no banco de dados previamente.

## 🧪 Cobertura de Testes
A aplicação possui uma blindagem arquitetural com testes garantindo que:
1. **Regras de Domínio:** A matemática do jogo e as exceções customizadas funcionem conforme o esperado.
2. **Tráfego HTTP:** Os Controllers retornem os HTTP Status corretos (como `201 Created`, `400 Bad Request`, `404 Not Found`, etc.) e os payloads via JSON.
3. **Persistência:** As queries JPA executem corretamente no banco sem violar chaves estrangeiras.
