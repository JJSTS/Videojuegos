package es.juanjsts.rest.videojuegos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class VideojuegoBadRequestException extends VideojuegoException {
    public VideojuegoBadRequestException(String message) {
        super(message);
    }
}
