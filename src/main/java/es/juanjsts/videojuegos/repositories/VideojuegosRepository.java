package es.juanjsts.videojuegos.repositories;

import es.juanjsts.videojuegos.models.Videojuego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VideojuegosRepository extends JpaRepository<Videojuego, Long> {
    List<Videojuego> findAllByNombre(String nombre);

    List<Videojuego> findAllByGeneroContainingIgnoreCase(String genero);

    List<Videojuego> findAllByNombreAndGeneroContainingIgnoreCase(String nombre, String genero);

    //Por UUID
    Optional<Videojuego> findByUuid(UUID uuid);
    boolean existsByUuid(UUID uuid);
    void deleteByUuid(UUID uuid);

    //En caso esté borrado
    List<Videojuego> findByIsDeleted(Boolean isDeleted);

    // Actualizar la tarjeta con isDeleted a true
    @Modifying // Para indicar que es una consulta de actualización
    @Query("UPDATE Videojuego v SET v.isDeleted = true WHERE v.id = :id")

    // Consulta de actualización
    void updateIsDeletedToTrueById(Long id);

}
