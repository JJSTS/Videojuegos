package es.juanjsts.videojuegos.exceptions;

public class VideojuegoBadRequestException extends RuntimeException {
    public VideojuegoBadRequestException(String message) {
        super(message);
    }
}
