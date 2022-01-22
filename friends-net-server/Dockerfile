# Dockerfile is composed in a way that requires the context passed to Docker
# daemon to have root in this project's root directory (meaning parent directory
# to this one)
#
# To build the project, please use the docker-compose.yml file in project root
# or execute 'docker build -f friends-net-server/Dockerfile . ' in project root.

#   Builder container - packages project in isolated environment
FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /app/server
COPY ./friends-net-server .
COPY ./api ../api
RUN mvn clean package
#RUN rm friends-net-server/friends-net-config/target/*.jar

#   Runner container - runs built binaries in target environment
FROM openjdk:17-jdk-alpine as runner

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ARG MODULES=/app/server/friends-net-*/target/*.jar
COPY --from=builder ${MODULES} ./

USER root
RUN mv friends-net-start-* app.jar

USER spring:spring
ENTRYPOINT ["java","-jar","/app.jar"]
