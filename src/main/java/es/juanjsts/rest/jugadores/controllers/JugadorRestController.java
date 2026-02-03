package es.juanjsts.rest.jugadores.controllers;

import es.juanjsts.rest.jugadores.dto.JugadorCreatedDto;
import es.juanjsts.rest.jugadores.dto.JugadorUpdateDto;
import es.juanjsts.rest.jugadores.models.Jugador;
import es.juanjsts.rest.jugadores.services.JugadorService;
import es.juanjsts.utils.pagination.PageResponse;
import es.juanjsts.utils.pagination.PaginationLinksUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/${api.version}/jugadores")
@RestController
public class JugadorRestController {
    private final JugadorService jugadorService;
    private final PaginationLinksUtils paginationLinksUtils;

    @GetMapping
    public ResponseEntity<PageResponse<Jugador>> getAll(
            @RequestParam(required = false) Optional<String> nombre,
            @RequestParam(required = false) Optional<Boolean> isDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        log.info("Buscando jugadores por nombre: {}, isDeleted {}", nombre, isDeleted);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(request.getRequestURL().toString());
        Page<Jugador> pageResult = jugadorService.findAll(nombre, isDeleted, pageable);
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy,direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Jugador> getById(@PathVariable Long id){
        log.info("Buscando Jugador con id: {}", id);
        return ResponseEntity.ok(jugadorService.findById(id));
    }

    @PostMapping()
    public ResponseEntity<Jugador> create(@Valid @RequestBody JugadorCreatedDto jugadorCreatedDto){
        log.info("Creando jugador: {}", jugadorCreatedDto);
        var saved = jugadorService.save(jugadorCreatedDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Jugador> update(@PathVariable Long id, @Valid @RequestBody JugadorUpdateDto jugadorUpdateDto) {
        log.info("Actualizando jugador con id: {} con jugador: {}", id, jugadorUpdateDto);
        return ResponseEntity.ok(jugadorService.update(id, jugadorUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Borrando jugador por id: {}", id);
        jugadorService.deleteById(id);
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
