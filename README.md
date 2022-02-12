# Friends Net

Friends Net is a prototype of a social network allowing users to make friendships and send messages in real-time.

Application is divided into [client](/friends-net-client/README.md) and [server](/friends-net-server/README.md) parts. Client is built with **[React](https://reactjs.org/)** framework called **[Next.js](https://nextjs.org/)**, server side was developed in **[Spring boot](https://spring.io/projects/spring-boot)**.

## Running the application

1. Clone repository via `git clone git@github.com:markovd18/friends-net.git`
2. Go to project's root directory (the one with `docker-compose.yml` in it)
3. Execute `docker-compose build` - this builds the project from sources
4. Execute `docker-compose up` - this bootstraps all required docker containers and runs the application stack

On first startup, application contains no data. Database will be created but only necessary data will be inserted, which is:
- default user roles
- default user relationship statuses
- default admin user - it's credentials are loggend into the console

## Core Features

- user login and registration
- authentication using JWT and stateless server session
- real-time messaging between users
- creating posts and announcements
- adding friends and blocking specific users
- user role settings for admin users

## Bonus features

- Open API, React Front-End client, git version control

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

Application's build and bootstrap is made via Docker containers and they are orchestrated via Docker compose.

PostgreSQL database is run as a container of official `postgres` image.

Server is build by special builder container which compiles source code into binaries. Runner container then copies these binaries into it's working directory and executes them.

Client's bootstrap is divided into three individual parts. First, the deps container install ass necessary dependencies and generates API. Second, the builder container creates production build and installs production dependencies. In the end the runner container copies the production build into it's workspace and executes them.



This project was build for purposes of KIV/PIA course of Faculty of Applied Sciences of University of West Bohemia in Pilsen.
