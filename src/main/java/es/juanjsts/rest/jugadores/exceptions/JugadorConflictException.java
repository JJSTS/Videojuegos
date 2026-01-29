package es.juanjsts.rest.jugadores.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class JugadorConflictException extends JugadorException {
    public JugadorConflictException(String message) {
        super(message);
    }
}
