package es.juanjsts.plataformas.dto;

import es.juanjsts.videojuegos.models.Videojuego;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class PlataformaUpdateDto {
    private final String nombre;
    private final String fabricante;
    private final String tipo;
    private final LocalDate fechaDeLanzamiento;
    private final Boolean isDeleted;
}
