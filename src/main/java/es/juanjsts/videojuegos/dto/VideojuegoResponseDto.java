package es.juanjsts.videojuegos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideojuegoResponseDto {
    private Long id;

    private String nombre;
    private String genero;
    private String almacenamiento;
    private LocalDate fechaDeCreacion;
    private Double costo;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID uuid;
}
