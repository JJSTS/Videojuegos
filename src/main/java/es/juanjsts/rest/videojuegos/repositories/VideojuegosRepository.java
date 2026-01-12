package es.juanjsts.rest.videojuegos.repositories;

import es.juanjsts.rest.plataformas.models.Plataforma;
import es.juanjsts.rest.videojuegos.models.Videojuego;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VideojuegosRepository extends JpaRepository<Videojuego, Long>, JpaSpecificationExecutor<Videojuego> {
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

    @Query("SELECT v FROM Videojuego v WHERE v.plataforma.usuario.id = :usuarioId")
    Page<Videojuego> findByUsuarioId(Long usuarioId, Pageable pageable);

    @Query("SELECT v FROM Videojuego v WHERE v.plataforma.usuario.id = :usuarioId")
    List<Videojuego> findByUsuarioId(Long usuarioId);

    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM Videojuego v WHERE v.plataforma.usuario.id = :id")
    Boolean existsByUsuarioId(Long id);

    List<Videojuego> findByPlataforma(Plataforma plataforma);
}
