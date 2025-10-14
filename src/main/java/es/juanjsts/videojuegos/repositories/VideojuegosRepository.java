package es.juanjsts.videojuegos.repositories;

import es.juanjsts.videojuegos.models.Videojuego;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VideojuegosRepository {
    List <Videojuego> findAll();

    List<Videojuego> findAllByNombre(String nombre);

    List<Videojuego> findAllByGenero(String genero);

    List<Videojuego> findAllByNombreAndGenero(String nombre, String genero);

    Optional<Videojuego> findById(Long id);

    Optional<Videojuego> findByUuid(UUID uuid);

    boolean existsById(Long id);

    boolean existsByUuid(UUID uuid);

    Videojuego save(Videojuego videojuego);

    void deleteById(Long id);

    void deleteByUuid(UUID uuid);

    Long nextId();
}
