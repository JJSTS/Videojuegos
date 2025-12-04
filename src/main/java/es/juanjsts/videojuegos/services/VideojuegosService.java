package es.juanjsts.videojuegos.services;

import es.juanjsts.videojuegos.dto.VideojuegoCreateDto;
import es.juanjsts.videojuegos.dto.VideojuegoResponseDto;
import es.juanjsts.videojuegos.dto.VideojuegoUpdateDto;
import es.juanjsts.videojuegos.models.Videojuego;

import java.util.List;

public interface VideojuegosService {
    List<VideojuegoResponseDto> findAll(String nombre, String plataforma);

    VideojuegoResponseDto findById(Long id);

    VideojuegoResponseDto findByUuid(String uuid);

    VideojuegoResponseDto save(VideojuegoCreateDto videojuegocreateDto);

    VideojuegoResponseDto update(Long id, VideojuegoUpdateDto videojuegoupdateDto);

    void deleteById(Long id);
}
