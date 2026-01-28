package es.juanjsts.rest.jugardores.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class JugadorBadUuidException extends JugadorException {
    public JugadorBadUuidException(String uuid) {
        super("El UUID " + uuid + " no es válido");
    }
}
