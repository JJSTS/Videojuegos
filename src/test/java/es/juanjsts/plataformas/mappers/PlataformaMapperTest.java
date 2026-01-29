package es.juanjsts.plataformas.mappers;

import es.juanjsts.rest.jugadores.dto.JugadorCreatedDto;
import es.juanjsts.rest.jugadores.dto.JugadorUpdateDto;
import es.juanjsts.rest.jugadores.mappers.JugadorMapper;
import es.juanjsts.rest.jugadores.models.Jugador;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PlataformaMapperTest {
    private final JugadorMapper jugadorMapper = new JugadorMapper();

    private final Jugador jugador = Jugador.builder()
            .id(1L)
            .nombre("Nintendo")
            .fabricante("Nintendo")
            .tipo("PC")
            .fechaDeLanzamiento(LocalDate.of(1985, 1, 1))
            .build();

    private final JugadorCreatedDto jugadorCreatedDto = JugadorCreatedDto.builder()
            .nombre("NINTENDO")
            .fabricante("Nintendo")
            .tipo("PC")
            .fechaDeLanzamiento(LocalDate.of(1985, 1, 1))
            .build();

    private final JugadorUpdateDto jugadorUpdateDto = JugadorUpdateDto.builder()
            .nombre("NINTENDO")
            .fabricante("PANINI")
            .tipo("NOSE")
            .fechaDeLanzamiento(LocalDate.of(9999, 1, 1))
            .build();

    @Test
    public void toPlataformaCreatedDto() {
        Jugador mappedJugador = jugadorMapper.toPlataforma(jugadorCreatedDto);

        assertAll("whenToPlataforma_thenReturnPlataforma",
                () -> assertEquals(jugadorCreatedDto.getNombre(), mappedJugador.getNombre()));
    }

    @Test
    public void whenToPlataformaWithExistingPlataforma_thenReturnUpdatedPlataforma() {
        Jugador updateJugador = jugadorMapper.toPlataforma(jugadorUpdateDto, jugador);

        assertAll("whenToPlataformaWithExistingPlataforma_thenReturnUpdatedPlataforma",
                () -> assertEquals(jugadorCreatedDto.getNombre(), updateJugador.getNombre()));
    }
}
