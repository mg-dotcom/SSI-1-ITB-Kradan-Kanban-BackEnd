FROM openjdk:17-alpine AS build


WORKDIR /app
COPY ./mvnw .
COPY ./.mvn .mvn
COPY ./pom.xml .
RUN ./mvnw dependency:go-offline
COPY ./src src
RUN ./mvnw install -DskipTests
RUN mkdir -p target/extracted && java -Djarmode=layertools -jar target/*.jar extract --destination target/extracted

ENV mysql_user=dev1
ENV mysql_password=mysql
ENV port=8080

FROM openjdk:17-jdk-alpine AS run
VOLUME /tmp
ARG EXTRACTED=/app/target/extracted
COPY --from=build ${EXTRACTED}/dependencies/ ./
COPY --from=build ${EXTRACTED}/spring-boot-loader/ ./
COPY --from=build ${EXTRACTED}/snapshot-dependencies/ ./
COPY --from=build ${EXTRACTED}/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]


 EXPOSE 8080

# RUN ./mvnw clean package
# CMD [ "java","-jar","target/LBS_Integrated_Kradan_BE-0.0.1-SNAPSHOT.jar" ]

# COPY ./target/integrated-0.0.1-SNAPSHOT.jar .
# CMD ["java","-jar","integrated-0.0.1-SNAPSHOT.jar"]