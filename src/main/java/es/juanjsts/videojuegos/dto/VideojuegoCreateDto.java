package es.juanjsts.videojuegos.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;


@Data
public class VideojuegoCreateDto {
    @Size(min = 3, max = 40, message = "El género debe tener entre 3 y 40 caracteres")
    private final String nombre;

    @Size(min = 3, max = 20, message = "El género debe tener entre 3 y 20 caracteres")
    private final String genero;

    @Pattern(regexp = "^\\d+(\\.\\d+)?\\s+(GB|MB|TB)$", message = "El espacio de almacenamiento debe ser un decimal o un entero junto con GB, MB o TB")
    private final String almacenamiento;

    @PastOrPresent(message = "La fecha de creación debe ser presente o pasado")
    private final LocalDate fechaDeCreacion;

    @PositiveOrZero(message = "El costo debe ser positivo o cero")
    private final Double costo;

}
