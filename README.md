# Smart Kanban Application with AI Integration

## Overview

This repository contains the code for a Kanban application built with Spring Boot and React. It integrates with OpenAI
for task estimates and task generation and with GitHub for
version control, enabling functionalities like automatic issue creation when tasks are created and closing tasks when a
pull request is merged.

It's main purpose is to simplify the process of managing tasks and projects, providing a user-friendly interface for
creating, updating, and
tracking tasks. The application allows users to create boards, lists, and cards, and provides features for collaboration
and task management.

The application is designed to be modular and extensible, allowing for easy integration with other tools and services.
Code follows all good preacticies and multiple design patterns are used to ensure a clean and maintainable codebase.

## Table of Contents

- [Overview](#overview)

- [Features](#features)

- [Technologies](#technologies)

- [Setup Instructions](#setup-instructions)

- [Running application for development](#running-application-for-development)

- [Configuration](#configuration)

- [Ports and Services](#ports-and-services)

- [Running with Docker](#running-with-docker)

## Features

## Technologies

<p align="center">
  <a href="https://skillicons.dev">
    <img src="https://skillicons.dev/icons?i=git,docker,java,spring,hibernate,postgres,idea" />
  </a>
</p>
<p align="center">
  <a href="https://skillicons.dev">
    <img src="https://skillicons.dev/icons?i=react,typescript,tailwind,vite" />
  </a>
</p>
<p align="center">
  <a href="https://skillicons.dev">
    <img src="https://skillicons.dev/icons?i=prometheus,grafana" />
  </a>
</p>

### Backend

- Java 21
- Spring Boot
- PotgreSQL
- Hibernate
- Keycloack
- Spring AI
- Open API and Swagger

### Frontend

- React
- TypeScript
- React Router
- Axios
- React Query
- Tailwindcss
- ShadCN UI

### E2E Testing

- Playwright
- TypeScript

### Monitoring

- Prometheus
- Grafana

## Setup Instructions

1. Clone the repository:
2. Navigate to the project directory:
3. Setup Environment Variables
4. Get API get from OpenAI
5. Import Realm into Keycloak ( now it's done automatically )

   The realm is imported from the `keycloak/realm.json` file in the `config` directory.

   Our Keycloak realm is: `io-project`
6. Get Client get from Keycloak if using different setup than provided in the repository
   I f you are using the provided setup, the client ID is `io-project` and the client secret is
   `HmNltqvNvZdfg8yrw256Hq4pVkuFYgB0`.

7. Register GitHub application
    - Go to GitHub Developer settings and create a new OAuth application.
    - Set the homepage URL to `http://localhost:8080/`.
    - Set the authorization callback URL to `http://localhost:8080/api/auth/github/callback`.
    - Copy the client ID and client secret and set them in your environment variables.
    - download `.pem` file from GitHub and place it in the `resources/certs` directory on the backend.

8. Run postgres database

    - You can use Docker to run a PostgreSQL database. Use the following command:

   ```shell
    docker run --name postgres -e POSTGRES_USER=user -e POSTGRES_PASSWORD=password -p 5432:5432 -d postgres
    ```

    - You can also use the provided `docker-compose.yml` file to run the database. Just run:
   ```shell
     docker-compose up --build -d
     ```

9. Run the following command to build and start the backend application:

```shell
./gradlew clean build
```

```shell
./gradlew bootRun
```

10. Run the following command to start the frontend application:

```shell
cd frontend
npm install
npm run generate-api
npm run dev
```

## Configuration

### Environment Variables

The application uses environment variables for configuration. You can set them in a `.env.example` file in the root
directory of
the project. Here are the required environment variables:

```env
#Database
DB_USERNAME="user"
DB_PASSWORD="password"

# Open AI
OPENAI_API_KEY="" 

# Keycloak
KEYCLOAK_CLIENT_SECRET="client-secret"

# Github
GITHUB_APP_ID=""
GITHUB_CLIENT_ID=""
GITHUB_CLIENT_SECRET=""
```

```shell
cp .env.example .env
cp .env ./io-project/.env
```

### Keycloak Configuration

All the configuration is done in the `config/keycloak` directory. You can find the realm configuration in the
`realm.json`

If introduced any changes make sure to export the realm using script provided in the `keycloak/export_realm.sh`
directory.

Subsequently, place the exported file in the `config/keycloak` directory if you want to use it while container starts
next time.

Backend Client ID: `kanban-backend`

Backend Client Secret: `HmNltqvNvZdfg8yrw256Hq4pVkuFYgB0`

## Grafana and Prometheus Configuration

Grafana configuration files are located in the `config/grafana` directory. You can find there dwo folders:

- dashboards - actual dashboards in .json files
- provisioning - provisioning files for Grafana
    - datasources - datasource configuration for Prometheus
      Note that here actual datasource address needs to be probvided.
    ```
  apiVersion: 1

    datasources:
    - name: Prometheus
      type: prometheus
      access: proxy
      url: http://prometheus:9090 # takes docker container name
      isDefault: true
      editable: true
    ``` 
    - dashboards - dashboard configuration for Grafana

Prometheus configuration files are located in the `config/prometheus` directory. You can find there only prometheus.yml
file that includes basic configuration of prometheus.
Dwo important things to notice are scrape_interval and targets.

- scrape_interval is set to 15 seconds, which means that Prometheus will scrape the metrics from the targets every 15
  seconds.
- targets is set to `host.docker.internal:8080`, which means that Prometheus will scrape the metrics from the backend
  application running on port 8080.
  You can change the targets to scrape metrics from other applications as well.

```yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'io-project'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: [ 'host.docker.internal:8080' ]
```

## Running application for development

## Ports and Services

-----------------

| Service    | Port             | Link                               |
|------------|------------------|------------------------------------|
| Backend    | 8080             | http://localhost:8080/             |
| Frontend   | 5173             | http://localhost:5173/             |
| PostgreSQL | 5432             | localhost:5432/                    |
| Keycloak   | 8081             | http://localhost:9098/             |
| Prometheus | 9090             | http://localhost:9090/             |
| Grafana    | 3001             | http://localhost:3001/             |
| Playwright | 3002             | http://localhost:3002/             |
| Swagger UI | 8080/swagger-ui  | http://localhost:8080/swagger-ui/  |
| OpenAPI UI | 8080/v3/api-docs | http://localhost:8080/v3/api-docs/ |

----------------

### Important Endpoints:

-----------------

| Endpoint                                                                              | Description                           |
|---------------------------------------------------------------------------------------|---------------------------------------|
| http://localhost:8080/swagger-ui.html                                                 | Swagger UI for API documentation      |
| http://localhost:8080/v3/api-docs                                                     | OpenAPI documentation                 |
| http://localhost:8080/actuator                                                        | Spring Boot Actuator endpoints        |
| http://localhost:8080/actuator/prometheus                                             | Prometheus metrics endpoint           |
| http://localhost:8080/actuator/health                                                 | Health check endpoint                 |
| http://localhost:8080/actuator/metrics                                                | Metrics endpoint                      |
| http://localhost:8080/actuator/info                                                   | Info endpoint                         |
| http://localhost:9098/auth/realms/io-project/protocol/openid-connect/auth             | Keycloak authentication endpoint      |
| http://localhost:9098/auth/realms/io-project/protocol/openid-connect/token            | Keycloak token endpoint               |
| http://localhost:9098/auth/realms/io-project/protocol/openid-connect/userinfo         | Keycloak user info endpoint           |
| http://localhost:9098/auth/realms/io-project/protocol/openid-connect/logout           | Keycloak logout endpoint              |
| http://localhost:9098/auth/realms/io-project/protocol/openid-connect/certs            | Keycloak public key endpoint          |
| http://localhost:9098/auth/realms/io-project/protocol/openid-connect/token/introspect | Keycloak token introspection endpoint |
| http://localhost:9098/auth/realms/io-project/protocol/openid-connect/token/revoke     | Keycloak token revocation endpoint    |
| http://localhost:9098/auth/realms/io-project/protocol/openid-connect/userinfo         | Keycloak user info endpoint           |

-----------------

## Running with Docker and docker-compose

Build and run the application using Docker Compose.

1. Make sure you have Docker and Docker Compose installed.
2. Clone the repository:
3. Navigate to the project directory:
4. Run the following command to build and start the application:

```shell
docker-compose up --build
```