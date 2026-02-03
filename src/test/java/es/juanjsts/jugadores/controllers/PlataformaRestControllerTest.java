package es.juanjsts.jugadores.controllers;

import es.juanjsts.rest.jugadores.dto.JugadorCreatedDto;
import es.juanjsts.rest.jugadores.dto.JugadorUpdateDto;
import es.juanjsts.rest.jugadores.exceptions.JugadorConflictException;
import es.juanjsts.rest.jugadores.exceptions.JugadorNotFoundException;
import es.juanjsts.rest.jugadores.models.Jugador;
import es.juanjsts.rest.jugadores.services.JugadorService;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class PlataformaRestControllerTest {
    private final String ENDPOINT = "/api/v1/plataformas";

    private final Jugador plataforma1 = Jugador.builder()
            .id(1L)
            .nombre("Nintendo")
            .build();
    private final Jugador plataforma2 = Jugador.builder()
            .id(2L)
            .nombre("PlayStation")
            .build();

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private JugadorService jugadorService;

    @Test
    void getAll() {
        // Arrange
        var plataformas = List.of(plataforma1, plataforma2);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(plataformas);
        when(jugadorService.findAll(Optional.empty(),Optional.empty(), pageable)).thenReturn(page);

        // Act. Consultar el endpoint
        var result = mockMvcTester.get()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.content.length()").isEqualTo(plataformas.size());
                    assertThat(json).extractingPath("$.content[0]")
                            .convertTo(Jugador.class).usingRecursiveComparison().isEqualTo(plataforma1);
                    assertThat(json).extractingPath("$.content[1]")
                            .convertTo(Jugador.class).usingRecursiveComparison().isEqualTo(plataforma2);
                });

        // Verify
        verify(jugadorService, times(1))
                .findAll(Optional.empty(), Optional.empty(), pageable);
    }

    @Test
    void getAllByNombre() {
        // Arrange
        var plataformas = List.of(plataforma2);
        String queryString = "?nombre=" + plataforma2.getNombre();
        Optional<String> nombre = Optional.of(plataforma2.getNombre());
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(plataformas);
        when(jugadorService.findAll(nombre, Optional.empty(), pageable)).thenReturn(page);

        // Act
        var result = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.content.length()").isEqualTo(plataformas.size());
                    assertThat(json).extractingPath("$.content[0]")
                            .convertTo(Jugador.class).usingRecursiveComparison().isEqualTo(plataforma2);
                });

        // Verify
        verify(jugadorService, times(1))
                .findAll(nombre, Optional.empty(), pageable);
    }

    @Test
    void getById() {
        // Arrange
        Long id = plataforma1.getId();
        when(jugadorService.findById(id)).thenReturn(plataforma1);

        // Act
        var result = mockMvcTester.get()
                .uri(ENDPOINT + "/" + id.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .convertTo(Jugador.class).usingRecursiveComparison().isEqualTo(plataforma1);

        // Verify
        verify(jugadorService, only()).findById(anyLong());

    }

    @Test
    void getById_shouldThrowPlataformaNotFound_whenInvalidIdProvided() {
        // Arrange
        Long id = 3L;
        when(jugadorService.findById(anyLong())).thenThrow(new JugadorNotFoundException(id));

        // Act
        var result = mockMvcTester.get()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
                .hasStatus4xxClientError()
                // throws TarjetaNotFoundException
                .hasFailed().failure()
                .isInstanceOf(JugadorNotFoundException.class)
                .hasMessageContaining("Plataforma con id: " + id + " no encontrada");

        // Verify
        verify(jugadorService, only()).findById(anyLong());

    }

    @Test
    void create() {
        // Arrange
        String requestBody = """
           {
              "nombre": "Manuela"
           }
           """;

        var plataformaSaved = Jugador.builder()
                .id(1L)
                .nombre("Manuela")
                .build();

        when(jugadorService.save(any(JugadorCreatedDto.class))).thenReturn(plataformaSaved);

        // Act
        var result = mockMvcTester.post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(Jugador.class)
                .usingRecursiveComparison()
                .isEqualTo(plataformaSaved);

        verify(jugadorService, only()).save(any(JugadorCreatedDto.class));


    }


    @Test
    void create_whenBadRequest() {
        // Arrange
        String requestBody = """
           {
              "nombre": "r"
           }
           """;

        // Act
        var result = mockMvcTester.post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .hasPathSatisfying("$.errores", path ->
                        assertThat(path).hasFieldOrProperty("nombre"));


        verify(jugadorService, never()).save(any(JugadorCreatedDto.class));

    }

    @Test
    void create_whenNombreExists() {
        // Arrange
        String requestBody = """
           {
              "nombre": "Jose"
           }
           """;

        when(jugadorService.save(any(JugadorCreatedDto.class)))
                .thenThrow(new JugadorConflictException("Ya existe un plataforma con el nombre Jose"));


        // Act
        var result = mockMvcTester.post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.CONFLICT)
                // throws plataformasConflictEsception
                .hasFailed().failure()
                .isInstanceOf(JugadorConflictException.class)
                .hasMessageContaining("Ya existe un plataforma");


        verify(jugadorService, only()).save(any(JugadorCreatedDto.class));
    }

    @Test
    void update() {
        // Arrange
        Long id = 1L;
        String requestBody = """
           {
              "nombre": "JOSE"
           }
           """;

        var plataformaSaved = Jugador.builder()
                .id(1L)
                .nombre("JOSE")
                .build();

        when(jugadorService.update(anyLong(), any(JugadorUpdateDto.class))).thenReturn(plataformaSaved);

        // Act
        var result = mockMvcTester.put()
                .uri(ENDPOINT+ "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .convertTo(Jugador.class)
                .usingRecursiveComparison()
                .isEqualTo(plataformaSaved);

        verify(jugadorService, only()).update(anyLong(), any(JugadorUpdateDto.class));
    }

    @Test
    void update_shouldThrowPlataformaNotFound() {
        // Arrange
        Long id = 2L;
        String requestBody = """
           {
              "nombre": "Mario"
           }
           """;
        when(jugadorService.update(anyLong(), any(JugadorUpdateDto.class))).thenThrow(new JugadorNotFoundException(id));

        // Act
        var result = mockMvcTester.put()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.NOT_FOUND)
                // throws TarjetaNotFoundException
                .hasFailed().failure()
                .isInstanceOf(JugadorNotFoundException.class)
                .hasMessageContaining("Plataforma con id: " + id + " no encontrada");

        // Verify
        verify(jugadorService, only()).update(anyLong(), any());
    }

    @Test
    void update_shouldThrowBadRequest() {
        // Arrange
        Long id = 2L;
        String requestBody = """
           {
              "nombre": "fa"
           }
           """;

        // Act
        var result = mockMvcTester.put()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .hasPathSatisfying("$.errores", path ->
                        assertThat(path).hasFieldOrProperty("nombre"));


        verify(jugadorService, never()).update(anyLong(), any(JugadorUpdateDto.class));
    }

    @Test
    void update_whenNombreExists() {
        // Arrange
        Long id = 1L;
        String requestBody = """
           {
              "nombre": "Jose"
           }
           """;

        when(jugadorService.update(anyLong(), any(JugadorUpdateDto.class)))
                .thenThrow(new JugadorConflictException("Ya existe un plataforma con el nombre Jose"));


        // Act
        var result = mockMvcTester.put()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.CONFLICT)
                // throws plataformasConflictEsception
                .hasFailed().failure()
                .isInstanceOf(JugadorConflictException.class)
                .hasMessageContaining("Ya existe un plataforma");

        verify(jugadorService, only()).update(anyLong(), any(JugadorUpdateDto.class));
    }

    @Test
    void delete() {
        // Arrange
        Long id = 1L;
        doNothing().when(jugadorService).deleteById(anyLong());
        // Act
        var result = mockMvcTester.delete()
                .uri(ENDPOINT+ "/" + id)
                .exchange();
        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.NO_CONTENT);

        verify(jugadorService, only()).deleteById(anyLong());
    }

    @Test
    void delete_shouldThrowPlataformaaNotFound() {
        // Arrange
        Long id = 1L;
        doThrow(new JugadorNotFoundException(id)).when(jugadorService).deleteById(anyLong());
        // Act
        var result = mockMvcTester.delete()
                .uri(ENDPOINT+ "/" + id)
                .exchange();
        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.NOT_FOUND);

        verify(jugadorService, only()).deleteById(anyLong());
    }



}
