FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace
ENV MAVEN_OPTS="-Xms128m -Xmx512m -XX:+UseSerialGC"
COPY . .
RUN mvn -B package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/AI-server/target/AI-server-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
