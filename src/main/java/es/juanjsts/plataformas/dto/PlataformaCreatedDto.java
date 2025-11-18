package es.juanjsts.plataformas.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PlataformaCreatedDto {
    private final String nombre;
    private final String fabricante;
    private final String tipo;
    private final LocalDate fechaDeLanzamiento;
    private final Boolean isDeleted;
}