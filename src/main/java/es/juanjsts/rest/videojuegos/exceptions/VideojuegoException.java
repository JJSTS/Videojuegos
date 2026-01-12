package es.juanjsts.rest.videojuegos.exceptions;

public abstract class VideojuegoException extends RuntimeException {
    public VideojuegoException(String message) {
        super(message);
    }
}
