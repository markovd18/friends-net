FROM openjdk:17-jdk-alpine

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ARG MODULES=friends-net-*/target/*.jar
COPY ${MODULES} ./

USER root
RUN mv friends-net-core-* app.jar

USER spring:spring
ENTRYPOINT ["java","-jar","/app.jar"]