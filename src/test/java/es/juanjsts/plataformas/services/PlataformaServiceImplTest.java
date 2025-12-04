package es.juanjsts.plataformas.services;

import es.juanjsts.plataformas.dto.PlataformaCreatedDto;
import es.juanjsts.plataformas.dto.PlataformaUpdateDto;
import es.juanjsts.plataformas.exceptions.PlataformaConflictException;
import es.juanjsts.plataformas.mappers.PlataformaMapper;
import es.juanjsts.plataformas.models.Plataforma;
import es.juanjsts.plataformas.repositories.PlataformaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlataformaServiceImplTest {
    private final Plataforma plataforma = Plataforma.builder()
            .id(1L)
            .nombre("Nintendo")
            .fabricante("Nintendo")
            .tipo("PC")
            .fechaDeLanzamiento(LocalDate.of(1985, 1, 1))
            .build();

    private final PlataformaCreatedDto plataformaCreatedDto = PlataformaCreatedDto.builder()
            .nombre("Epic Games Store")
            .fabricante("Epic Games")
            .tipo("PC")
            .fechaDeLanzamiento(LocalDate.of(1985, 1, 1))
            .build();

    private final PlataformaUpdateDto plataformaUpdateDto = PlataformaUpdateDto.builder()
            .nombre("PlayStation")
            .fabricante("Sony")
            .tipo("Consolas")
            .fechaDeLanzamiento(LocalDate.of(1995, 1, 1))
            .build();
    
    @Mock
    private PlataformaRepository plataformaRepository;
    // Es la clase que se testea y a la que se inyectan los mocks y espías automáticamente
    @InjectMocks
    private PlataformaServiceImpl plataformaService;

    @Test
    public void testFindAll() {
        // Arrange
        when(plataformaRepository.findAll()).thenReturn(List.of(plataforma));

        // Act
        var res = plataformaService.findAll(null);

        // Assert
        assertAll("findAll",
                () -> assertNotNull(res),
                () -> assertFalse(res.isEmpty())
        );

        // Verify
        verify(plataformaRepository, times(1)).findAll();
    }

    @Test
    public void testFindByNombre() {
        // Arrange
        when(plataformaRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.of(plataforma));

        // Act
        var res = plataformaService.findByNombre("Nintendo");

        // Assert
        assertAll("findByNombre",
                () -> assertNotNull(res),
                () -> assertEquals("Nintendo", res.getNombre())
        );

        // Verify
        verify(plataformaRepository, times(1)).findByNombreEqualsIgnoreCase(anyString());
    }

    @Test
    public void testFindById() {
        // Arrange
        when(plataformaRepository.findById(anyLong())).thenReturn(Optional.of(plataforma));

        // Act
        var res = plataformaService.findById(1L);

        // Assert
        assertAll("findById",
                () -> assertNotNull(res),
                () -> assertEquals("Nintendo", res.getNombre())
        );

        // Verify
        verify(plataformaRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testSave() {
        // Arrange
        when(plataformaRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(plataformaRepository.save(any(Plataforma.class))).thenReturn(plataforma);

        // Act
        plataformaService.save(plataformaCreatedDto);

        // Assert
        assertAll("save",
                () -> assertNotNull(plataforma),
                () -> assertEquals("Epic Games Store", plataforma.getNombre())
        );

        // Verify
        verify(plataformaRepository, times(1)).findByNombreEqualsIgnoreCase(anyString());
        verify(plataformaRepository, times(1)).save(any(Plataforma.class));
    }

    @Test
    public void testSaveConflict() {
        // Arrange
        when(plataformaRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.of(plataforma));

        // Act
        var res = assertThrows(PlataformaConflictException.class,
                () -> plataformaService.save(plataformaCreatedDto));

        // Assert
        assertAll("saveConflict",
                () -> assertNotNull(res),
                () -> assertEquals("Ya existe una plataforma con el nombre: Epic Games Store", res.getMessage())
        );

        // Verify
        verify(plataformaRepository, times(1)).findByNombreEqualsIgnoreCase(anyString());
        verify(plataformaRepository, times(0)).save(any(Plataforma.class));
    }

    @Test
    public void testUpdate() {
        // Arrange
        when(plataformaRepository.findById(anyLong())).thenReturn(Optional.of(plataforma));
        when(plataformaRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.of(plataforma));
        when(plataformaRepository.save(any(Plataforma.class))).thenReturn(plataforma);

        // Act
        plataformaService.update(1L, plataformaUpdateDto);

        // Assert
        assertAll("update",
                () -> assertNotNull(plataforma),
                () -> assertEquals("Nintendo", plataforma.getNombre())
        );


        // Verify
        verify(plataformaRepository, times(1)).findById(anyLong());
        verify(plataformaRepository, times(1)).findByNombreEqualsIgnoreCase(anyString());
        verify(plataformaRepository, times(1)).save(any(Plataforma.class));
    }

    @Test
    public void testUpdateConflict() {
        // Arrange
        when(plataformaRepository.findById(anyLong())).thenReturn(Optional.of(plataforma));
        when(plataformaRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.of(plataforma));

        // Act, el id no debe ser igual, no se puede actualizar, porqe ya existe
        var res = assertThrows(PlataformaConflictException.class,
                () -> plataformaService.update(2L, plataformaUpdateDto));

        // Assert
        assertAll("updateConflict",
                () -> assertNotNull(res),
                () -> assertEquals("Ya existe una plataforma con el nombre: PlayStation", res.getMessage())
        );

        // Verify
        verify(plataformaRepository, times(1)).findById(anyLong());
        verify(plataformaRepository, times(1)).findByNombreEqualsIgnoreCase(anyString());
        verify(plataformaRepository, times(0)).save(any(Plataforma.class));
    }

    @Test
    public void testDeleteById() {
        // Arrange
        when(plataformaRepository.findById(anyLong())).thenReturn(Optional.of(plataforma));
        when(plataformaRepository.existsVideojuegoById(anyLong())).thenReturn(false);

        // Act
        plataformaService.deleteById(1L);

        // Assert
        assertAll("deleteById",
                () -> assertNotNull(plataforma),
                () -> assertEquals("Nintendo", plataforma.getNombre())
        );

        // Verify
        verify(plataformaRepository, times(1)).findById(anyLong());
        verify(plataformaRepository, times(1)).existsVideojuegoById(anyLong());
        verify(plataformaRepository, times(1)).deleteById(anyLong());
    }

}