package es.juanjsts.plataformas.services;

import es.juanjsts.rest.jugadores.dto.JugadorCreatedDto;
import es.juanjsts.rest.jugadores.dto.JugadorUpdateDto;
import es.juanjsts.rest.jugadores.exceptions.JugadorConflictException;
import es.juanjsts.rest.jugadores.mappers.JugadorMapper;
import es.juanjsts.rest.jugadores.models.Jugador;
import es.juanjsts.rest.jugadores.repositories.JugadorRepository;
import es.juanjsts.rest.jugadores.services.JugadorServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JugadorServiceImplTest {
    private final Jugador jugador = Jugador.builder()
            .id(1L)
            .nombre("Nintendo")
            .fabricante("Nintendo")
            .tipo("PC")
            .fechaDeLanzamiento(LocalDate.of(1985, 1, 1))
            .build();

    private final JugadorCreatedDto jugadorCreatedDto = JugadorCreatedDto.builder()
            .nombre("Epic Games Store")
            .fabricante("Epic Games")
            .tipo("PC")
            .fechaDeLanzamiento(LocalDate.of(1985, 1, 1))
            .build();

    private final JugadorUpdateDto jugadorUpdateDto = JugadorUpdateDto.builder()
            .nombre("PlayStation")
            .fabricante("Sony")
            .tipo("Consolas")
            .fechaDeLanzamiento(LocalDate.of(1995, 1, 1))
            .build();

    @Mock
    private JugadorRepository jugadorRepository;

    @Spy
    private JugadorMapper jugadorMapper;

    // Es la clase que se testea y a la que se inyectan los mocks y espías automáticamente
    @InjectMocks
    private JugadorServiceImpl plataformaService;

    @Test
    public void testFindAll() {
        // Arrange
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(List.of(jugador));
        when(jugadorRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        // Act
        var res = plataformaService.findAll(Optional.empty(), Optional.empty(), pageable);

        // Assert
        assertAll("findAll",
                () -> assertNotNull(res),
                () -> assertFalse(res.isEmpty())
        );

        // Verify
        verify(jugadorRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    public void testFindByNombre() {
        // Arrange
        when(jugadorRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.of(jugador));

        // Act
        var res = plataformaService.findByNombre("Nintendo");

        // Assert
        assertAll("findByNombre",
                () -> assertNotNull(res),
                () -> assertEquals("Nintendo", res.getNombre())
        );

        // Verify
        verify(jugadorRepository, times(1)).findByNombreEqualsIgnoreCase(anyString());
    }

    @Test
    public void testFindById() {
        // Arrange
        when(jugadorRepository.findById(anyLong())).thenReturn(Optional.of(jugador));

        // Act
        var res = plataformaService.findById(1L);

        // Assert
        assertAll("findById",
                () -> assertNotNull(res),
                () -> assertEquals("Nintendo", res.getNombre())
        );

        // Verify
        verify(jugadorRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testSave() {
        // Arrange
        when(jugadorRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(jugadorRepository.save(any(Jugador.class))).thenReturn(jugador);

        // Act
        plataformaService.save(jugadorCreatedDto);

        // Assert
        assertAll("save",
                () -> assertNotNull(jugador),
                () -> assertEquals("Nintendo", jugador.getNombre())
        );

        // Verify
        verify(jugadorRepository, times(1)).findByNombreEqualsIgnoreCase(anyString());
        verify(jugadorRepository, times(1)).save(any(Jugador.class));
    }

    @Test
    public void testSaveConflict() {
        // Arrange
        when(jugadorRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.of(jugador));

        // Act
        var res = assertThrows(JugadorConflictException.class,
                () -> plataformaService.save(jugadorCreatedDto));

        // Assert
        assertAll("saveConflict",
                () -> assertNotNull(res),
                () -> assertEquals("Ya existe una plataforma con el nombre: Epic Games Store", res.getMessage())
        );

        // Verify
        verify(jugadorRepository, times(1)).findByNombreEqualsIgnoreCase(anyString());
        verify(jugadorRepository, times(0)).save(any(Jugador.class));
    }

    @Test
    public void testUpdate() {
        // Arrange
        when(jugadorRepository.findById(anyLong())).thenReturn(Optional.of(jugador));
        when(jugadorRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.of(jugador));
        when(jugadorRepository.save(any(Jugador.class))).thenReturn(jugador);

        // Act
        plataformaService.update(1L, jugadorUpdateDto);

        // Assert
        assertAll("update",
                () -> assertNotNull(jugador),
                () -> assertEquals("Nintendo", jugador.getNombre())
        );


        // Verify
        verify(jugadorRepository, times(1)).findById(anyLong());
        verify(jugadorRepository, times(1)).findByNombreEqualsIgnoreCase(anyString());
        verify(jugadorRepository, times(1)).save(any(Jugador.class));
    }

    @Test
    public void testUpdateConflict() {
        // Arrange
        when(jugadorRepository.findById(anyLong())).thenReturn(Optional.of(jugador));
        when(jugadorRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.of(jugador));

        // Act, el id no debe ser igual, no se puede actualizar, porqe ya existe
        var res = assertThrows(JugadorConflictException.class,
                () -> plataformaService.update(2L, jugadorUpdateDto));

        // Assert
        assertAll("updateConflict",
                () -> assertNotNull(res),
                () -> assertEquals("Ya existe una plataforma con el nombre: PlayStation", res.getMessage())
        );

        // Verify
        verify(jugadorRepository, times(1)).findById(anyLong());
        verify(jugadorRepository, times(1)).findByNombreEqualsIgnoreCase(anyString());
        verify(jugadorRepository, times(0)).save(any(Jugador.class));
    }

    @Test
    public void testDeleteById() {
        // Arrange
        when(jugadorRepository.findById(anyLong())).thenReturn(Optional.of(jugador));
        when(jugadorRepository.existsVideojuegoById(anyLong())).thenReturn(false);

        // Act
        plataformaService.deleteById(1L);

        // Assert
        assertAll("deleteById",
                () -> assertNotNull(jugador),
                () -> assertEquals("Nintendo", jugador.getNombre())
        );

        // Verify
        verify(jugadorRepository, times(1)).findById(anyLong());
        verify(jugadorRepository, times(1)).existsVideojuegoById(anyLong());
        verify(jugadorRepository, times(1)).deleteById(anyLong());
    }

}
