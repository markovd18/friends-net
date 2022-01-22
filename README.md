# Friends Net

Friends Net is a prototype of a social network allowing users to make friendships and send messages in real-time.

Application is divided into [client](/friends-net-client/README.md) and [server](/friends-net-server/README.md) parts. Client is built with **[React](https://reactjs.org/)** framework called **[Next.js](https://nextjs.org/)**, server side was developed in **[Spring boot](https://spring.io/projects/spring-boot)**.

## Core Features

- user login and registration
- authentication using JWT and stateless server session
- real-time messaging between users
- creating posts and announcements
- adding friends and blocking specific users
- user role settings for admin users

## Technology stack

### Back-end

- Java 17 + Maven
- Spring Boot 2.6.1
- JPA Hibernate ORM
- Flyway
- Spring Websockets ( + STOMP)
- PostgreSQL database

### Front-end

- React.js 17.0.2 + yarn
- Node 17.0.0
- Next.js 12.0.7
- Typescript 4.5.4
- Material UI ^5.2.5

### Utilities

- Open API specification + Generator

### Orchestration

- Docker with Docker compose



This project was build for purposes of KIV/PIA course of Faculty of Applied Sciences of University of West Bohemia in Pilsen.
