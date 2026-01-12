package es.juanjsts.rest.plataformas.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Data
@Builder
public class PlataformaUpdateDto {
    @NotBlank
    @Length(min = 3)
    private final String nombre;

    private final String fabricante;
    private final String tipo;
    private final LocalDate fechaDeLanzamiento;
    private final Boolean isDeleted;
}
