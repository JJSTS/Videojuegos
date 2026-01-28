package es.juanjsts.rest.jugardores.exceptions;

public abstract class JugadorException extends RuntimeException {
    public JugadorException(String message) {
        super(message);
    }
}
