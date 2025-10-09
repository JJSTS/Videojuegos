package es.juanjsts.videojuegos.repositories;

import es.juanjsts.videojuegos.models.Videojuego;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class VideojuegosRepositoryImpl implements VideojuegosRepository {
    private final Map<Long, Videojuego> videojuegos = new LinkedHashMap<>(
            Map.of(
            1L,new Videojuego(1L, "Among us", "3.0 GB", LocalDate.of(2018,8,8),2.99,
                    LocalDateTime.now(), LocalDateTime.now(), UUID.randomUUID()),
            2L,new Videojuego(2L, "Fortnite", "15.0 GB", LocalDate.of(2019,10,14),0.00,
                    LocalDateTime.now(), LocalDateTime.now(), UUID.randomUUID()),
            3L,new Videojuego(3L, "Leage of Legens", "25.0 GB", LocalDate.of(2015,11,15),0.00,
                    LocalDateTime.now(), LocalDateTime.now(), UUID.randomUUID())
    ));

    @Override
    public List<Videojuego> findAll() {
        return videojuegos.values().stream().toList();
    }

    @Override
    public List<Videojuego> findByNombre(String nombre) {
        return videojuegos.values().stream()
                .filter(videojuego -> videojuego.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .toList();
    }

    @Override
    public Optional<Videojuego> findById(Long id) {
        return videojuegos.get(id) != null  ? Optional.of(videojuegos.get(id)) : Optional.empty();
    }

    @Override
    public Optional<Videojuego> findByUuid(UUID uuid) {
        return videojuegos.values().stream()
                .filter(videojuego -> videojuego.getUuid().equals(uuid))
                .findFirst();
    }

    @Override
    public boolean existsById(Long id) {
        return videojuegos.get(id) != null;
    }

    @Override
    public boolean existsByUuid(UUID uuid) {
        return videojuegos.values().stream()
                .anyMatch(videojuego -> videojuego.getUuid().equals(uuid));
    }

    @Override
    public Videojuego save(Videojuego videojuego) {
        if (videojuego.getId() != null && existsById(videojuego.getId())) {
            return update(videojuego);
        } else {
            return create(videojuego);
        }
    }

    private Videojuego create(Videojuego videojuego) {
        Long id = videojuegos.keySet().stream()
                .mapToLong(value -> value)
                .max()
                .orElse(0) + 1;

        Videojuego nuevoVideojuego = new Videojuego(id, videojuego.getNombre(),
                videojuego.getAlmacenamiento(), videojuego.getFechaDeCreacion(),
                videojuego.getCosto(), LocalDateTime.now(), LocalDateTime.now(), UUID.randomUUID());

        videojuegos.put(id, nuevoVideojuego);
        return nuevoVideojuego;
    }

    private Videojuego update(Videojuego videojuego) {
        Videojuego videojuegoActual = videojuegos.get(videojuego.getId());

        Videojuego videojuegoActualizado = new Videojuego(
                videojuegoActual.getId(),
                videojuego.getNombre() != null ? videojuego.getNombre() : videojuegoActual.getNombre(),
                videojuego.getAlmacenamiento() != null ? videojuego.getAlmacenamiento() : videojuegoActual.getAlmacenamiento(),
                videojuego.getFechaDeCreacion() != null ? videojuego.getFechaDeCreacion() : videojuegoActual.getFechaDeCreacion(),
                videojuego.getCosto() != null ? videojuego.getCosto() : videojuegoActual.getCosto(),
                videojuegoActual.getCreatedAt(),
                LocalDateTime.now(),
                videojuegoActual.getUuid()
        );

        videojuegos.put(videojuego.getId(), videojuegoActualizado);

        return videojuegoActualizado;
    }


    @Override
    public void deleteById(Long id) {
        videojuegos.remove(id);
    }

    @Override
    public void deleteByUuid(UUID uuid) {
        videojuegos.values().removeIf(videojuego -> videojuego.getUuid().equals(uuid));
    }
}
