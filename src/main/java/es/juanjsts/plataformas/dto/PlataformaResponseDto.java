package es.juanjsts.plataformas.dto;

import es.juanjsts.videojuegos.models.Videojuego;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlataformaResponseDto {
    private  Long id;

    private  String nombre;
    private  String fabricante;
    private  String tipo;
    private  LocalDate fechaDeLanzamiento;
    private  List<Videojuego> videojuegos;
    private Boolean isDeleted = false;

    private  LocalDateTime createdAt = LocalDateTime.now();
    private  LocalDateTime updatedAt = LocalDateTime.now();
    private  UUID uuid = UUID.randomUUID();
}
