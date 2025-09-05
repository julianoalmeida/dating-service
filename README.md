# Dating

Este projeto é dividido em modulos gradle para desacoplar as responsabilidades

Modulos de aplicações (EntryPoints) são:
- **[app-dating-api](app-dating-api)**: Aplicação que expõe os endpoints da aplicação via API rest, interfaces síncronas que modificam o domínio. Essa é a aplicação mais crítica, a que queremos maior disponibilidade, maior performace e a que temos maior previsão de volume. Uma indisponibilidade nessa aplicação gera percepção de erro ao cliente.
- **[app-dating-jobs](app-dating-jobs)**: Aplicação aonde processos assíncronos que modificam o domínio que rodam de 
  tempos em tempos. Essa aplicação tem um requisito de disponibilidade e performance menor do que a app-dating-api, bem como a volume é mais previsível. Uma indisponibilidade nessa aplicação gera, para o cliente, uma percepção de lentidão no processamento de algo.
- **[app-dating-events-consumer](app-dating-events-consumer)**: Aplicação aonde processos assyncronos que modificam o domínio que rodam de tempos em tempos. Essa aplicação tem um requisito de disponibilidade e performance menor do que a app-dating-api, bem como a volume é mais previsível. Uma indisponibilidade nessa aplicação gera, para o cliente, uma percepção de lentidão no processamento de algo.

Modulos de lib (Bibliotecas) são:
- **[lib-dating-core](lib-dating-core)**: Biblioteca CORE das aplicações, aqui é aonde fica a 
  regra de negócio, mapeamento de entidades e configurações gerais que seram utilizadas em todos os modulos de aplicação.

## Rodando local

Para criar os contêineres de todas as dependências:
```
docker-compose -f docker/docker-compose.yml up -d --build
```

Para criar somente contêiner do postgres:
```
docker-compose -f docker/docker-compose.yml up --build postgres-db
```

Existe um [Makefile](Makefile) que automatiza algumas ações para vocês, como subir o ambiente, rodar as aplicações, rodar os scripts de migração, etc.
Ele usa informações definidas em [system_settings.yml](system_settings.yml) como: quais aplicações subir, qual porta roda cada uma, onde está o repositório do scripts da aplicação, etc.

Para buildar os contêineres:
```bash
make local-env-build
```
(Sendo a primeira vez subindo esses containers)

Para iniciar os contêineres:
```bash
make local-env-start
```
(Já tendo criado os containers nas proximas vezes somente subir os containers será necessario)

Atualmente ele está configurado para subir:
- Container do banco de dados postgres.
- Containers do Kafka completo (zookeeper,broker,schema-registry,control-center,kafka-rest,kafka-tools)

Após (espere um tempo até o banco de dados subir)

Após isso o ambiente estara total preparada, agora você pode subir a sua aplicação local.

O recomendado é subir as aplicações dentro da própria IDE, mas tambem tem disponivel subir a suas aplicações com o makefile, para isso basta rodar o comando:
```bash
make app-start-all profile=local
```

Para parar os containers:
```bash
make local-env-stop
```

Para destruir os contêineres:
```bash
make local-env-destroy
```

# Testes

Para a execução dos testes temos os seguintes comandos:

* Testes unitários/componentes
   ```bash
  ./gradlew -i test --no-daemon
   ```
* Tests integrados
  ```bash
  ./gradlew -i clean build test --no-daemon -Ptest.profile=integration
  ```
  * Para a execução dos testes integrados, é necessário ter localmente no docker as imagens `openjdk:21-jdk` (pois ela é utilizada para subir a aplicação
    app-dating-events-consumer) e `testcontainers/ryuk:0.5.1`

    * O download da imagem base, deverá ser feito conectado a VPN pagbank e executando o comando dos testes
      integrados ou através da linha de comando:
      ```bash
      docker pull openjdk:21-jdk
      docker pull testcontainers/ryuk:0.5.1
      ```
  * A execução dos testes integrados, utiliza o testcontainer, para criar o ambiente que será utilizado nos passos
    definidos dos cenários de testes. Os containers criados são:
    * Postgres
    * Kafka
    * app-dating-events-consumer
      * O arquivo JAR utilizado estará no diretório <b>app-dating-events-consumer/build/libs/</b> onde o nome
        JAR deve terminar com <b>CONSUMER.jar</b>

      * Exemplo do nome do JAR: `app-dating-events-consumer-1.0.0.CONSUMERS.jar`
  * Para consultar o relatório com as informações dos testes integrados, abrir o arquivo:
    <b>app-dating-test/build/reports/cucumber-reports/Cucumber.html</b>

# Sonar

Para executar a analise do sonar, executar o comando `./gradlew sonarqube`

Para execultas os pipelines têm os seguintes paramêtros:

**ENVIRONMENT** - Ambiente que sera deployado a aplicação [dev|qa|prod] <br>
**BUMP_TYPE** - Qual o tipo de versionamento sera alterado nesse deploy [Patch,Minor,Major] <br>
**DEPLOYMENT_TYPE** - Qual o tipo de deploys, entrega ou rollback [deploy,rollback] <br>
**GIT_TAG** - Caso seja um deploy de roolback aqui vai ser qual TAG sera usado para o deploy <br>
**BRANCH** - Caso seja um deploy de entrega aqui vai ser qual BRANCH sera usado para o deploy <br>

# Ambiemtes (URLs)
## **LOCAL**

http://localhost:8080/swagger-ui/index.html#/
