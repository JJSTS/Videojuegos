package es.juanjsts.plataformas.repositories;

import es.juanjsts.rest.plataformas.models.Plataforma;
import es.juanjsts.rest.plataformas.repositories.PlataformaRepository;
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
class PlataformaRepositoryTest {

    private final Plataforma plataforma = Plataforma.builder()
            .nombre("Nintendo")
            .fabricante("Nintendo")
            .tipo("PC")
            .fechaDeLanzamiento(LocalDate.of(1985, 1, 1))
            .build();

    @Autowired
    private PlataformaRepository repositorio;
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
        List<Plataforma> plataforma = repositorio.findAll();

        // Assert
        assertAll("findAll",
                () -> assertNotNull(plataforma),
                () -> assertFalse(plataforma.isEmpty())
        );
    }

    @Test
    void findByNombre() {
        // Act
        List<Plataforma> plataforma = repositorio.findByNombreContainingIgnoreCase("Nintendo");

        // Assert
        assertAll("findAllByNombre",
                () -> assertNotNull(plataforma),
                () -> assertFalse(plataforma.isEmpty()),
                () -> assertEquals("Nintendo", plataforma.getFirst().getNombre())
        );
    }

    @Test
    void findById() {
        // Act
        Plataforma plataforma = repositorio.findById(1L).orElse(null);

        // Assert
        assertAll("findById",
                () -> assertNotNull(plataforma),
                () -> assertEquals("Nintendo", plataforma.getNombre())
        );
    }

    @Test
    void findByIdNotFound() {
        // Act
        Plataforma plataforma = repositorio.findById(100L).orElse(null);

        // Assert
        assertNull(plataforma);
    }

    @Test
    void save() {
        // Act
        Plataforma plataforma = repositorio.save(Plataforma.builder()
                .nombre("Epic Games Store")
                .fabricante("Epic Games")
                .tipo("PC")
                .fechaDeLanzamiento(LocalDate.of(1985, 1, 1))
                .build());

        // Assert
        assertAll("save",
                () -> assertNotNull(plataforma),
                () -> assertEquals("Epic Games Store", plataforma.getNombre()),
                () -> assertEquals("Epic Games", plataforma.getFabricante()),
                () -> assertEquals("PC", plataforma.getTipo()),
                () -> assertEquals(LocalDate.of(1985, 1, 1), plataforma.getFechaDeLanzamiento())
        );
    }

    @Test
    void update() {
        // Act
        var plataformaExistente = repositorio.findById(1L).orElse(null);
        Plataforma plataformaActualizar = Plataforma.builder()
                .id(plataformaExistente.getId())
                .nombre("Pepe").build();
        Plataforma plataformaActualizado = repositorio.save(plataformaActualizar);

        // Assert
        assertAll("update",
                () -> assertNotNull(plataformaActualizado),
                () -> assertEquals("Pepe", plataformaActualizado.getNombre())
        );
    }

    @Test
    void delete() {
        // Act
        var plataformaBorrar = repositorio.findById(1L).orElse(null);
        repositorio.delete(plataformaBorrar);
        Plataforma plataformaBorrado = repositorio.findById(1L).orElse(null);

        // Assert
        assertNull(plataformaBorrado);
    }

    // Para comprobar la diferencia entre usar FetchType.EAGER o LAZY en la relación de plataforma con tarjetas
    // hay que añadir o quitar fetch = FetchType.EAGER a la anotación @OneToMany.  No se puede hacer por código.
    // En este tipo de relación la opción por defecto es LAZY.
    @Test
    void test_FetchType_EAGER_vs_LAZY() {
        // Vacía la cache del contexto de persistencia (L1 Cache) para poder ver todas
        // las consultas a la BD en la consola
        entityManager.clear();

        Plataforma plataforma = repositorio.findById(1L).orElse(null);
        assertNotNull(plataforma);
    }

}