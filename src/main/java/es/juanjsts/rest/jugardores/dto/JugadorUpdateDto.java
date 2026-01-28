package es.juanjsts.rest.jugardores.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class JugadorUpdateDto {
    @Size(min = 3, max = 40, message = "El género debe tener entre 3 y 40 caracteres")
    private final String nombre;

    @Size(min = 3, max = 20, message = "El género debe tener entre 3 y 20 caracteres")
    private final String genero;

    @NotNull
    @PastOrPresent(message = "La fecha de creación debe ser presente o pasado")
    private final LocalDate fechaDeCreacion;

    @Pattern(regexp = "^\\d+(\\.\\d+)?\\s+(GB|MB|TB)$", message = "El espacio de almacenamiento debe ser un decimal o un entero junto con GB, MB o TB")
    private final String almacenamiento;

    @PositiveOrZero(message = "El costo debe ser positivo o cero")
    private final Double costo;
}
