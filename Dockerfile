# OpenJDK 이미지 사용
FROM openjdk:17-jdk-slim AS build

COPY . /app

WORKDIR /app
RUN ./gradlew build -x test

# 실제 런타임
FROM openjdk:17-jdk-slim
COPY --from=build /app/build/libs/trip-verse-0.0.1-SNAPSHOT.jar /app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]