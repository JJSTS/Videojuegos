package es.juanjsts.plataformas.services;

import es.juanjsts.plataformas.dto.PlataformaCreatedDto;
import es.juanjsts.plataformas.dto.PlataformaResponseDto;
import es.juanjsts.plataformas.dto.PlataformaUpdateDto;
import es.juanjsts.plataformas.models.Plataforma;

import java.util.List;

public interface PlataformaService {
    List<PlataformaResponseDto> findAll(String nombre, String fabricante);

    Plataforma findById(Long id);

    Plataforma findByNombre(String nombre);

    PlataformaResponseDto save(PlataformaCreatedDto plataforma);

    PlataformaResponseDto update(Long id, PlataformaUpdateDto plataforma);

    void delete(Long id);
}
