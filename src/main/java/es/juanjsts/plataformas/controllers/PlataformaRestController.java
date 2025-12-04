package es.juanjsts.plataformas.controllers;

import es.juanjsts.plataformas.dto.PlataformaCreatedDto;
import es.juanjsts.plataformas.dto.PlataformaUpdateDto;
import es.juanjsts.plataformas.models.Plataforma;
import es.juanjsts.plataformas.services.PlataformaService;
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

@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/${api.version}/plataformas")
@RestController
public class PlataformaRestController {
    private final PlataformaService plataformaService;

    @GetMapping
    public ResponseEntity<List<Plataforma>> getAll(@RequestParam(required = false) String nombre) {
        log.info("Buscando plataformas por nombre: {}", nombre);
        return ResponseEntity.ok(plataformaService.findAll(nombre));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Plataforma> getById(@PathVariable Long id){
        log.info("Buscando Plataforma con id: {}", id);
        return ResponseEntity.ok(plataformaService.findById(id));
    }

    @PostMapping()
    public ResponseEntity<Plataforma> create(@Valid @RequestBody PlataformaCreatedDto plataformaCreatedDto){
        log.info("Creando plataforma: {}", plataformaCreatedDto);
        var saved = plataformaService.save(plataformaCreatedDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Plataforma> update(@PathVariable Long id, @Valid @RequestBody PlataformaUpdateDto plataformaUpdateDto) {
        log.info("Actualizando plataforma con id: {} con plataforma: {}", id,plataformaUpdateDto);
        return ResponseEntity.ok(plataformaService.update(id, plataformaUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Borrando plataforma por id: {}", id);
        plataformaService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

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
