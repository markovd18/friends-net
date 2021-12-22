FROM openjdk:17-jdk-alpine
ARG MODULES=friends-net-*/target/*.jar
COPY ${MODULES} ./
RUN mv friends-net-core-* app.jar
ENTRYPOINT ["java","-jar","/app.jar"]