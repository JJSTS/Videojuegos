package es.juanjsts.videojuegos.dto;

import lombok.Data;

import java.time.LocalDate;


@Data
public class VideojuegoUpdateDto {
    private final String nombre;
    private final String genero;
    private final String almacenamiento;
    private final LocalDate fechaDeCreacion;
    private final Double costo;
}
