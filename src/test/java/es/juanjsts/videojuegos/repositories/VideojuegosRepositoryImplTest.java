package es.juanjsts.videojuegos.repositories;

import es.juanjsts.videojuegos.models.Videojuego;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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

    private VideojuegosRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new VideojuegosRepositoryImpl();
        repository.save(videojuego1);
        repository.save(videojuego2);
    }

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
    }

    @Test
    void findById() {
    }

    @Test
    void findByUuid() {
    }

    @Test
    void existsById() {
    }

    @Test
    void existsByUuid() {
    }

    @Test
    void save() {
    }

    @Test
    void deleteById() {
    }

    @Test
    void deleteByUuid() {
    }

    @Test
    void nextId() {
    }
}