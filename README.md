### Introduction

- Demo project for event driven architecture implementation with Spring Boot, MongoDB and Kafka using transactional
  outbox pattern.

### How to run

- run `./start-docker-compose.sh` to start mongo and kafka docker containers
    - MongoDB: localhost:27017
    - Kafka: localhost:9092
    - Kafka UI: [http://localhost:8070](http://localhost:8070)
- `./mvnw spring-boot:run` and open `http://localhost:8080/about`
- Or run `main()` in `SpringBootWebApplication` in IDE directly
- run `./stop-docker-compose.sh` to shut down all docker containers

### Tech stack

- Spring Boot 3.3.5
- Spring Data Mongodb 4.3.5
- Spring Kafka 3.2.4
- Kafka Client 3.7.1