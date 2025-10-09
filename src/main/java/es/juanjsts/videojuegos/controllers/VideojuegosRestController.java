package es.juanjsts.videojuegos.controllers;


import es.juanjsts.videojuegos.models.Videojuego;
import es.juanjsts.videojuegos.repositories.VideojuegosRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/${api.version}/videojuegos")
@RestController
public class VideojuegosRestController {
    private final VideojuegosRepositoryImpl videojuegoRepository;

    @Autowired
    public VideojuegosRestController(VideojuegosRepositoryImpl videojuegoRepository) {
        this.videojuegoRepository = videojuegoRepository;
    }

    @GetMapping()
    public ResponseEntity<List<Videojuego>> getAllVideojuegos(@RequestParam(required = false) String nombre) {
        if (nombre != null){
            return ResponseEntity.ok(videojuegoRepository.findByNombre(nombre));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(videojuegoRepository.findAll());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Videojuego> getVideojuegoById(@PathVariable Long id){
        return videojuegoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping()
    public ResponseEntity<Videojuego> createVideojuego(@RequestBody Videojuego videojuego){
        var saved = videojuegoRepository.save(videojuego);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Videojuego> updateVideojuego(@PathVariable Long id, @RequestBody Videojuego videojuego){
        return videojuegoRepository.findById(id)
                .map(p -> {
                    var updated = videojuegoRepository.save(videojuego);
                    return ResponseEntity.status(HttpStatus.CREATED).body(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Videojuego> updatePartialVideojuego(@PathVariable Long id, @RequestBody Videojuego videojuego){
        return videojuegoRepository.findById(id)
                .map(p -> {
                    var updated = videojuegoRepository.save(videojuego);
                    return ResponseEntity.status(HttpStatus.CREATED).body(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        return videojuegoRepository.findById(id)
                .map(p -> {
                    videojuegoRepository.deleteById(id);
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
