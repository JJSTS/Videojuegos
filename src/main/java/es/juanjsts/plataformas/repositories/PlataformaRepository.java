package es.juanjsts.plataformas.repositories;

import es.juanjsts.plataformas.models.Plataforma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface  PlataformaRepository extends JpaRepository<Plataforma, Long> {
    // Encuentra por nombre exacto
    Optional<Plataforma> findByNombreEqualsIgnoreCase(String nombre);

    //Encuentra por nombre exacto y no eliminado
    Optional<Plataforma> findByNombreEqualsIgnoreCaseAndIsDeletedFalse(String nombre);

    //Plataforma por nombre
    List<Plataforma> findByNombreContainingIgnoreCase(String nombre);

    //Plataforma por fabricante
    List<Plataforma> findAllByFabricanteContainingIgnoreCase(String fabricante);

    List<Plataforma> findAllByNombreAndFabricanteContainingIgnoreCase(String nombre, String fabricante);

    //Plataformas activas
    List<Plataforma> findAllByNombreContainingIgnoreCaseAndIsDeletedFalse(String nombre);

    List<Plataforma> findAllByIsDeleted(Boolean isDeleted);

    //Actualizar la plataforma con isDeleted a true
    @Modifying //Para indicar que es una consulta de actualización
    @Query("UPDATE Plataforma pla SET pla.isDeleted = true WHERE pla.id = :id")
    void updateIsDeletedToTrueById(Long id);

    //Obtiene si existe un videojuego con el id de la plataforma
    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM Videojuego v WHERE v.plataforma.id = :id")
    boolean existsVideojuegoById(Long id);


}
