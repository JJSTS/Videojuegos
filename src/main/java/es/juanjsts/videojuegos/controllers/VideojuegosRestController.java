package es.juanjsts.videojuegos.controllers;


import es.juanjsts.videojuegos.dto.VideojuegoCreateDto;
import es.juanjsts.videojuegos.dto.VideojuegoResponseDto;
import es.juanjsts.videojuegos.dto.VideojuegoUpdateDto;
import es.juanjsts.videojuegos.services.VideojuegosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/${api.version}/videojuegos")
@RestController
public class VideojuegosRestController {
    private final VideojuegosService videojuegosService;

    @GetMapping()
    public ResponseEntity<List<VideojuegoResponseDto>> getAll(@RequestParam(required = false) String nombre,
                                                              @RequestParam(required = false) String genero) {
        log.info("Buscando videojuegos por nombre: {}, genero: {}", nombre, genero);
        return ResponseEntity.ok(videojuegosService.findAll(nombre, genero));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideojuegoResponseDto> getById(@PathVariable Long id){
        log.info("Buscando videojuego con id: {}", id);
        return ResponseEntity.ok(videojuegosService.findById(id));
    }

    @PostMapping()
    public ResponseEntity<VideojuegoResponseDto> create(@Valid @RequestBody VideojuegoCreateDto videojuegoCreateDto){
        log.info("Creando videojuego: {}", videojuegoCreateDto);
        var saved = videojuegosService.save(videojuegoCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VideojuegoResponseDto> update(@PathVariable Long id, @Valid @RequestBody VideojuegoUpdateDto videojuegoUpdateDto){
        log.info("Actualizando videojuego con id: {} con videojuego: {}", id, videojuegoUpdateDto);
        return ResponseEntity.ok(videojuegosService.update(id, videojuegoUpdateDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<VideojuegoResponseDto> updatePartial(@PathVariable Long id,@Valid @RequestBody VideojuegoUpdateDto videojuegoUpdateDto){
        log.info("Actualizando parcialmente videojuego con id: {} con videojuego: {}", id, videojuegoUpdateDto);
        return ResponseEntity.ok(videojuegosService.update(id, videojuegoUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Eliminando videojuego con id: {}", id);
        videojuegosService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // Para capturar los errores de validación
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        BindingResult result = ex.getBindingResult();
        problemDetail.setDetail("Falló la validación para el objeto='" + result.getObjectName()
                + "'. " + "Núm. errores: " + result.getErrorCount());

        Map<String, String> errores = new HashMap<>();
        result.getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errores.put(fieldName, errorMessage);
        });

        problemDetail.setProperty("errores", errores);
        return problemDetail;
    }
}
