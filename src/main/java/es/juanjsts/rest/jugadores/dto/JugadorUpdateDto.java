package es.juanjsts.rest.jugadores.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Data
@Builder
public class JugadorUpdateDto {
    @NotBlank
    @Length(min = 3)
    private final String nombre;
    private final Boolean isDeleted;
}
