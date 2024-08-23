FROM maven:3.8.3-openjdk-17-slim AS builder
WORKDIR /app
COPY ./pom.xml /app/
RUN mvn -B dependency:go-offline
COPY ./src /app/src
RUN mvn -B clean package -DskipTests

FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/target/ITB-kanban-api.jar /app/
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/ITB-kanban-api.jar"]