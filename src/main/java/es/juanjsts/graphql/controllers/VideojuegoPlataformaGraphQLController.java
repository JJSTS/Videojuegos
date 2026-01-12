package es.juanjsts.graphql.controllers;

import es.juanjsts.rest.plataformas.models.Plataforma;
import es.juanjsts.rest.plataformas.repositories.PlataformaRepository;
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
    private final PlataformaRepository plataformaRepository;

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
    public List<Plataforma> plataformas() {
        return plataformaRepository.findAll();
    }


    @QueryMapping
    public Plataforma plataformaById(@Argument Long id) {
        return plataformaRepository.findById(id).orElse(null);
    }

    @QueryMapping
    public List<Plataforma> plataformaByNombre(@Argument String nombre){
        return plataformaRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @SchemaMapping(typeName = "Videojuego", field = "plataforma")
    public Plataforma plataforma (Videojuego videojuego){
        return videojuego.getPlataforma();
    }

    @SchemaMapping(typeName = "Plataforma", field = "videojuego")
    public List<Videojuego> videojuegos (Plataforma plataforma){
        return videojuegoRepository.findByPlataforma(plataforma);
    }
}
