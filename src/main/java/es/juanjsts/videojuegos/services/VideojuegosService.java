package es.juanjsts.videojuegos.services;

import es.juanjsts.videojuegos.dto.VideojuegoCreateDto;
import es.juanjsts.videojuegos.dto.VideojuegoResponseDto;
import es.juanjsts.videojuegos.dto.VideojuegoUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface VideojuegosService {
    Page<VideojuegoResponseDto> findAll(Optional<String> nombre, Optional<String> plataforma, Optional<Boolean> isDeleted, Pageable pageable);

    VideojuegoResponseDto findById(Long id);

    VideojuegoResponseDto findByUuid(String uuid);

    Page<VideojuegoResponseDto> findByUsuarioId(Long id, Pageable pageable);

    VideojuegoResponseDto save(VideojuegoCreateDto videojuegocreateDto);

    VideojuegoResponseDto update(Long id, VideojuegoUpdateDto videojuegoupdateDto);

    void deleteById(Long id);
}
