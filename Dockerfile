FROM eclipse-temurin:25-jdk
COPY target/Videojuegos.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
