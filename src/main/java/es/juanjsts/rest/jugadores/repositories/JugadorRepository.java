package es.juanjsts.rest.jugadores.repositories;

import es.juanjsts.rest.jugadores.models.Jugador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JugadorRepository extends JpaRepository<Jugador, Long>, JpaSpecificationExecutor<Jugador> {
    // Encuentra por nombre exacto
    Optional<Jugador> findByNombreEqualsIgnoreCase(String nombre);

    //Plataforma por nombre
    List<Jugador> findByNombreContainingIgnoreCase(String nombre);

    //Actualizar la plataforma con isDeleted a true
    @Modifying //Para indicar que es una consulta de actualización
    @Query("UPDATE Jugador j SET j.isDeleted = true WHERE j.id = :id")
    void updateIsDeletedToTrueById(Long id);

    //Obtiene si existe un videojuego con el id de la plataforma
    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM Videojuego v WHERE v.jugador.id = :id")
    boolean existsVideojuegoById(Long id);


}
