package es.juanjsts.videojuegos.controllers;

import es.juanjsts.rest.videojuegos.dto.VideojuegoCreateDto;
import es.juanjsts.rest.videojuegos.dto.VideojuegoResponseDto;
import es.juanjsts.rest.videojuegos.dto.VideojuegoUpdateDto;
import es.juanjsts.rest.videojuegos.exceptions.VideojuegoNotFoundException;
import es.juanjsts.rest.videojuegos.services.VideojuegosService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class VideojuegosRestControllerTest {

    private final String ENDPOINT = "/api/v1/videojuegos";

    private final VideojuegoResponseDto videojuegoResponseDto1 = VideojuegoResponseDto.builder()
            .id(1L)
            .nombre("Marvel Rivals")
            .genero("Hero shooter")
            .jugador("Nintendo")
            .almacenamiento("15 GB")
            .fechaDeCreacion(LocalDate.of(2024, 12, 6))
            .costo(0.0)
            .build();

    private final VideojuegoResponseDto videojuegoResponseDto2 = VideojuegoResponseDto.builder()
            .id(2L)
            .nombre("Clash Royale")
            .genero("Estrategia")
            .almacenamiento("250 MB")
            .jugador("Nintendo")
            .fechaDeCreacion(LocalDate.of(2016, 3,2))
            .costo(0.0)
            .build();

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private VideojuegosService videojuegosService;

    @Test
    void getAll() {
        //Arrange

        var videojuegoResponses = List.of(videojuegoResponseDto1, videojuegoResponseDto2);
        var pageable =  PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(videojuegoResponses);
        when(videojuegosService.findAll(Optional.empty(), Optional.empty(), Optional.empty(), pageable))
                .thenReturn(page);

        //Act
        var resultado = mockMvcTester.get()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        //Assert
        assertThat(resultado)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.content.length()").isEqualTo(videojuegoResponses.size());
                    assertThat(json).extractingPath("$.content[0]")
                            .convertTo(VideojuegoResponseDto.class).isEqualTo(videojuegoResponseDto1);
                    assertThat(json).extractingPath("$.content[1]")
                            .convertTo(VideojuegoResponseDto.class).isEqualTo(videojuegoResponseDto2);
                });

        //Verify
        verify(videojuegosService, times(1))
                .findAll(Optional.empty(), Optional.empty(), Optional.empty(), pageable);
    }

    @Test
    void getAllByNombre() {
        //Arrange
        var videojuegoResponses = List.of(videojuegoResponseDto2);
        String queryString = "?nombre=" + videojuegoResponseDto2.getNombre();
        Optional<String> nombre = Optional.of(videojuegoResponseDto2.getNombre());
        var pageable =  PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(videojuegoResponses);
        when(videojuegosService.findAll(nombre,Optional.empty(), Optional.empty(), pageable))
                .thenReturn(page);

        //Act
        var resultado = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        //Assert
        assertThat(resultado)
            .hasStatusOk()
                .bodyJson().satisfies(json -> {
                   assertThat(json).extractingPath("$.content.length()").isEqualTo(videojuegoResponses.size());
                   assertThat(json).extractingPath("$.content[0]")
                           .convertTo(VideojuegoResponseDto.class).isEqualTo(videojuegoResponseDto2);
                });

        //Verify
        verify(videojuegosService, times(1))
                .findAll(nombre, Optional.empty(), Optional.empty(), pageable);
    }

    @Test
    void getAllByPlataforma() {
        //Arrange
        var  videojuegoResponses = List.of(videojuegoResponseDto2);
        String queryString = "?jugador=" + videojuegoResponseDto2.getJugador();
        Optional<String> jugador = Optional.of(videojuegoResponseDto2.getJugador());
        var pageable =  PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(videojuegoResponses);
        when(videojuegosService.findAll(Optional.empty(), jugador, Optional.empty(), pageable))
                .thenReturn(page);

        //Act
        var resultado = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        //Assert
        assertThat(resultado)
            .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.content.length()").isEqualTo(videojuegoResponses.size());
                    assertThat(json).extractingPath("$.content[0]")
                            .convertTo(VideojuegoResponseDto.class).isEqualTo(videojuegoResponseDto2);
                });
        //Verify
        verify(videojuegosService, only())
                .findAll(Optional.empty(), jugador, Optional.empty(), pageable);

    }

    @Test
    void getAllByNombreAndPlataforma() {
        //Arrange
        var  videojuegoResponses = List.of(videojuegoResponseDto2);
        String queryString = "?nombre=" + videojuegoResponseDto2.getNombre() + "&jugador=" + videojuegoResponseDto2.getJugador();
        Optional<String> nombre = Optional.of(videojuegoResponseDto2.getNombre());
        Optional<String> jugador = Optional.of(videojuegoResponseDto2.getJugador());
        var pageable =  PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(videojuegoResponses);
        when(videojuegosService.findAll(nombre, jugador, Optional.empty(), pageable))
                .thenReturn(page);

        //Act
        var resultado = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        //Assert
        assertThat(resultado)
                .bodyJson().satisfies(json -> {
                   assertThat(json).extractingPath("$.content.length()").isEqualTo(videojuegoResponses.size());
                   assertThat(json).extractingPath("$.content[0]")
                           .convertTo(VideojuegoResponseDto.class).isEqualTo(videojuegoResponseDto2);
                });

        //Verify
        verify(videojuegosService, only())
                .findAll(nombre, jugador, Optional.empty(), pageable);
    }

    @Test
    void getById_ReturnJsonWithVideojuego_WhenValidIdProvided() {
        //Arrange
        Long id = videojuegoResponseDto1.getId();
        when(videojuegosService.findById(id)).thenReturn(videojuegoResponseDto1);

        //Act
        var resultado = mockMvcTester.get()
                .uri(ENDPOINT + "/" + id.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        //Assert
        assertThat(resultado)
                .hasStatusOk()
                .bodyJson()
                .convertTo(VideojuegoResponseDto.class)
                .isEqualTo(videojuegoResponseDto1);

        //Verify
        verify(videojuegosService, only()).findById(anyLong());
    }

    @Test
    void getById_shouldThrowVideojuegoNotFound_WhenInvalidIdProvided() {
        //Arrange
        Long id = 3L;
        when(videojuegosService.findById(anyLong())).thenThrow(new VideojuegoNotFoundException(id));

        //Act
        var resultado = mockMvcTester.get()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        //Assert
        assertThat(resultado)
            .hasStatus4xxClientError()
                .hasFailed().failure()
                .isInstanceOf(VideojuegoNotFoundException.class)
                .hasMessageContaining("no encontrado");

        verify(videojuegosService, only()).findById(anyLong());

    }

    @Test
    void create() {
        //Arrange
        String requestBody = """
                {
                    "nombre": "Plants vs Zombies Replanted",
                    "genero": "Estrategia",
                    "almacenamiento": "1 GB",
                    "fechaDeCreacion": "2025-10-07",
                    "costo": 19.99
                }
                """;

        var videojuegoSaved = VideojuegoResponseDto.builder()
                .id(1L)
                .nombre("Plants vs Zombies Replanted")
                .genero("tower defense")
                .almacenamiento("25 GB")
                .costo(20.99)
                .build();

        when(videojuegosService.save(any(VideojuegoCreateDto.class))).thenReturn(videojuegoSaved);

        //Act
        var resultado = mockMvcTester.post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        //Assert
        assertThat(resultado)
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(VideojuegoResponseDto.class)
                .isEqualTo(videojuegoSaved);

        //Verify
        verify(videojuegosService, only()).save(any(VideojuegoCreateDto.class));
    }

    @Test
    void create_WhenBadRequest() {
        //Arrange
        String requestBody = """
                {
                    "nombre": "Plants vs Zombies Replanted",
                    "genero": "",
                    "almacenamiento": "1 XB",
                    "fechaDeCreacion": "2026-12-07",
                    "costo": 19.99
                }
                """;

        //Act
        var resultado = mockMvcTester.post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        //Assert
        assertThat(resultado)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                    .hasPathSatisfying("$.errores", path -> {
                       assertThat(path).hasFieldOrProperty("genero");
                       assertThat(path).hasFieldOrProperty("fechaDeCreacion");
                       assertThat(path).hasFieldOrProperty("almacenamiento");
                });

        //Verify
        verify(videojuegosService, never()).save(any(VideojuegoCreateDto.class));
    }

    @Test
    void update() {
        //Arrange
        Long id = 1L;
        String  requestBody = """
                {
                    "genero": "Battle Royale"
                }
                """;

        var videojuegoSaved = VideojuegoResponseDto.builder()
                .nombre("Plants vs Zombies Replanted")
                .genero("Estrategia")
                .almacenamiento("1 GB")
                .fechaDeCreacion(LocalDate.of(2025, 10, 7))
                .costo(19.99)
                .build();

        when(videojuegosService.update(anyLong(), any(VideojuegoUpdateDto.class))).thenReturn(videojuegoSaved);

        //Act
        var resultado = mockMvcTester.put()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        //Assert
        assertThat(resultado)
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .convertTo(VideojuegoResponseDto.class)
                .isEqualTo(videojuegoSaved);

        //Verify
        verify(videojuegosService, only()).update(anyLong(), any(VideojuegoUpdateDto.class));
    }

    @Test
    void update_shouldThrowVideojuegoNotFoundException_whenInvalidIdProvided() {
        //Arrange
        Long id = 3L;
        String  requestBody = """
                {
                    "genero": "Battle Royale"
                }
                """;

        when(videojuegosService.update(anyLong(), any(VideojuegoUpdateDto.class))).thenThrow(new VideojuegoNotFoundException(id));

        //Act
        var resultado = mockMvcTester.put()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        //Assert
        assertThat(resultado)
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasFailed().failure()
                .isInstanceOf(VideojuegoNotFoundException.class)
                .hasMessageContaining("Videojuego con id: " + id + " no encontrado");

        //Verify
        verify(videojuegosService, only()).update(anyLong(), any());
    }

    @Test
    void updatePartial() {
        Long id = 1L;
        String  requestBody = """
                {
                    "genero": "Battle Royale"
                }
                """;
        var videojuegoSaved = VideojuegoResponseDto.builder()
                .nombre("Plants vs Zombies Replanted")
                .genero("Estrategia")
                .almacenamiento("1 GB")
                .fechaDeCreacion(LocalDate.of(2025, 10, 7))
                .costo(19.99)
                .build();

        when(videojuegosService.update(anyLong(), any(VideojuegoUpdateDto.class))).thenReturn(videojuegoSaved);

        //Act
        var resultado = mockMvcTester.put()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        //Assert
        assertThat(resultado)
                .hasStatusOk()
                .bodyJson()
                .convertTo(VideojuegoResponseDto.class)
                .isEqualTo(videojuegoSaved);

        //Verify
        verify(videojuegosService, only()).update(anyLong(), any(VideojuegoUpdateDto.class));
    }

    @Test
    void delete() {
        //Arrange
        Long id = 1L;
        doNothing().when(videojuegosService).deleteById(anyLong());

        //Act
        var resultado = mockMvcTester.delete()
                .uri(ENDPOINT + "/" + id)
                .exchange();

        //Assert
        assertThat(resultado)
                .hasStatus(HttpStatus.NO_CONTENT);

        //Verify
        verify(videojuegosService, only()).deleteById(anyLong());
    }

    @Test
    void delete_shouldThrowVideojuegoNotFoundException_whenInvalidIdProvided() {
        //Arrange
        Long id = 3L;
        doThrow(new VideojuegoNotFoundException(id)).when(videojuegosService).deleteById(anyLong());

        //Act
        var resultado = mockMvcTester.delete()
                .uri(ENDPOINT + "/" + id)
                .exchange();

        //Assert
        assertThat(resultado)
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasFailed().failure()
                .isInstanceOf(VideojuegoNotFoundException.class)
                .hasMessageContaining("Videojuego con id: " + id + " no encontrado");

        //Verify
        verify(videojuegosService, only()).deleteById(anyLong());
    }
}
