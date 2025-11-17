package es.juanjsts.plataformas.controllers;

import es.juanjsts.plataformas.dto.PlataformaCreatedDto;
import es.juanjsts.plataformas.dto.PlataformaResponseDto;
import es.juanjsts.plataformas.dto.PlataformaUpdateDto;
import es.juanjsts.plataformas.models.Plataforma;
import es.juanjsts.plataformas.services.PlataformaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/${api.version}/plataformas")
@RestController
public class PlataformaRestController {
    private final PlataformaService plataformaService;

    @GetMapping
    public ResponseEntity<List<PlataformaResponseDto>> getAll(@RequestParam(required = false) String nombre,
                                                              @RequestParam(required = false) String fabricante) {
        log.info("Buscando plataformas por nombre: {}, fabricante: {}", nombre, fabricante);
        return ResponseEntity.ok(plataformaService.findAll(nombre,fabricante));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Plataforma> getById(@PathVariable Long id){
        log.info("Buscando Plataforma con id: {}", id);
        return ResponseEntity.ok(plataformaService.findById(id));
    }

    @PostMapping()
    public ResponseEntity<PlataformaResponseDto> create(@RequestBody PlataformaCreatedDto plataformaCreatedDto){
        log.info("Creando plataforma: {}", plataformaCreatedDto);
        var saved = plataformaService.save(plataformaCreatedDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlataformaResponseDto> update(@PathVariable Long id,@RequestBody PlataformaUpdateDto plataformaUpdateDto) {
        log.info("Actualizando plataforma: {}", plataformaUpdateDto);
        return ResponseEntity.ok(plataformaService.update(id, plataformaUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Eliminando plataforma con id: {}", id);
        plataformaService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
