package es.juanjsts.videojuegos.mappers;

import es.juanjsts.rest.jugadores.models.Jugador;
import es.juanjsts.rest.videojuegos.dto.VideojuegoCreateDto;
import es.juanjsts.rest.videojuegos.dto.VideojuegoUpdateDto;
import es.juanjsts.rest.videojuegos.mappers.VideojuegoMapper;
import es.juanjsts.rest.videojuegos.models.Videojuego;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VideojuegoMapperTest {

    private final Jugador jugador = Jugador.builder().nombre("Nintendo").build();

    private final VideojuegoMapper videojuegoMapper = new VideojuegoMapper();


    @Test
    void toVideojuego_create() {
        //Arrange
        Long id = 1L;
        VideojuegoCreateDto videojuegoCreateDto = VideojuegoCreateDto.builder()
                .nombre("Marvel Rivals")
                .genero("Hero shooter")
                .almacenamiento("15 GB")
                .plataforma("Nintendo")
                .fechaDeCreacion(LocalDate.of(2024, 12, 6))
                .costo(0.0)
                .build();
        //Act
        var res = videojuegoMapper.toVideojuego(videojuegoCreateDto, jugador);
        //Assert
        assertAll(
                () -> assertEquals(videojuegoCreateDto.getNombre(), res.getNombre()),
                () -> assertEquals(videojuegoCreateDto.getGenero(), res.getGenero()),
                () -> assertEquals(videojuegoCreateDto.getAlmacenamiento(), res.getAlmacenamiento()),
                () -> assertEquals(videojuegoCreateDto.getCosto(), res.getCosto()),
                () -> assertEquals(videojuegoCreateDto.getFechaDeCreacion(), res.getFechaDeCreacion()),
                () -> assertEquals(videojuegoCreateDto.getPlataforma(), res.getJugador().getNombre()),
                () -> assertEquals(videojuegoCreateDto.getCosto(), res.getCosto())
        );
    }

    @Test
    void testToVideojuego_update() {
        //Arrange
        Long id = 1L;
        VideojuegoUpdateDto videojuegoUpdateDto = VideojuegoUpdateDto.builder()
                .nombre("Marvel Rivals")
                .genero("Hero shooter")
                .almacenamiento("15 GB")
                .fechaDeCreacion(LocalDate.of(2024, 12, 6))
                .costo(0.0)
                .build();

        Videojuego videojuego = Videojuego.builder()
                .id(id)
                .nombre(videojuegoUpdateDto.getNombre())
                .genero(videojuegoUpdateDto.getGenero())
                .almacenamiento(videojuegoUpdateDto.getAlmacenamiento())
                .fechaDeCreacion(videojuegoUpdateDto.getFechaDeCreacion())
                .costo(videojuegoUpdateDto.getCosto())
                .build();
        //Act
        var res = videojuegoMapper.toVideojuego(videojuegoUpdateDto, videojuego);

        //Assert
        assertAll(
                () -> assertEquals(id, res.getId()),
                () -> assertEquals(videojuegoUpdateDto.getNombre(), res.getNombre()),
                () -> assertEquals(videojuegoUpdateDto.getGenero(), res.getGenero()),
                () -> assertEquals(videojuegoUpdateDto.getAlmacenamiento(), res.getAlmacenamiento()),
                () -> assertEquals(videojuegoUpdateDto.getFechaDeCreacion(), res.getFechaDeCreacion()),
                () -> assertEquals(videojuegoUpdateDto.getCosto(), res.getCosto())
        );
    }

    @Test
    void toVideojuegoResponseDto() {
        //Arrange
        Videojuego videojuego = Videojuego.builder()
                .id(1L)
                .nombre("Marvel Rivals")
                .genero("Hero shooter")
                .almacenamiento("15 GB")
                .fechaDeCreacion(LocalDate.of(2024, 12, 6))
                .costo(0.0)
                .jugador(jugador)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(UUID.randomUUID())
                .build();
        //Act
        var res = videojuegoMapper.toVideojuegoResponseDto(videojuego);

        //Assert
        assertAll(
                () -> assertEquals(videojuego.getId(), res.getId()),
                () -> assertEquals(videojuego.getNombre(), res.getNombre()),
                () -> assertEquals(videojuego.getGenero(), res.getGenero()),
                () -> assertEquals(videojuego.getAlmacenamiento(), res.getAlmacenamiento()),
                () -> assertEquals(videojuego.getCosto(), res.getCosto())
        );

    }
}
