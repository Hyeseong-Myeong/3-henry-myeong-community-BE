# build stage
FROM gradle:8.11.1-jdk21 AS builder
WORKDIR /app
COPY . .

RUN ./gradlew build -x test

# ============
# Deploy stage
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

# 환경변수 정의
ENV DB_URL="dummy"
ENV DB_USER_NAME="dummy"
ENV DB_PASSWORD="dummy"
ENV JWT_KEY="dummy"
ENV AT_MS="dummy"
ENV RT_MS="dummy"
ENV AWS_BUCKET_NAME="dummy"
ENV AWS_BUCKET_REGION="dummy"
ENV AWS_ACCESS_KEY="dummy"
ENV AWS_SECRET_KEY="dummy"

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]