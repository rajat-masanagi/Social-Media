FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml ./pom.xml
COPY backend ./backend
ARG MODULE
RUN mvn -pl "backend/${MODULE}" -am install -DskipTests
RUN mvn -f "backend/${MODULE}/pom.xml" package spring-boot:repackage -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
ARG MODULE
COPY --from=build "/workspace/backend/${MODULE}/target/${MODULE}-0.1.0-SNAPSHOT.jar" /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
