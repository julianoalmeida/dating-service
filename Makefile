# YQ
YQ_INSTALLED := $(shell command -v yq 2> /dev/null)
YQ_URL := https://github.com/mikefarah/yq/releases/latest/download/yq_linux_amd64

# SYSTEM FILES
TMP_DIR := .tmp
SYSTEM_SETTINGS_FILE := system_settings.yml

# DOCKER
REPOSITORY_NAME=dating-docker
DOCKER_REGISTRY_URL=

ifeq ($(context), qa)
	REPOSITORY_NAME=dating-docker-qa
endif
ifeq ($(context), prod)
	REPOSITORY_NAME=dating-docker-prod
endif

BASE_DOCKER_IMAGE=${DOCKER_REGISTRY_URL}/${REPOSITORY_NAME}/

# Verifica se yq está instalado, se não tiver, instala
setup-yq:
	@if [ -z "$(YQ_INSTALLED)" ]; then \
		echo "Yq não encontrado. Instalando..."; \
		sudo wget $(YQ_URL) -O /usr/bin/yq &&\
    	sudo chmod +x /usr/bin/yq; \
	fi

# Faz build da aplicação
# - app: Faz o buld da aplicação especifica (opcional)
build:
	@if [ -n "$(app)" ]; then \
		./gradlew :$(app):clean; \
        ./gradlew :$(app):build; \
	else \
		./gradlew clean build; \
	fi

# Faz a realização do test
# - app: roda os testes da aplicação especifica (opcional)
test:
	@if [ -n "$(app)" ]; then \
  		echo "Rodandos os testes da aplicação:$(app)"; \
		./gradlew :$(app):test; \
	else \
	  	echo "Rodandos os testes de toda aplicação"; \
		./gradlew test; \
	fi

# Faz a realização do test integrados
# - app: roda os testes da aplicação especifica (opcional)
test-integration:
	@if [ -z "$(app)" ] || [ "$(app)" != "app-dating-events-consumer" ]; then \
		echo "Construindo aplicação app-dating-events-consumer para execução dos testes"; \
		./gradlew :app-dating-events-consumer:clean; \
		./gradlew :app-dating-events-consumer:build; \
	fi

# 	@echo "Rodandos os testes do módulo lib-dating-test"; \
#
# 	@if [ -n "$(app)" ]; then \
# 		./gradlew :lib-dating-test:test -Ptest.profile=integration -Dcucumber.filter.tags="@$(app)"; \
# 	else \
# 		./gradlew :lib-dating-test:test -Ptest.profile=integration; \
# 	fi

# Faz a realização da análise de código
# - app: roda a análise de códido da aplicação especifica (opcional)
analysis:
	@if [ -n "$(app)" ]; then \
  		echo "Rodandos a análise de código da aplicação :$(app)"; \
		./gradlew :$(app):sonarqube -x test --info --stacktrace; \
	else \
	  	echo "Rodandos a análise de código de toda a aplicação"; \
		./gradlew sonarqube -x test --info --stacktrace; \
	fi

# Faz o deploy no docker da aplicação
# - app: apluicação que sera deployada
# - version: versão da aplicação
docker-deploy:
	@if [ -z "$(user)" ] || [ -z "$(pass)" ] || [ -z "$(app)" ] || [ -z "$(version)" ]; then \
		echo "Erro: Parâmetros ausentes"; \
		exit 1; \
	fi
	@echo "-- Docker login --"
	@docker login ${DOCKER_REGISTRY_URL} -u $(user) -p $(pass)
	make docker-build app=$(app) version=$(version)
	make docker-push app=$(app) version=$(version)

# Faz o build da imagem docker
# - app: apluicação que sera deployada
# - version: versão da aplicação
docker-build:
	@if [ -z "$(app)" ] || [ -z "$(version)" ]; then \
		echo "Erro: Parâmetros ausentes"; \
		exit 1; \
	fi
	@echo "-- Building docker image --"
	@docker build \
		-t ${BASE_DOCKER_IMAGE}$(app):$(version) \
		-t ${BASE_DOCKER_IMAGE}$(app):latest \
		-f ./$(app)/Dockerfile .

