package es.juanjsts.videojuegos.services;

import es.juanjsts.videojuegos.dto.VideojuegoCreateDto;
import es.juanjsts.videojuegos.dto.VideojuegoResponseDto;
import es.juanjsts.videojuegos.dto.VideojuegoUpdateDto;
import es.juanjsts.videojuegos.exceptions.VideojuegoNotFoundException;
import es.juanjsts.videojuegos.mappers.VideojuegoMapper;
import es.juanjsts.videojuegos.models.Videojuego;
import es.juanjsts.videojuegos.repositories.VideojuegosRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideojuegoServiceImplTest {

    private final Videojuego videojuego1 = Videojuego.builder()
            .id(1L)
            .nombre("Among us")
            .genero("Party")
            .almacenamiento("3.0 GB")
            .fechaDeCreacion(LocalDate.of(2018,8,8))
            .costo(2.99)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478"))
            .build();

    private final Videojuego videojuego2 = Videojuego.builder()
            .id(2L)
            .nombre("Fortnite")
            .genero("Battle Royale")
            .almacenamiento("15.0 GB")
            .fechaDeCreacion(LocalDate.of(2019,10,14))
            .costo(0.00)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("b36835eb-e56a-4023-b058-52bfa600fee5"))
            .build();

    private VideojuegoResponseDto videojuegoResponse;

    @Mock
    private VideojuegosRepository videojuegosRepository;

    @Spy
    private VideojuegoMapper videojuegoMapper;

    @InjectMocks
    private VideojuegoServiceImpl videojuegoService;

    @Captor
    private ArgumentCaptor<Videojuego> videojuegoCaptor;

    @BeforeEach
    void setUp() {
        videojuegoResponse = videojuegoMapper.toVideojuegoResponseDto(videojuego1);
    }

    @Test
    void findAll_ShouldReturnAllVideojuegos_WhenNoParametersProvided() {
        //Arrange
        List <Videojuego> expectedVideojuego = Arrays.asList(videojuego1, videojuego2);
        List <VideojuegoResponseDto> expectedVideojuegoResponses = videojuegoMapper.toResponseDtoList(expectedVideojuego);
        when(videojuegosRepository.findAll()).thenReturn(expectedVideojuego);

        //Act
        List <VideojuegoResponseDto> actualVideojuego = videojuegoService.findAll(null, null);

        //Assert
        assertIterableEquals(expectedVideojuegoResponses, actualVideojuego);

        //Verify
        verify(videojuegosRepository, times(1)).findAll();
    }

    @Test
    void findAll_ShouldReturnAllVideojuegos_WhenNombreParametersProvided() {
        //Arrange
        String nombre = "Among us";
        List <Videojuego> expectedVideojuego = Arrays.asList(videojuego1);
        List <VideojuegoResponseDto> expectedVideojuegoResponses = videojuegoMapper.toResponseDtoList(expectedVideojuego);
        when(videojuegosRepository.findAllByNombre(nombre)).thenReturn(expectedVideojuego);

        //Act
        List <VideojuegoResponseDto> actualVideojuego = videojuegoService.findAll(nombre, null);

        //Assert
        assertIterableEquals(expectedVideojuegoResponses, actualVideojuego);

        //Verify
        verify(videojuegosRepository, only()).findAllByNombre(nombre);
    }

    @Test
    void findAll_ShouldReturnAllVideojuegos_WhenGeneroParametersProvided() {
        String genero = "Battle Royale";
        List <Videojuego> expectedVideojuego = Arrays.asList(videojuego1);
        List <VideojuegoResponseDto> expectedVideojuegoResponses = videojuegoMapper.toResponseDtoList(expectedVideojuego);
        when(videojuegosRepository.findAllByGenero(genero)).thenReturn(expectedVideojuego);

        //Act
        List <VideojuegoResponseDto> actualVideojuego = videojuegoService.findAll(null, genero);

        //Assert
        assertIterableEquals(expectedVideojuegoResponses, actualVideojuego);

        //Verify
        verify(videojuegosRepository, only()).findAllByGenero(genero);
    }

    @Test
    void findAll_ShouldReturnAllVideojuegos_WhenBothParametersProvided() {
        //Arrange
        String nombre = "Among us";
        String genero = "Battle Royale";
        List <Videojuego> expectedVideojuego = Arrays.asList(videojuego1);
        List <VideojuegoResponseDto> expectedVideojuegoResponses = videojuegoMapper.toResponseDtoList(expectedVideojuego);
        when(videojuegosRepository.findAllByNombreAndGenero(nombre, genero)).thenReturn(expectedVideojuego);

        //Act
        List <VideojuegoResponseDto> actualVideojuego = videojuegoService.findAll(nombre, genero);

        //Assert
        assertIterableEquals(expectedVideojuegoResponses, actualVideojuego);

        //Verify
        verify(videojuegosRepository, only()).findAllByNombreAndGenero(nombre, genero);
    }

    @Test
    void findById_ShouldReturnVideojuego_WhenValidIdProvided() {
        //Arrange
        Long id = 1L;
        VideojuegoResponseDto expectedVideojuegoResponse = videojuegoResponse;
        when(videojuegosRepository.findById(id)).thenReturn(Optional.of(videojuego1));

        //Act
        VideojuegoResponseDto actualVideojuegoResponse = videojuegoService.findById(id);

        //Assert
        assertEquals(expectedVideojuegoResponse, actualVideojuegoResponse);

        //Verify
        verify(videojuegosRepository, only()).findById(id);
    }

    @Test
    void findById_ShouldReturnVideojuego_WhenInvalidIdProvided() {
        //Arrange
        Long id = 1L;
        when(videojuegosRepository.findById(id)).thenReturn(Optional.empty());

        //Act y Assert
        var res = assertThrows(VideojuegoNotFoundException.class, () -> videojuegoService.findById(id));
        assertEquals("Videojuego con id: " + id + " no encontrado", res.getMessage());

        //Verify
        verify(videojuegosRepository).findById(id);
    }


    @Test
    void findByUuid_ShouldReturnVideojuego_WhenValidUuidProvided() {
        //Arrange
        UUID expectedUuid = videojuego1.getUuid();
        VideojuegoResponseDto expectedVideojuegoResponse = videojuegoResponse;
        when(videojuegosRepository.findByUuid(expectedUuid)).thenReturn(Optional.of(videojuego1));

        //Act
        VideojuegoResponseDto actualVideojuegoResponse = videojuegoService.findByUuid(expectedUuid.toString());

        //Assert
        assertEquals(expectedVideojuegoResponse, actualVideojuegoResponse);

        //Verify
        verify(videojuegosRepository).findByUuid(expectedUuid);
    }

    @Test
    void findByUuid_ShouldReturnVideojuego_WhenInvalidUuidProvided() {
        //Arrange
        String uuid = "4lkjnb1bjnkl12bjnk4b214oj2124nib412";

        //Act y Assert
        var res =  assertThrows(VideojuegoNotFoundException.class, () -> videojuegoService.findByUuid(uuid));
        assertEquals("Videojuego con uuid: " + uuid + " no encontrado", res.getMessage());

        //Verify
        verify(videojuegosRepository, never()).findByUuid(any());
    }

    @Test
    void save_ShouldReturnSavedVideojuego_WhenValidVideojuegoCreatedDtoProvided() {
        //Arrange
        VideojuegoCreateDto videojuegoCreateDto = VideojuegoCreateDto.builder()
                .nombre("Plants vs Zombies Replanted")
                .genero("tower defense")
                .almacenamiento("25 GB")
                .costo(20.99)
                .build();

        Videojuego expectedVideojuego = Videojuego.builder()
                .id(1L)
                .nombre("Plants vs Zombies Replanted")
                .genero("tower defense")
                .almacenamiento("25 GB")
                .costo(20.99)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(UUID.randomUUID())
                .build();

        VideojuegoResponseDto expectedVideojuegoResponse = videojuegoMapper.toVideojuegoResponseDto(expectedVideojuego);

        when(videojuegosRepository.nextId()).thenReturn(1L);
        when(videojuegosRepository.save(any(Videojuego.class))).thenReturn(expectedVideojuego);

        //Act
        VideojuegoResponseDto actualResponseDto = videojuegoService.save(videojuegoCreateDto);

        //Assert
        assertEquals(expectedVideojuegoResponse, actualResponseDto);

        //Verify
        verify(videojuegosRepository).nextId();
        verify(videojuegosRepository).save(videojuegoCaptor.capture());

        Videojuego videojuegoCaptured = videojuegoCaptor.getValue();
        assertEquals(expectedVideojuego.getNombre(), videojuegoCaptured. getNombre());
    }

    @Test
    void update_ShouldReturnSavedVideojuego_WhenValidIdAndVideojuegoUpdatedDtoProvided() {
        //Arrange
        Long id = 1L;
        Double costo = 99.99;
        when(videojuegosRepository.findById(id)).thenReturn(Optional.of(videojuego1));

        VideojuegoUpdateDto videojuegoUpdateDto = VideojuegoUpdateDto.builder()
                .costo(costo)
                .build();
        Videojuego videojuegoUpdate = videojuegoMapper.toVideojuego(videojuegoUpdateDto, videojuego1);
        when(videojuegosRepository.save(any(Videojuego.class))).thenReturn(videojuegoUpdate);

        videojuegoResponse.setCosto(costo);
        VideojuegoResponseDto expectedVideojuegoResponse = videojuegoResponse;

        //Act
        VideojuegoResponseDto actualVideojuegoResponse = videojuegoService.update(id, videojuegoUpdateDto);

        //Assert
        assertThat(actualVideojuegoResponse)
                .usingRecursiveComparison()
                .ignoringFields("updatedAt")
                .isEqualTo(expectedVideojuegoResponse);

        //Verify
        verify(videojuegosRepository).findById(id);
        verify(videojuegosRepository).save(any());
    }

    @Test
    void update_ShouldReturnSavedVideojuego_WhenInvalidIdProvided() {
        //Arrange
        Long id = 1L;
        VideojuegoUpdateDto videojuegoUpdateDto = VideojuegoUpdateDto.builder()
                .costo(20.99)
                .build();

        when(videojuegosRepository.findById(id)).thenReturn(Optional.empty());

        //Act y Assert
        assertThatThrownBy(
                () -> videojuegoService.update(id, videojuegoUpdateDto))
                .isInstanceOf(VideojuegoNotFoundException.class)
                .hasMessage("Videojuego con id: " + id + " no encontrado");

        //Verify
        verify(videojuegosRepository).findById(id);
        verify(videojuegosRepository, never()).save(any());
    }

    @Test
    void deleteById_ShouldDeleteVideojuego_WhenValidIdProvided() {
        //Arrange
        Long  id = 1L;
        when(videojuegosRepository.findById(id)).thenReturn(Optional.of(videojuego1));

        //Act con AssertJ
        assertThatCode(() -> videojuegoService.deleteById(id))
                .doesNotThrowAnyException();

        //Verify
        verify(videojuegosRepository).deleteById(id);
    }

    @Test
    void deleteById_ShouldDeleteVideojuego_WhenInvalidIdProvided() {
        //Arrange
        Long id = 1L;
        when(videojuegosRepository.findById(id)).thenReturn(Optional.empty());

        //AssertJ
        assertThatThrownBy(() -> videojuegoService.deleteById(id))
                .isInstanceOf(VideojuegoNotFoundException.class)
                .hasMessage("Videojuego con id: " + id + " no encontrado");

        //Verify
        verify(videojuegosRepository, never()).deleteById(id);
    }
}