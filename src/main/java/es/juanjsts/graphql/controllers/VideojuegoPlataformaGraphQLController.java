package es.juanjsts.graphql.controllers;

import es.juanjsts.rest.jugadores.models.Jugador;
import es.juanjsts.rest.jugadores.repositories.JugadorRepository;
import es.juanjsts.rest.videojuegos.models.Videojuego;
import es.juanjsts.rest.videojuegos.repositories.VideojuegosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class VideojuegoPlataformaGraphQLController {
    private final VideojuegosRepository videojuegoRepository;
    private final JugadorRepository jugadorRepository;

    // --QUERIES--
    @QueryMapping
    public List<Videojuego> videojuegos() {
        return videojuegoRepository.findAll();
    }

    @QueryMapping
    public Videojuego videojuegoById(@Argument Long id) {
        Optional<Videojuego> videojuegoOpt = videojuegoRepository.findById(id);
        return videojuegoOpt.orElse(null);
    }

    @QueryMapping
    public List<Jugador> plataformas() {
        return jugadorRepository.findAll();
    }


    @QueryMapping
    public Jugador plataformaById(@Argument Long id) {
        return jugadorRepository.findById(id).orElse(null);
    }

    @QueryMapping
    public List<Jugador> plataformaByNombre(@Argument String nombre){
        return jugadorRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @SchemaMapping(typeName = "Videojuego", field = "plataforma")
    public Jugador plataforma (Videojuego videojuego){
        return videojuego.getJugador();
    }

    @SchemaMapping(typeName = "Jugador", field = "videojuego")
    public List<Videojuego> videojuegos (Jugador jugador){
        return videojuegoRepository.findByJugador(jugador);
    }
}
