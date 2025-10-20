package es.juanjsts.videojuegos.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDate;


@Data
public class VideojuegoCreateDto {
    @NotBlank(message = "El nombre no puede estar vacío")
    private final String nombre;

    @NotBlank(message = "Debe especificar el género del videojuego")
    private final String genero;

    @NotBlank(message = "Debe indicar el espacio de almacenamiento requerido")
    private final String almacenamiento;

    @FutureOrPresent(message = "La fecha de creación debe ser actual o futura")
    private final LocalDate fechaDeCreacion;

    @PositiveOrZero(message = "El costo debe ser positivo o cero")
    private final Double costo;

}
