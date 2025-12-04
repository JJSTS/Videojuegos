package es.juanjsts.plataformas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Data
@Builder
public class PlataformaCreatedDto {
    @NotBlank
    @Length(min = 3)
    private final String nombre;

    private final String fabricante;
    private final String tipo;
    private final LocalDate fechaDeLanzamiento;
    private final Boolean isDeleted;
}