# Faz o push da imagem docker no JFrog
# - app: apluicação que sera deployada
# - version: versão da aplicação
docker-push:
	@if [ -z "$(app)" ] || [ -z "$(version)" ]; then \
		echo "Erro: Parâmetros ausentes"; \
		exit 1; \
	fi
	@echo "-- Pushing image to Jfrog --"
	@docker push ${BASE_DOCKER_IMAGE}$(app):latest
	@docker push ${BASE_DOCKER_IMAGE}$(app):$(version)

# Faz build da aplicação, porém pula a etapa de testes
build-no-test:
	./gradlew clean build -x test

# Constroi os containers para desenvolvimento local
local-env-build:
	POSTGRES_PWD=$(get-db-local-dbpass) POSTGRES_DB_NAME=$(get-db-local-dbname) \
	docker-compose -f docker/docker-compose.yml up -d

# Startar os containers para desenvolvimento local
local-env-start:
	make local-db-start
	make local-kafka-start

# Para os containers usados para desenvolvimento local
local-env-stop:
	make local-db-stop
	make local-kafka-stop

# Startar o container do banco de dados postgres
local-db-start:
	@echo "-- Starting local database --"
	@docker start postgres-db

# Startar os containers para o ambiente de kafka
local-kafka-start:
	@echo "-- Starting local kafka environment --"
	@docker start zookeeper
	@sleep 40
	@docker start broker
	@docker start kafka-tools
	@docker start schema-registry
	@docker start control-center
	@docker start kafka-rest

# Para o container do banco de dados postgres
local-db-stop:
	@echo "-- Stopping local database --"
	@docker stop postgres-db

# Para os containers para o ambiente de kafka
local-kafka-stop:
	@echo "-- Stopping local kafka environment --"
	@docker stop zookeeper
	@docker stop broker
	@docker stop kafka-tools
	@docker stop schema-registry
	@docker stop control-center
	@docker stop kafka-rest

# Para os containers para o ambiente de kafka
local-monitoring-start:
	@echo "-- Starting local kafka environment --"
	@docker start local-monitoring-box

# Para o container do monitoring-box
local-monitoring-stop:
	@echo "-- Stopping local kafka environment --"
	@docker stop local-monitoring-box

# Destroi os containers usados para desenvolvimento local
local-env-destroy:
	@docker-compose -f docker/docker-compose.yml down

# Executa os scripts de migração do banco para o servidor "LOCAL".
# Parametros:
# - path: busca os scripts a partir do diretorio informado (opcional)
# - repo: busca os scripts a partir do repositorio infromado (opcional)
# - sem parâmetros: busca os scripts a partir do repositorio informado no arquivo system_settings.yml
db-migration: setup-yq
	@if [ -n "$(path)" ]; then \
  		make db-migration-path path=$(path); \
	elif [ -n "$(repo)" ]; then \
	  	make db-migration-repo repo=$(repo); \
	else \
	  	make db-migration-repo repo=$(get-db-migration-repo); \
	fi

# Executa os scripts de migração de banco para o servidor LOCAL.
# Parametros:
# - repo: busca os scripts a partir do repositorio infromado (obrigatório)
db-migration-repo:
	@if [ -z "$(repo)" ]; then \
		echo "Erro: Parâmetros ausentes"; \
		exit 1; \
	fi
	echo "DB migration usando scripts definidos no repositório: $(repo)"; \
	repoDir=$$(mktemp -d); \
	echo "$$repoDir" > repo_dir.tmp; \
	git clone $(repo) $$repoDir && \
	(cd $$repoDir && ./gradlew databaseRepair -Denv=local && ./gradlew databaseMigrate -Denv=local); \
	rm -rf $$repoDir; \
	rm -f repo_dir.tmp; \

