package es.juanjsts.rest.jugardores.controllers;


import es.juanjsts.rest.jugardores.dto.JugadorCreateDto;
import es.juanjsts.rest.jugardores.dto.JugadorResponseDto;
import es.juanjsts.rest.jugardores.dto.JugadorUpdateDto;
import es.juanjsts.rest.jugardores.services.JugadorService;
import es.juanjsts.utils.pagination.PageResponse;
import es.juanjsts.utils.pagination.PaginationLinksUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Videojuegos", description = "Endpoint de Videojuegos de nuestra api")
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/${api.version}/videojuegos")
@RestController
public class JugadoresRestController {
    private final JugadorService videojuegosService;
    private final PaginationLinksUtils paginationLinksUtils;

    @Operation(summary = "Obtiene todos los videojuegos", description = "Obtiene una lista de Videojuegos")
    @Parameters({
            @Parameter(name = "nombre", description = "Nombre del videojuego", example = ""),
            @Parameter(name = "plataforma", description = "Plataforma del videojuego", example = ""),
            @Parameter(name = "isDeleted", description = "Si está borrada o no", example = "false"),
            @Parameter(name = "page", description = "Número de página", example = "0"),
            @Parameter(name = "size", description = "Tamaño de la página", example = "10"),
            @Parameter(name = "sortBy", description = "Campo de ordenación", example = "id"),
            @Parameter(name = "direction", description = "Dirección de ordenación", example = "asc")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de Videojuegos")
    })

    // Podemos activar CORS en SecurityConfig de manera centralizada
    // o por método de esta manera
    //@CrossOrigin(origins = "http://mifrontend.es")
    @GetMapping()
    public ResponseEntity<PageResponse<JugadorResponseDto>> getAll(
            @RequestParam(required = false) Optional<String> nombre,
            @RequestParam(required = false) Optional<String> plataforma,
            @RequestParam(required = false) Optional<Boolean> isDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request) {
        log.info("Buscando videojuegos por nombre: {}, plataforma: {}, isDeleted: {}", nombre, plataforma, isDeleted);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(request.getRequestURI().toString());
        Page<JugadorResponseDto> pageResult = videojuegosService.findAll(nombre, plataforma, isDeleted, pageable);
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult,uriBuilder))
                .body(PageResponse.of(pageResult, sortBy,direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JugadorResponseDto> getById(@PathVariable Long id){
        log.info("Buscando videojuego con id: {}", id);
        return ResponseEntity.ok(videojuegosService.findById(id));
    }

    @PostMapping()
    public ResponseEntity<JugadorResponseDto> create(@Valid @RequestBody JugadorCreateDto videojuegoCreateDto){
        log.info("Creando videojuego: {}", videojuegoCreateDto);
        var saved = videojuegosService.save(videojuegoCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JugadorResponseDto> update(@PathVariable Long id, @Valid @RequestBody JugadorUpdateDto videojuegoUpdateDto){
        log.info("Actualizando videojuego con id: {} con videojuego: {}", id, videojuegoUpdateDto);
        return ResponseEntity.ok(videojuegosService.update(id, videojuegoUpdateDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<JugadorResponseDto> updatePartial(@PathVariable Long id, @Valid @RequestBody JugadorUpdateDto videojuegoUpdateDto){
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
