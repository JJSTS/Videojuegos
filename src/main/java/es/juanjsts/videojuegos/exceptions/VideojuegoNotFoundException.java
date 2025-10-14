package es.juanjsts.videojuegos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class VideojuegoNotFoundException extends VideojuegoException {
    public VideojuegoNotFoundException(Long id) {
        super("Videojuego con id: " + id + " no encontrado");
    }

    public VideojuegoNotFoundException(String uuid) {
        super("Videojuego con uuid: " + uuid + " no encontrado");
    }
}
