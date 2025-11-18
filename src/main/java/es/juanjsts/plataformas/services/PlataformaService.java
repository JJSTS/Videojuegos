package es.juanjsts.plataformas.services;

import es.juanjsts.plataformas.dto.PlataformaCreatedDto;
import es.juanjsts.plataformas.dto.PlataformaUpdateDto;
import es.juanjsts.plataformas.models.Plataforma;

import java.util.List;

public interface PlataformaService {
    List<Plataforma> findAll(String nombre, String fabricante);

    Plataforma findById(Long id);

    Plataforma findByNombre(String nombre);

    Plataforma save(PlataformaCreatedDto plataforma);

    Plataforma update(Long id, PlataformaUpdateDto plataforma);

    void delete(Long id);
}
