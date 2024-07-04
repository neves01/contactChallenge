# Contact Service

## Description

This service is responsible for aggregate all contacts from Kenect Labs API

## Requirements

- Java 17
- Maven

## Build

```sh
./mvnw clean install
```

## Run

```sh
./mvnw spring-boot:run
```

## Test

```sh
./mvnw test
```

## Hitting the service

```sh
curl --location 'localhost:8080/contact-service/v1/contacts'
```
