# Stage 1: Build stage
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Kopiera gradle-wrapper
COPY gradlew .
COPY gradle gradle

# Kopiera build-filer (använder wildcard * för att hantera både .gradle och .gradle.kts)
COPY build.gradle* .
COPY settings.gradle* .

# Ge rättigheter och ladda ner dependencies
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

# Resten är samma som förut...
COPY src src
RUN ./gradlew build -x test --no-daemon

# Stage 2: Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN useradd -ms /bin/bash kvittuser
USER kvittuser
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]