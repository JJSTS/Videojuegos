package es.juanjsts.rest.jugadores.services;

import es.juanjsts.rest.jugadores.dto.JugadorCreatedDto;
import es.juanjsts.rest.jugadores.dto.JugadorUpdateDto;
import es.juanjsts.rest.jugadores.models.Jugador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface JugadorService {
    Page<Jugador> findAll(Optional<String> nombre, Optional<Boolean> isDeleted, Pageable pageable);

    Jugador findById(Long id);

    Jugador findByNombre(String nombre);

    Jugador save(JugadorCreatedDto jugador);

    Jugador update(Long id, JugadorUpdateDto jugador);

    void deleteById(Long id);
}
