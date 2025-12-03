package es.juanjsts.videojuegos.repositories;

import es.juanjsts.videojuegos.models.Videojuego;
import es.juanjsts.videojuegos.services.VideojuegosService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VideojuegosRepositoryImplTest {

    private final Videojuego videojuego1 = Videojuego.builder()
            .id(1L)
            .nombre("Among us")
            .genero("Party")
            .almacenamiento("3.0 GB")
            .fechaDeCreacion(LocalDate.of(2018,8,8))
            .costo(2.99)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.randomUUID())
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
            .uuid(UUID.randomUUID())
            .build();

    private final Videojuego videojuego3 = Videojuego.builder()
            .id(3L)
            .nombre("League of Legends").
            genero("MOBA")
            .almacenamiento("25.0 GB")
            .fechaDeCreacion(LocalDate.of(2015,11,15))
            .costo(0.00)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.randomUUID())
            .build();

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private VideojuegosService videojuegosService;

    @Test
    void findAll() {
        //Atc
        List<Videojuego> videojuegos = repository.findAll();

        //Assert
        assertAll("findAll",
                () -> assertNotNull(videojuegos),
                () -> assertEquals(3, videojuegos.size())
        );
    }

    @Test
    void findAllByNombre() {
        //Atc
        String nombre = "Among us";
        List<Videojuego> videojuegos = repository.findAllByNombre(nombre);

        //Assert
        assertAll("findAllByNombre",
                () -> assertNotNull(videojuegos),
                () -> assertEquals(1, videojuegos.size()),
                () -> assertEquals(nombre, videojuegos.getFirst().getNombre())
        );
    }

    @Test
    void findAllByGenero() {
        //Act
        String genero = "MOBA";
        List<Videojuego> videojuegos = repository.findAllByGenero(genero);

        //Assert
        assertAll("findByGenero",
                () -> assertNotNull(videojuegos),
                () -> assertEquals(1, videojuegos.size()),
                () -> assertEquals(genero, videojuegos.getFirst().getGenero())
        );
    }

    @Test
    void findAllByNombreAndGenero() {
        String nombre = "Among us";
        String genero = "Party";
        List<Videojuego> videojuegos = repository.findAllByNombreAndGenero(nombre, genero);

        //Assert
        assertAll("findAllByNombreAndGenero",
                () -> assertNotNull(videojuegos),
                () -> assertEquals(1, videojuegos.size()),
                () -> assertEquals(nombre, videojuegos.getFirst().getNombre()),
                () -> assertEquals(genero, videojuegos.getFirst().getGenero())
        );
    }

    @Test
    void findById_existingId_returnOptionalWithVideojuego() {
        Long id = 1L;
        Optional<Videojuego> optionaVideojuego = repository.findById(id);

        assertAll("findById_existingId_returnOptionalWithVideojuego",
                () -> assertNotNull(optionaVideojuego),
                () -> assertTrue(optionaVideojuego.isPresent()),
                () -> assertEquals(id, optionaVideojuego.get().getId())
        );
    }

    @Test
    void findById_nonExistingId_returnEmptyOptional() {
        Long id = 100L;
        Optional<Videojuego> optionaVideojuego = repository.findById(id);

        assertAll("findById_nonExistingId_returnEmptyOptional",
                () -> assertNotNull(optionaVideojuego),
                () -> assertTrue(optionaVideojuego.isEmpty())
        );
    }

    @Test
    void findByUuid_existingUuid_returnOptionalWithVideojuego() {
        UUID uuid = videojuego1.getUuid();
        Optional<Videojuego> optionaVideojuego = repository.findByUuid(uuid);

        assertAll("findByUuid_existingUuid_returnOptionalWithVideojuego",
                () -> assertNotNull(optionaVideojuego),
                () -> assertTrue(optionaVideojuego.isPresent()),
                () -> assertEquals(uuid, optionaVideojuego.get().getUuid())
        );
    }

    @Test
    void findByUuid_nonExistingUuid_returnEmptyOptional() {
        UUID uuid = UUID.randomUUID();
        Optional<Videojuego> optionaVideojuego = repository.findByUuid(uuid);

        assertAll("findByUuid_nonExistingUuid_returnEmptyOptional",
                () -> assertNotNull(optionaVideojuego),
                () -> assertTrue(optionaVideojuego.isEmpty())
        );
    }

    @Test
    void existsById_existingId_returnTrue() {
        Long id = 1L;
        boolean exists = repository.existsById(id);

        assertTrue(exists);

    }

    @Test
    void existsById_nonExistingId_returnFalse() {
        Long id = 100L;
        boolean exists = repository.existsById(id);

        assertFalse(exists);
    }

    @Test
    void existsByUuid_existingUuid_returnTrue() {
        UUID uuid = videojuego1.getUuid();
        boolean exists = repository.existsByUuid(uuid);

        assertTrue(exists);

    }

    @Test
    void existsByUuid_nonExistingUuid_returnFalse() {
        UUID uuid = UUID.randomUUID();
        boolean exists = repository.existsByUuid(uuid);

        assertFalse(exists);
    }

    @Test
    void save_notExists() {
        Videojuego videojuego = Videojuego.builder()
                .id(4L)
                .nombre("Valorant")
                .genero("FPS")
                .almacenamiento("10.0 GB")
                .fechaDeCreacion(LocalDate.of(2020,12,12))
                .costo(1.99)
                .build();

        Videojuego savedVideojuego = repository.save(videojuego);
        var all = repository.findAll();

        assertAll("save",
                () -> assertNotNull(savedVideojuego),
                () -> assertEquals(videojuego, savedVideojuego),
                () -> assertEquals(4, all.size())
        );
    }

    @Test
    void save_butExists() {
        Videojuego videojuego = Videojuego.builder().id(1L).build();

        Videojuego savedVideojuego = repository.save(videojuego);
        var all = repository.findAll();

        assertAll("save",
                () -> assertNotNull(savedVideojuego),
                () -> assertEquals(videojuego, savedVideojuego),
                () -> assertEquals(3, all.size())
        );
    }

    @Test
    void deleteById_existingId() {
        Long id = 1L;
        repository.deleteById(id);

        var all = repository.findAll();

        assertAll("deleteById_existingId",
                () -> assertEquals(2, all.size()),
                () -> assertFalse(repository.existsById(id))
        );
    }

    @Test
    void deleteByUuid_existingUuid() {
        UUID uuid = videojuego1.getUuid();
        repository.deleteByUuid(uuid);

        var all = repository.findAll();

        assertAll("deleteByUuid_existingUuid",
                () -> assertEquals(2, all.size()),
                () -> assertFalse(repository.existsByUuid(uuid))
        );
    }

    @Test
    void nextId() {
        Long nextId = repository.nextId();
        var all = repository.findAll();

        assertAll("nextId",
                () -> assertEquals(4L, nextId),
                () -> assertEquals(3, all.size())
        );
    }
}