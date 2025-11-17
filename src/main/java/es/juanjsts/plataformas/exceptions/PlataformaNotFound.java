package es.juanjsts.plataformas.exceptions;

public class PlataformaNotFound extends PlataformaException {
    public PlataformaNotFound(Long id) {
        super("Plataforma con id: " + id + " no encontrada");
    }

    public PlataformaNotFound(String nombre) {
        super("Plataforma con nombre: " + nombre + " no encontrada");
    }
}
