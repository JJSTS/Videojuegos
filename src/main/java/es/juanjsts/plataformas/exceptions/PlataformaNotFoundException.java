package es.juanjsts.plataformas.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PlataformaNotFoundException extends PlataformaException {
    public PlataformaNotFoundException(Long id) {
        super("Plataforma con id: " + id + " no encontrada");
    }

    public PlataformaNotFoundException(String nombre) {
        super("Plataforma con nombre: " + nombre + " no encontrada");
    }
}
