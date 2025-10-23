package es.juanjsts.videojuegos.models;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
@Builder
@Data
public class Videojuego {
    private final Long id;

    private final String nombre;
    private final String genero;
    private final String almacenamiento;
    private final LocalDate fechaDeCreacion;
    private final Double costo;

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final UUID uuid;
}
