package es.juanjsts.videojuegos.services;

import es.juanjsts.videojuegos.models.Videojuego;

import java.util.List;

public interface VideojuegosService {
    List<Videojuego> findAll(String nombre, String genero);

    Videojuego findById(Long id);

    Videojuego findByUuid(String uuid);

    Videojuego save(Videojuego videojuego);

    Videojuego update(Long id, Videojuego videojuego);

    void deleteById(Long id);
}
