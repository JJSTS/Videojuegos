package es.juanjsts.rest.plataformas.services;

import es.juanjsts.rest.plataformas.dto.PlataformaCreatedDto;
import es.juanjsts.rest.plataformas.dto.PlataformaUpdateDto;
import es.juanjsts.rest.plataformas.models.Plataforma;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PlataformaService {
    Page<Plataforma> findAll(Optional<String> nombre, Optional<Boolean> isDeleted, Pageable pageable);

    Plataforma findById(Long id);

    Plataforma findByNombre(String nombre);

    Plataforma save(PlataformaCreatedDto plataforma);

    Plataforma update(Long id, PlataformaUpdateDto plataforma);

    void deleteById(Long id);
}
