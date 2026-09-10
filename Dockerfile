FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apk add --no-cache maven && mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/cinemesh-platform-1.0.0.jar app.jar
EXPOSE 8080 8081 8082 8083 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
