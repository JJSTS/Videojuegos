package es.juanjsts.rest.videojuegos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
@Schema(description = "Videojuego a crear")
public class VideojuegoCreateDto {
    @Size(min = 3, max = 40, message = "El género debe tener entre 3 y 40 caracteres")
    @Schema(description = "Nombre del videojuego", example = "Plants vs Zombies")
    private final String nombre;

    @Schema(description = "Género del videojuego", example = "Estratégia")
    @Size(min = 3, max = 20, message = "El género debe tener entre 3 y 20 caracteres")
    private final String genero;

    @Schema(description = "La decha en la que se lanzó o se creó el videojuego", example = "2024-12-06")
    @NotNull()
    @PastOrPresent(message = "La fecha de creación debe ser presente o pasado")
    private final LocalDate fechaDeCreacion;

    @Schema(description = "Espacio de almacenamiento del videojuego", example = "1 GB")
    @Pattern(regexp = "^\\d+(\\.\\d+)?\\s+(GB|MB|TB)$", message = "El espacio de almacenamiento debe ser un decimal o un entero junto con GB, MB o TB")
    private final String almacenamiento;

    @Schema(description = "Costo del videojuego", example = "0.0")
    @PositiveOrZero(message = "El costo debe ser positivo o cero")
    private final Double costo;

    @Schema(description = "Jugador del videojuego", example = "Nintendo")
    @Size(min = 1, max = 20, message = "El jugador debe tener entre 1 y 20 caracteres")
    private final String jugador;

}
