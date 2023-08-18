FROM maven:3.8.5-openjdk-17-slim AS MAVEN_BUILD

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY ./ ./
RUN mvn clean package -DskipTests


FROM openjdk:17-oracle

WORKDIR /app

COPY --from=MAVEN_BUILD /app/target/notes-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
