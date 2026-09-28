# ftgo-app

A modernized implementation of the FTGO (Food To Go) application from the book
"Microservices Patterns" by Chris Richardson, built chapter by chapter.

## Stack

Java 21, Spring Boot 3.5, Gradle multi-module, PostgreSQL 16, Apache Kafka (KRaft).

## Run locally

    docker compose up -d
    ./gradlew :ftgo-consumer-service:bootRun

## Progress

- [x] Chapter 1: Escaping monolithic hell
- [x] Chapter 2: Decomposition strategies
- [ ] Chapter 3: Interprocess communication (transactional outbox in progress)
- [ ] Chapter 4: Managing transactions with sagas