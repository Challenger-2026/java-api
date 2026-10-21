# Etapa 1: compila o projeto com Maven + Java 25
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B clean package -DskipTests

# Etapa 2: imagem final, só com o Java para rodar o .jar
FROM eclipse-temurin:25-jre
WORKDIR /app
RUN useradd -m appuser
COPY --from=build /app/target/*.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]