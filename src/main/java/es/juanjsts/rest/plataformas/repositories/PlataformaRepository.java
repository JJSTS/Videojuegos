package es.juanjsts.rest.plataformas.repositories;

import es.juanjsts.rest.plataformas.models.Plataforma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface  PlataformaRepository extends JpaRepository<Plataforma, Long>, JpaSpecificationExecutor<Plataforma> {
    // Encuentra por nombre exacto
    Optional<Plataforma> findByNombreEqualsIgnoreCase(String nombre);

    //Plataforma por nombre
    List<Plataforma> findByNombreContainingIgnoreCase(String nombre);

    //Actualizar la plataforma con isDeleted a true
    @Modifying //Para indicar que es una consulta de actualización
    @Query("UPDATE Plataforma pla SET pla.isDeleted = true WHERE pla.id = :id")
    void updateIsDeletedToTrueById(Long id);

    //Obtiene si existe un videojuego con el id de la plataforma
    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM Jugador v WHERE v.plataforma.id = :id")
    boolean existsVideojuegoById(Long id);


}
