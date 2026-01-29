package es.juanjsts.plataformas.repositories;

import es.juanjsts.rest.jugadores.models.Jugador;
import es.juanjsts.rest.jugadores.repositories.JugadorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Reseteamos la base de datos para partir de una situación conocida
@Sql(value = {"/reset.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DataJpaTest
class JugadorRepositoryTest {

    private final Jugador plataforma = Jugador.builder()
            .nombre("Nintendo")
            .fabricante("Nintendo")
            .tipo("PC")
            .fechaDeLanzamiento(LocalDate.of(1985, 1, 1))
            .build();

    @Autowired
    private JugadorRepository repositorio;
    @Autowired
    private TestEntityManager entityManager; // EntityManager para hacer las pruebas

    @BeforeEach
    void setUp() {
        // Insertamos un plataforma antes de cada test
        entityManager.persist(plataforma);
        // Sincroniza los cambios en los objetos del contexto de persistencia con la BD
        entityManager.flush();
    }

    @Test
    void findAll() {
        // Act
        List<Jugador> jugador = repositorio.findAll();

        // Assert
        assertAll("findAll",
                () -> assertNotNull(jugador),
                () -> assertFalse(jugador.isEmpty())
        );
    }

    @Test
    void findByNombre() {
        // Act
        List<Jugador> jugador = repositorio.findByNombreContainingIgnoreCase("Nintendo");

        // Assert
        assertAll("findAllByNombre",
                () -> assertNotNull(jugador),
                () -> assertFalse(jugador.isEmpty()),
                () -> assertEquals("Nintendo", jugador.getFirst().getNombre())
        );
    }

    @Test
    void findById() {
        // Act
        Jugador jugador = repositorio.findById(1L).orElse(null);

        // Assert
        assertAll("findById",
                () -> assertNotNull(jugador),
                () -> assertEquals("Nintendo", jugador.getNombre())
        );
    }

    @Test
    void findByIdNotFound() {
        // Act
        Jugador jugador = repositorio.findById(100L).orElse(null);

        // Assert
        assertNull(jugador);
    }

    @Test
    void save() {
        // Act
        Jugador jugador = repositorio.save(Jugador.builder()
                .nombre("Epic Games Store")
                .fabricante("Epic Games")
                .tipo("PC")
                .fechaDeLanzamiento(LocalDate.of(1985, 1, 1))
                .build());

        // Assert
        assertAll("save",
                () -> assertNotNull(jugador),
                () -> assertEquals("Epic Games Store", jugador.getNombre()),
                () -> assertEquals("Epic Games", jugador.getFabricante()),
                () -> assertEquals("PC", jugador.getTipo()),
                () -> assertEquals(LocalDate.of(1985, 1, 1), jugador.getFechaDeLanzamiento())
        );
    }

    @Test
    void update() {
        // Act
        var plataformaExistente = repositorio.findById(1L).orElse(null);
        Jugador jugadorActualizar = Jugador.builder()
                .id(plataformaExistente.getId())
                .nombre("Pepe").build();
        Jugador jugadorActualizado = repositorio.save(jugadorActualizar);

        // Assert
        assertAll("update",
                () -> assertNotNull(jugadorActualizado),
                () -> assertEquals("Pepe", jugadorActualizado.getNombre())
        );
    }

    @Test
    void delete() {
        // Act
        var plataformaBorrar = repositorio.findById(1L).orElse(null);
        repositorio.delete(plataformaBorrar);
        Jugador jugadorBorrado = repositorio.findById(1L).orElse(null);

        // Assert
        assertNull(jugadorBorrado);
    }

    // Para comprobar la diferencia entre usar FetchType.EAGER o LAZY en la relación de plataforma con tarjetas
    // hay que añadir o quitar fetch = FetchType.EAGER a la anotación @OneToMany.  No se puede hacer por código.
    // En este tipo de relación la opción por defecto es LAZY.
    @Test
    void test_FetchType_EAGER_vs_LAZY() {
        // Vacía la cache del contexto de persistencia (L1 Cache) para poder ver todas
        // las consultas a la BD en la consola
        entityManager.clear();

        Jugador jugador = repositorio.findById(1L).orElse(null);
        assertNotNull(jugador);
    }

}