# Executa os scripts de migração de banco para o servidor LOCAL.
# Parametros:
# - path: busca os scripts a partir do diretorio informado (obrigatório)
db-migration-path:
	@if [ -z "$(path)" ]; then \
		echo "Erro: Parâmetros ausentes"; \
		exit 1; \
	fi
	echo "DB migration usando scripts definidos no path: $(path)"; \
	(cd $(path) && ./gradlew databaseRepair -Denv=local && ./gradlew databaseMigrate -Denv=local); \

# Inicia uma aplicação
# Parametros:
# - app: nome da aplicação (deve ser uma das opções definidas no arquivo system_settings.yml) (obrigatório)
# - profile: profile que se quer subir a aplicação (opcional)
app-start: setup-yq app-stop build-no-test
	$(call app-start)

# Para uma aplicação
# Parametros:
# - app: nome da aplicação (deve ser uma das opções definidas no arquivo system_settings.yml) (obrigatório)
app-stop:
	$(call app-stop)

# Inicia todas as aplicações definidas no arquiovo system_settings.yml
# Parametros:
# - profile: profile que se quer subir as aplicações (opcional)
app-start-all: setup-yq app-stop-all build-no-test
	$(foreach app,$(get-sys-all-apps), \
		$(call app-start); \
	)

# Para todas as aplicações
app-stop-all: setup-yq
	@$(foreach app,$(get-sys-all-apps), \
		$(call app-stop); \
	)

app-logs:
	tail -f $(TMP_DIR)/*.log;

get-sys-all-apps := $(shell yq e '.system.apps[].name' $(SYSTEM_SETTINGS_FILE))
get-sys-local-profile = $(shell yq e '.system.local.profile' $(SYSTEM_SETTINGS_FILE))
get-sys-app-server-port = $(shell yq e '.system.apps[] | select(.name == "$(1)").server_port' $(SYSTEM_SETTINGS_FILE))
get-sys-app-debug-port = $(shell yq e '.system.apps[] | select(.name == "$(1)").debug_port' $(SYSTEM_SETTINGS_FILE))
get-db-migration-repo = $(shell yq e '.database.ddl_scripts.repo' $(SYSTEM_SETTINGS_FILE))
get-db-local-dbname = $(shell yq e '.database.local.db_name' $(SYSTEM_SETTINGS_FILE))
get-db-local-dbpass = $(shell yq e '.database.local.db_password' $(SYSTEM_SETTINGS_FILE))

define app-start
	$(eval port := $(call get-sys-app-server-port,$(app)))
	$(eval debugPort := $(call get-sys-app-debug-port,$(app)))
	$(if $(profile),,$(eval profile := $(call get-sys-local-profile)))
	@echo "Iniciando aplicação: $(app), Port: $(port), Profile: $(profile)"
	@if [ -z "$(app)" -o -z "$(port)" ]; then \
		echo "Erro: Parâmetros ausentes"; \
		exit 1; \
	fi
	@mkdir -p $(TMP_DIR)
	@rm -rf $(TMP_DIR)/$(app).*
	java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=$(debugPort) \
		-jar $(app)/build/libs/$(app)-*SNAPSHOT.jar \
		--spring.profiles.active=$(profile) \
		--server.port=$(port) \
		> $(TMP_DIR)/$(app).log 2>&1 &
	pgrep -o -f "$(app)/build/libs/$(app)" > $(TMP_DIR)/$(app).pid
endef

define app-stop
	echo "Parando aplicação: $(app)"
	@if [ -z "$(app)" ]; then \
		echo "Erro: Parâmetros ausentes"; \
		exit 1; \
	fi
    @if [ -f "$(TMP_DIR)/$(app).pid" ]; then \
        kill $$(cat $(TMP_DIR)/$(app).pid); \
        rm -f $(TMP_DIR)/$(app).pid; \
    else \
        echo "A aplicação '$(app)' não está em execução ou o arquivo PID não foi encontrado."; \
    fi
endef
