package es.juanjsts.rest.videojuegos.services;

import es.juanjsts.rest.videojuegos.dto.VideojuegoCreateDto;
import es.juanjsts.rest.videojuegos.dto.VideojuegoResponseDto;
import es.juanjsts.rest.videojuegos.dto.VideojuegoUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface VideojuegosService {
    Page<VideojuegoResponseDto> findAll(Optional<String> nombre, Optional<String> plataforma, Optional<Boolean> isDeleted, Pageable pageable);

    VideojuegoResponseDto findById(Long id);

    VideojuegoResponseDto findByUuid(String uuid);

    Page<VideojuegoResponseDto> findByUsuarioId(Long usuarioId, Pageable pageable);
    VideojuegoResponseDto findByUsuarioId(Long usuarioId, Long idVideojuego);

    VideojuegoResponseDto save(VideojuegoCreateDto videojuegocreateDto);
    VideojuegoResponseDto save(VideojuegoCreateDto videojuegoCreateDto, Long usuarioId);

    VideojuegoResponseDto update(Long id, VideojuegoUpdateDto videojuegoupdateDto);
    VideojuegoResponseDto update(Long id, VideojuegoUpdateDto videojuegoupdateDto, Long usuarioId);

    void deleteById(Long id);
    void deleteById(Long id, Long usuarioId);
}
