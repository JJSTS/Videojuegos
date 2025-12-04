package es.juanjsts.plataformas.controllers;

import es.juanjsts.plataformas.dto.PlataformaCreatedDto;
import es.juanjsts.plataformas.dto.PlataformaUpdateDto;
import es.juanjsts.plataformas.exceptions.PlataformaConflictException;
import es.juanjsts.plataformas.exceptions.PlataformaNotFoundException;
import es.juanjsts.plataformas.models.Plataforma;
import es.juanjsts.plataformas.services.PlataformaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class PlataformaRestControllerTest {
    private final String ENDPOINT = "/api/v1/plataformas";
    
    private final Plataforma plataforma1 = Plataforma.builder()
            .id(1L)
            .nombre("Nintendo")
            .fabricante("Nintendo")
            .tipo("PC")
            .fechaDeLanzamiento(LocalDate.of(1985, 1, 1))
            .build();
    private final Plataforma plataforma2 = Plataforma.builder()
            .id(2L)
            .nombre("PlayStation")
            .fabricante("Sony")
            .tipo("Consolas")
            .fechaDeLanzamiento(LocalDate.of(1995, 1, 1))
            .build();
    
    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private PlataformaService plataformaService;

    @Test
    void getAll() {
        // Arrange
        var plataformaes = List.of(plataforma1, plataforma2);
        when(plataformaService.findAll(null)).thenReturn(plataformaes);

        // Act. Consultar el endpoint
        var result = mockMvcTester.get()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(plataformaes.size());
                    assertThat(json).extractingPath("$[0]")
                            .convertTo(Plataforma.class).usingRecursiveComparison().isEqualTo(plataforma1);
                    assertThat(json).extractingPath("$[1]")
                            .convertTo(Plataforma.class).usingRecursiveComparison().isEqualTo(plataforma2);
                });

        // Verify
        verify(plataformaService, times(1)).findAll(null);
    }

    @Test
    void getAllByNombre() {
        // Arrange
        var plataformaes = List.of(plataforma2);
        String queryString = "?nombre=" + plataforma2.getNombre();
        when(plataformaService.findAll(anyString())).thenReturn(plataformaes);

        // Act
        var result = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(plataformaes.size());
                    assertThat(json).extractingPath("$[0]")
                            .convertTo(Plataforma.class).usingRecursiveComparison().isEqualTo(plataforma2);
                });

        // Verify
        verify(plataformaService, times(1)).findAll(anyString());
    }

    @Test
    void getById() {
        // Arrange
        Long id = plataforma1.getId();
        when(plataformaService.findById(id)).thenReturn(plataforma1);

        // Act
        var result = mockMvcTester.get()
                .uri(ENDPOINT + "/" + id.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .convertTo(Plataforma.class).usingRecursiveComparison().isEqualTo(plataforma1);

        // Verify
        verify(plataformaService, only()).findById(anyLong());

    }

    @Test
    void getById_shouldThrowPlataformaNotFound_whenInvalidIdProvided() {
        // Arrange
        Long id = 3L;
        when(plataformaService.findById(anyLong())).thenThrow(new PlataformaNotFoundException(id));

        // Act
        var result = mockMvcTester.get()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
                .hasStatus4xxClientError()
                // throws TarjetaNotFoundException
                .hasFailed().failure()
                .isInstanceOf(PlataformaNotFoundException.class)
                .hasMessageContaining("Plataforma con id: " + id + " no encontrada");

        // Verify
        verify(plataformaService, only()).findById(anyLong());

    }

    @Test
    void create() {
        // Arrange
        String requestBody = """
           {
              "nombre": "Manuela"
           }
           """;

        var plataformaSaved = Plataforma.builder()
                .id(1L)
                .nombre("manuela")
                .build();

        when(plataformaService.save(any(PlataformaCreatedDto.class))).thenReturn(plataformaSaved);

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
                .convertTo(Plataforma.class)
                .usingRecursiveComparison()
                .isEqualTo(plataformaSaved);

        verify(plataformaService, only()).save(any(PlataformaCreatedDto.class));


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


        verify(plataformaService, never()).save(any(PlataformaCreatedDto.class));

    }

    @Test
    void create_whenNombreExists() {
        // Arrange
        String requestBody = """
           {
              "nombre": "Jose"
           }
           """;

        when(plataformaService.save(any(PlataformaCreatedDto.class)))
                .thenThrow(new PlataformaConflictException("Ya existe un plataforma con el nombre Jose"));


        // Act
        var result = mockMvcTester.post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.CONFLICT)
                // throws PlataformaesConflictEsception
                .hasFailed().failure()
                .isInstanceOf(PlataformaConflictException.class)
                .hasMessageContaining("Ya existe un plataforma");


        verify(plataformaService, only()).save(any(PlataformaCreatedDto.class));
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

        var plataformaSaved = Plataforma.builder()
                .id(1L)
                .nombre("JOSE")
                .build();

        when(plataformaService.update(anyLong(), any(PlataformaUpdateDto.class))).thenReturn(plataformaSaved);

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
                .convertTo(Plataforma.class)
                .usingRecursiveComparison()
                .isEqualTo(plataformaSaved);

        verify(plataformaService, only()).update(anyLong(), any(PlataformaUpdateDto.class));
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
        when(plataformaService.update(anyLong(), any(PlataformaUpdateDto.class))).thenThrow(new PlataformaNotFoundException(id));

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
                .isInstanceOf(PlataformaNotFoundException.class)
                .hasMessageContaining("Plataforma con id: " + id + " no encontrada");

        // Verify
        verify(plataformaService, only()).update(anyLong(), any());
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


        verify(plataformaService, never()).update(anyLong(), any(PlataformaUpdateDto.class));
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

        when(plataformaService.update(anyLong(), any(PlataformaUpdateDto.class)))
                .thenThrow(new PlataformaConflictException("Ya existe un plataforma con el nombre Jose"));


        // Act
        var result = mockMvcTester.put()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.CONFLICT)
                // throws PlataformaesConflictEsception
                .hasFailed().failure()
                .isInstanceOf(PlataformaConflictException.class)
                .hasMessageContaining("Ya existe un plataforma");

        verify(plataformaService, only()).update(anyLong(), any(PlataformaUpdateDto.class));
    }

    @Test
    void delete() {
        // Arrange
        Long id = 1L;
        doNothing().when(plataformaService).deleteById(anyLong());
        // Act
        var result = mockMvcTester.delete()
                .uri(ENDPOINT+ "/" + id)
                .exchange();
        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.NO_CONTENT);

        verify(plataformaService, only()).deleteById(anyLong());
    }

    @Test
    void delete_shouldThrowPlataformaaNotFound() {
        // Arrange
        Long id = 1L;
        doThrow(new PlataformaNotFoundException(id)).when(plataformaService).deleteById(anyLong());
        // Act
        var result = mockMvcTester.delete()
                .uri(ENDPOINT+ "/" + id)
                .exchange();
        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.NOT_FOUND);

        verify(plataformaService, only()).deleteById(anyLong());
    }



}