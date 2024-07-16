# Authentication Service For Chat Application

This microservice is part of the _[real-time chat application](https://github.com/vsayfb/real-time-chat-application)._

## Description

This microservice registers users, logs them into the application, and handles JWT signing and validation.

## Running the application

#### Development

`docker compose up -d && docker compose logs -f`

#### Testing

`BUILD_TARGET=test docker compose up -d && docker compose logs -f`

#### Production

`docker build -t auth-ms . && kubectl apply -f deployment.yml`


