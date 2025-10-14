package es.juanjsts.videojuegos.controllers;


import es.juanjsts.videojuegos.models.Videojuego;
import es.juanjsts.videojuegos.services.VideojuegosService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("api/${api.version}/videojuegos")
@RestController
public class VideojuegosRestController {
    private final VideojuegosService videojuegosService;

    @Autowired
    public VideojuegosRestController(VideojuegosService videojuegosService) {
        this.videojuegosService = videojuegosService;
    }

    @GetMapping()
    public ResponseEntity<List<Videojuego>> getAllV(@RequestParam(required = false) String nombre,
                                                    @RequestParam(required = false) String genero) {
        log.info("Buscando videojuegos por nombre: {}, genero: {}", nombre, genero);
        return ResponseEntity.ok(videojuegosService.findAll(nombre, genero));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Videojuego> getVideojuegoById(@PathVariable Long id){
        log.info("Buscando videojuego con id: {}", id);
        return ResponseEntity.ok(videojuegosService.findById(id));
    }

    @PostMapping()
    public ResponseEntity<Videojuego> createVideojuego(@RequestBody Videojuego videojuego){
        var saved = videojuegosService.save(videojuego);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Videojuego> update(@PathVariable Long id, @RequestBody Videojuego videojuego){
        log.info("Actualizando videojuego con id: {} con videojuego: {}", id, videojuego);
        return ResponseEntity.ok(videojuegosService.update(id, videojuego));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Videojuego> updatePartialVideojuego(@PathVariable Long id, @RequestBody Videojuego videojuego){
        log.info("Actualizando parcialmente videojuego con id: {} con videojuego: {}", id, videojuego);
        return ResponseEntity.ok(videojuegosService.update(id, videojuego));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("Eliminando videojuego con id: {}", id);
        videojuegosService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
