package es.juanjsts.videojuegos.repositories;

import es.juanjsts.videojuegos.models.Videojuego;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Repository
public class VideojuegosRepositoryImpl implements VideojuegosRepository {
    private final Map<Long, Videojuego> videojuegos = new LinkedHashMap<>(
            Map.of(
            1L,Videojuego.builder()
                            .id(1L)
                            .nombre("Among us")
                            .genero("Party")
                            .almacenamiento("3.0 GB")
                            .fechaDeCreacion(LocalDate.of(2018,8,8))
                            .costo(2.99)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .uuid(UUID.randomUUID())
                            .build(),
            2L, Videojuego.builder()
                            .id(2L)
                            .nombre("Fortnite")
                            .genero("Battle Royale")
                            .almacenamiento("15.0 GB")
                            .fechaDeCreacion(LocalDate.of(2019,10,14))
                            .costo(0.00)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .uuid(UUID.randomUUID())
                            .build(),
            3L,Videojuego.builder()
                            .id(3L)
                            .nombre("League of Legends").
                            genero("MOBA")
                            .almacenamiento("25.0 GB")
                            .fechaDeCreacion(LocalDate.of(2015,11,15))
                            .costo(0.00)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .uuid(UUID.randomUUID())
                            .build()
    ));

    @Override
    public List<Videojuego> findAll() {
        log.info("Buscando todos los videojuegos");
        return videojuegos.values().stream().toList();
    }

    @Override
    public List<Videojuego> findAllByNombre(String nombre) {
        log.info("Buscando videojuegos por nombre: {}", nombre);
        return videojuegos.values().stream()
                .filter(videojuego -> videojuego.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .toList();
    }

    @Override
    public List<Videojuego> findAllByGenero(String genero) {
      log.info("Buscando videojuegos por genero: {}", genero);
      return videojuegos.values().stream()
              .filter(videjuego -> videjuego.getGenero().toLowerCase().contains(genero.toLowerCase()))
              .toList();
    }

    @Override
    public List<Videojuego> findAllByNombreAndGenero(String nombre, String genero) {
        log.info("Buscando videojuegos por nombre: {} y genero: {}", nombre, genero);
        return videojuegos.values().stream()
                .filter(videojuego -> videojuego.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .filter(videojuego -> videojuego.getGenero().toLowerCase().contains(genero.toLowerCase()))
                .toList();
    }

    @Override
    public Optional<Videojuego> findById(Long id) {
        log.info("Buscando videojuego con id: {}", id);
        return videojuegos.get(id) != null  ? Optional.of(videojuegos.get(id)) : Optional.empty();
    }

    @Override
    public Optional<Videojuego> findByUuid(UUID uuid) {
        log.info("Buscando videojuego con uuid: {}", uuid);
        return videojuegos.values().stream()
                .filter(videojuego -> videojuego.getUuid().equals(uuid))
                .findFirst();
    }

    @Override
    public boolean existsById(Long id) {
        log.info("Comprobando si existe videojuego con id: {}", id);
        return videojuegos.get(id) != null;
    }

    @Override
    public boolean existsByUuid(UUID uuid) {
        log.info("Comprobando si existe videojuego con uuid: {}", uuid);
        return videojuegos.values().stream()
                .anyMatch(videojuego -> videojuego.getUuid().equals(uuid));
    }

    @Override
    public Videojuego save(Videojuego videojuego) {
        log.info("Guardando videojuego: {}", videojuego);
        videojuegos.put(videojuego.getId(), videojuego);
        return videojuego;
    }

    @Override
    public void deleteById(Long id) {
        log.info("Eliminando videojuego con id: {}", id);
        videojuegos.remove(id);
    }

    @Override
    public void deleteByUuid(UUID uuid) {
        log.info("Eliminando videojuego con uuid: {}", uuid);
        videojuegos.values().removeIf(videojuego -> videojuego.getUuid().equals(uuid));
    }

    @Override
    public Long nextId() {
        log.debug("Obteniendo siguiente id de videojuego");
        return videojuegos.keySet().stream()
                .mapToLong(value -> value)
                .max()
                .orElse(0L) + 1;
    }
}
