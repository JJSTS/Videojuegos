package es.juanjsts.videojuegos.repositories;

import es.juanjsts.plataformas.models.Plataforma;
import es.juanjsts.videojuegos.models.Videojuego;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Sql(value = {"/reset.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DataJpaTest
class VideojuegosRepositoryTest {

    private final Plataforma plataforma1 = Plataforma.builder()
            .nombre("Epic Games Store")
            .fabricante("Epic Games")
            .tipo("PC")
            .fechaDeLanzamiento(LocalDate.of(2024,12,24))
            .build();
    private final Plataforma plataforma2 = Plataforma.builder()
            .nombre("PlayStation")
            .fabricante("PlayStation")
            .tipo("Consolas")
            .fechaDeLanzamiento(LocalDate.of(1987,12,12))
            .build();

    private final Videojuego videojuego1 = Videojuego.builder()
            .nombre("Among us")
            .genero("Party")
            .almacenamiento("3.0 GB")
            .fechaDeCreacion(LocalDate.of(2018,8,8))
            .costo(2.99)
            .plataforma(plataforma1)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.randomUUID())
            .build();
    private final Videojuego videojuego2 = Videojuego.builder()
            .nombre("Fortnite")
            .genero("Battle Royale")
            .almacenamiento("15.0 GB")
            .fechaDeCreacion(LocalDate.of(2019,10,14))
            .costo(0.00)
            .plataforma(plataforma2)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.randomUUID())
            .build();

    private final Videojuego videojuego3 = Videojuego.builder()
            .nombre("League of Legends").
            genero("MOBA")
            .almacenamiento("25.0 GB")
            .fechaDeCreacion(LocalDate.of(2015,11,15))
            .costo(0.00)
            .plataforma(plataforma1)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.randomUUID())
            .build();

    @Autowired
    private VideojuegosRepository repositorio;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager.persist(plataforma1);
        entityManager.persist(plataforma2);

        entityManager.persist(videojuego1);
        entityManager.persist(videojuego2);
        entityManager.persist(videojuego3);
        
        entityManager.flush();
    }

    @Test
    void findAll() {
        //Atc
        List<Videojuego> videojuegos = repositorio.findAll();

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
        List<Videojuego> videojuegos = repositorio.findAllByNombre(nombre);

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
        List<Videojuego> videojuegos = repositorio.findAllByGeneroContainingIgnoreCase(genero);

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
        List<Videojuego> videojuegos = repositorio.findAllByNombreAndGeneroContainingIgnoreCase(nombre, genero);

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
        Optional<Videojuego> optionaVideojuego = repositorio.findById(id);

        assertAll("findById_existingId_returnOptionalWithVideojuego",
                () -> assertNotNull(optionaVideojuego),
                () -> assertTrue(optionaVideojuego.isPresent()),
                () -> assertEquals(id, optionaVideojuego.get().getId())
        );
    }

    @Test
    void findById_nonExistingId_returnEmptyOptional() {
        Long id = 100L;
        Optional<Videojuego> optionaVideojuego = repositorio.findById(id);

        assertAll("findById_nonExistingId_returnEmptyOptional",
                () -> assertNotNull(optionaVideojuego),
                () -> assertTrue(optionaVideojuego.isEmpty())
        );
    }

    @Test
    void findByUuid_existingUuid_returnOptionalWithVideojuego() {
        UUID uuid = videojuego1.getUuid();
        Optional<Videojuego> optionaVideojuego = repositorio.findByUuid(uuid);

        assertAll("findByUuid_existingUuid_returnOptionalWithVideojuego",
                () -> assertNotNull(optionaVideojuego),
                () -> assertTrue(optionaVideojuego.isPresent()),
                () -> assertEquals(uuid, optionaVideojuego.get().getUuid())
        );
    }

    @Test
    void findByUuid_nonExistingUuid_returnEmptyOptional() {
        UUID uuid = UUID.randomUUID();
        Optional<Videojuego> optionaVideojuego = repositorio.findByUuid(uuid);

        assertAll("findByUuid_nonExistingUuid_returnEmptyOptional",
                () -> assertNotNull(optionaVideojuego),
                () -> assertTrue(optionaVideojuego.isEmpty())
        );
    }

    @Test
    void existsById_existingId_returnTrue() {
        Long id = 1L;
        boolean exists = repositorio.existsById(id);

        assertTrue(exists);

    }

    @Test
    void existsById_nonExistingId_returnFalse() {
        Long id = 100L;
        boolean exists = repositorio.existsById(id);

        assertFalse(exists);
    }

    @Test
    void existsByUuid_existingUuid_returnTrue() {
        UUID uuid = videojuego1.getUuid();
        boolean exists = repositorio.existsByUuid(uuid);

        assertTrue(exists);

    }

    @Test
    void existsByUuid_nonExistingUuid_returnFalse() {
        UUID uuid = UUID.randomUUID();
        boolean exists = repositorio.existsByUuid(uuid);

        assertFalse(exists);
    }

    @Test
    void save_notExists() {
        Videojuego videojuego = Videojuego.builder()
                .nombre("Valorant")
                .genero("FPS")
                .almacenamiento("10.0 GB")
                .fechaDeCreacion(LocalDate.of(2020,12,12))
                .costo(1.99)
                .plataforma(plataforma2)
                .build();

        Videojuego savedVideojuego = repositorio.save(videojuego);
        var all = repositorio.findAll();

        assertAll("save",
                () -> assertNotNull(savedVideojuego),
                () -> assertEquals(videojuego, savedVideojuego),
                () -> assertEquals(4, all.size())
        );
    }

    @Test
    void save_butExists() {
        Long id = 1L;
        Videojuego videojuego = Videojuego.builder()
                .id(id)
                .nombre("Valorant")
                .genero("Estrategia")
                .almacenamiento("10.0 GB")
                .fechaDeCreacion(LocalDate.of(2020,12,12))
                .costo(1.99)
                .plataforma(plataforma2)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(UUID.randomUUID())
                .build();

        Videojuego savedVideojuego = repositorio.save(videojuego);
        var all = repositorio.findAll();

        assertAll("save",
                () -> assertNotNull(savedVideojuego),
                () -> assertTrue(repositorio.existsById(id)),
                () -> assertTrue(all.size() >= 3)
        );
    }

    @Test
    void deleteById_existingId() {
        Long id = 1L;
        repositorio.deleteById(id);

        var all = repositorio.findAll();

        assertAll("deleteById_existingId",
                () -> assertEquals(2, all.size()),
                () -> assertFalse(repositorio.existsById(id))
        );
    }

    @Test
    void deleteByUuid_existingUuid() {
        UUID uuid = videojuego1.getUuid();
        repositorio.deleteByUuid(uuid);

        var all = repositorio.findAll();

        assertAll("deleteByUuid_existingUuid",
                () -> assertEquals(2, all.size()),
                () -> assertFalse(repositorio.existsByUuid(uuid))
        );
    }
}