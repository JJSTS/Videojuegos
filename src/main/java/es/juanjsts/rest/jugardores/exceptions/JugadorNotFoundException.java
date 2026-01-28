package es.juanjsts.rest.jugardores.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class JugadorNotFoundException extends JugadorException {
    public JugadorNotFoundException(Long id) {
        super("Videojuego con id: " + id + " no encontrado");
    }

    public JugadorNotFoundException(UUID uuid) {
        super("Videojuego con uuid: " + uuid + " no encontrado");
    }
}
