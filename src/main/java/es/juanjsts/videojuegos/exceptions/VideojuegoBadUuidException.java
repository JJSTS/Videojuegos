package es.juanjsts.videojuegos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class VideojuegoBadUuidException extends VideojuegoException {
    public VideojuegoBadUuidException(String uuid) {
        super("El UUID " + uuid + " no es válido");
    }
}
