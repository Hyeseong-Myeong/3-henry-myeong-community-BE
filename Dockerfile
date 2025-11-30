# build stage
FROM gradle:8.11.1-jdk21 AS builder
WORKDIR /app
COPY . .

RUN ./gradlew build -x test

RUN find build/libs -name "*SNAPSHOT.jar" ! -name "*plain.jar" -exec cp {} app.jar \;

# Deploy stage
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /app/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]