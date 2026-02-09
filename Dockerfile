FROM amazon-corretto:25.0.2-alpine
COPY target/Videojuego.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
