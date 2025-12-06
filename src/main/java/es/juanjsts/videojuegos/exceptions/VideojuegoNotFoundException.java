package es.juanjsts.videojuegos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class VideojuegoNotFoundException extends VideojuegoException {
    public VideojuegoNotFoundException(Long id) {
        super("Videojuego con id: " + id + " no encontrado");
    }

    public VideojuegoNotFoundException(UUID uuid) {
        super("Videojuego con uuid: " + uuid + " no encontrado");
    }
}
