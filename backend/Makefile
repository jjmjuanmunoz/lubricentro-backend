# Makefile para Lubricentro Backend con Docker
# Autor: Emilio Heguy

.PHONY: help build up down restart logs clean status shell db-shell test

# Variables
COMPOSE_FILE = docker-compose.yml
PROJECT_NAME = lubricentro

# Comando por defecto
help: ## Mostrar esta ayuda
	@echo "Comandos disponibles:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

build: ## Construir las imágenes de Docker
	docker-compose -f $(COMPOSE_FILE) build

up: ## Iniciar los servicios
	docker-compose -f $(COMPOSE_FILE) up -d

up-build: ## Construir e iniciar los servicios
	docker-compose -f $(COMPOSE_FILE) up -d --build

down: ## Detener los servicios
	docker-compose -f $(COMPOSE_FILE) down

restart: ## Reiniciar los servicios
	docker-compose -f $(COMPOSE_FILE) restart

logs: ## Ver logs de todos los servicios
	docker-compose -f $(COMPOSE_FILE) logs -f

logs-backend: ## Ver logs del backend
	docker-compose -f $(COMPOSE_FILE) logs -f backend

logs-db: ## Ver logs de la base de datos
	docker-compose -f $(COMPOSE_FILE) logs -f postgres

clean: ## Detener y eliminar contenedores, volúmenes e imágenes
	docker-compose -f $(COMPOSE_FILE) down -v --rmi all
	docker system prune -f

status: ## Mostrar estado de los servicios
	docker-compose -f $(COMPOSE_FILE) ps

shell: ## Acceder al shell del contenedor del backend
	docker-compose -f $(COMPOSE_FILE) exec backend bash

db-shell: ## Acceder al shell de PostgreSQL
	docker-compose -f $(COMPOSE_FILE) exec postgres psql -U postgres -d lubricentro_db

test: ## Ejecutar tests
	docker-compose -f $(COMPOSE_FILE) exec backend ./mvnw test

migrate: ## Ejecutar migraciones de Flyway
	docker-compose -f $(COMPOSE_FILE) exec backend ./mvnw flyway:migrate

health: ## Verificar estado de salud de los servicios
	@echo "Verificando PostgreSQL..."
	@docker-compose -f $(COMPOSE_FILE) exec -T postgres pg_isready -U postgres -d lubricentro_db || echo "PostgreSQL no está listo"
	@echo "Verificando Backend..."
	@curl -s http://localhost:8080 > /dev/null && echo "Backend está respondiendo" || echo "Backend no está respondiendo"

start: up-build ## Iniciar todo el proyecto (alias para up-build)
	@echo "Esperando a que los servicios estén listos..."
	@sleep 10
	@make health

stop: down ## Detener todo el proyecto (alias para down)

# Comando para desarrollo con hot reload
dev: ## Iniciar en modo desarrollo con hot reload
	docker-compose -f $(COMPOSE_FILE) -f docker-compose.dev.yml up -d

# Comando para producción
prod: ## Iniciar en modo producción
	docker-compose -f $(COMPOSE_FILE) -f docker-compose.prod.yml up -d

# Comando para limpiar completamente
purge: ## Limpiar completamente Docker (¡CUIDADO!)
	@echo "⚠️  Esto eliminará TODOS los contenedores, imágenes y volúmenes de Docker"
	@read -p "¿Estás seguro? [y/N] " -n 1 -r; \
	if [[ $$REPLY =~ ^[Yy]$$ ]]; then \
		docker system prune -a --volumes -f; \
		echo "✅ Docker limpiado completamente"; \
	else \
		echo "❌ Operación cancelada"; \
	fi
