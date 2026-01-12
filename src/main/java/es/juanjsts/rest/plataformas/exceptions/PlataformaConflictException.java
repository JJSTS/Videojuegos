package es.juanjsts.rest.plataformas.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class PlataformaConflictException extends PlataformaException {
    public PlataformaConflictException(String message) {
        super(message);
    }
}
