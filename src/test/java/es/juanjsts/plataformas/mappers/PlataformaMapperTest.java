package es.juanjsts.plataformas.mappers;

import es.juanjsts.plataformas.dto.PlataformaCreatedDto;
import es.juanjsts.plataformas.dto.PlataformaUpdateDto;
import es.juanjsts.plataformas.models.Plataforma;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PlataformaMapperTest {
    private final PlataformaMapper plataformaMapper = new PlataformaMapper();

    private final Plataforma plataforma = Plataforma.builder()
            .id(1L)
            .nombre("Nintendo")
            .fabricante("Nintendo")
            .tipo("PC")
            .fechaDeLanzamiento(LocalDate.of(1985, 1, 1))
            .build();

    private final PlataformaCreatedDto plataformaCreatedDto = PlataformaCreatedDto.builder()
            .nombre("NINTENDO")
            .fabricante("Nintendo")
            .tipo("PC")
            .fechaDeLanzamiento(LocalDate.of(1985, 1, 1))
            .build();

    private final PlataformaUpdateDto plataformaUpdateDto = PlataformaUpdateDto.builder()
            .nombre("NINTENDO")
            .fabricante("PANINI")
            .tipo("NOSE")
            .fechaDeLanzamiento(LocalDate.of(9999, 1, 1))
            .build();

    @Test
    public void toPlataformaCreatedDto() {
        Plataforma mappedPlataforma = plataformaMapper.toPlataforma(plataformaCreatedDto);

        assertAll("whenToPlataforma_thenReturnPlataforma",
                () -> assertEquals(plataformaCreatedDto.getNombre(), mappedPlataforma.getNombre()));
    }

    @Test
    public void whenToPlataformaWithExistingPlataforma_thenReturnUpdatedPlataforma() {
        Plataforma updatePlataforma = plataformaMapper.toPlataforma(plataformaUpdateDto, plataforma);

        assertAll("whenToPlataformaWithExistingPlataforma_thenReturnUpdatedPlataforma",
                () -> assertEquals(plataformaCreatedDto.getNombre(), updatePlataforma.getNombre()));
    }
}