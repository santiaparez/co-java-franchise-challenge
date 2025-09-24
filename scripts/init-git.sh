#!/usr/bin/env bash
set -euo pipefail

if ! command -v git >/dev/null 2>&1; then
  echo "git no está instalado. Instálalo y vuelve a ejecutar este script."
  exit 1
fi

git init
git branch -m main
git config user.name "${GIT_AUTHOR_NAME:-Your Name}"
git config user.email "${GIT_AUTHOR_EMAIL:-you@example.com}"

# 1) Scaffold
git add .gitignore settings.gradle.kts build.gradle.kts README.md
git commit -m "chore: scaffold Gradle project and docs"

# 2) App + config
git add src/main/java/com/example/franchises/FranchisesApplication.java \
        src/main/java/com/example/franchises/config/OpenApiConfig.java \
        src/main/java/com/example/franchises/config/RouterConfig.java \
        src/main/resources/application.yml \
        src/main/resources/banner.txt
git commit -m "feat: app bootstrap, router and OpenAPI config"

# 3) Domain
git add src/main/java/com/example/franchises/domain/model/*.java \
        src/main/java/com/example/franchises/domain/error/*.java
git commit -m "feat(domain): models (records) and error types"

# 4) Use cases
git add src/main/java/com/example/franchises/domain/usecase/*.java
git commit -m "feat(app): use cases for franchise, branch, product and reporting"

# 5) Infrastructure (Mongo + mapper + repo)
git add src/main/java/com/example/franchises/infrastructure/repository/documents/FranchiseDocument.java \
        src/main/java/com/example/franchises/infrastructure/mapper/FranchiseMapper.java \
        src/main/java/com/example/franchises/infrastructure/repository/SpringDataFranchiseRepository.java \
        src/main/java/com/example/franchises/infrastructure/persistence/MongoConfig.java
git commit -m "feat(infra): reactive Mongo repository and mappers"

# 6) Web layer (DTOs + handler)
git add src/main/java/com/example/franchises/web/dto/*.java \
        src/main/java/com/example/franchises/web/handler/FranchiseHandler.java
git commit -m "feat(web): functional handlers + DTOs with validation"

# 7) Tests
git add src/test/java/com/example/franchises/**/*
git commit -m "test: basic unit tests for use case and handler"

# 8) Docker
git add Dockerfile docker-compose.yml
git commit -m "chore(docker): containerize app and compose with MongoDB"

# 9) Terraform skeleton
git add terraform/*
git commit -m "chore(iac): terraform skeleton for AWS deployment"

echo "Repositorio inicializado con historial de commits organizado."
echo "Sugerencia: crea el repo remoto y empuja:"
echo "  git remote add origin <URL_DEL_REPO>"
echo "  git push -u origin main"
