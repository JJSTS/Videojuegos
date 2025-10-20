package es.juanjsts.videojuegos.mappers;

import es.juanjsts.videojuegos.dto.VideojuegoCreateDto;
import es.juanjsts.videojuegos.dto.VideojuegoResponseDto;
import es.juanjsts.videojuegos.dto.VideojuegoUpdateDto;
import es.juanjsts.videojuegos.models.Videojuego;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class VideojuegoMapper {
    public Videojuego toVideojuego(Long id, VideojuegoCreateDto videojuegoCreateDto) {
        return new Videojuego(
                id,
                videojuegoCreateDto.getNombre(),
                videojuegoCreateDto.getGenero(),
                videojuegoCreateDto.getAlmacenamiento(),
                videojuegoCreateDto.getFechaDeCreacion(),
                videojuegoCreateDto.getCosto(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                UUID.randomUUID()
        );
    }

    public Videojuego toVideojuego(VideojuegoUpdateDto videojuegoUpdateDto, Videojuego videojuegoActual) {
        return new Videojuego(
                videojuegoActual.getId(),
                videojuegoActual.getNombre() != null ? videojuegoUpdateDto.getNombre() : videojuegoActual.getNombre(),
                videojuegoActual.getGenero() != null ? videojuegoUpdateDto.getGenero() : videojuegoActual.getGenero(),
                videojuegoActual.getAlmacenamiento() != null ? videojuegoUpdateDto.getAlmacenamiento() : videojuegoActual.getAlmacenamiento(),
                videojuegoActual.getFechaDeCreacion() != null ? videojuegoUpdateDto.getFechaDeCreacion() : videojuegoActual.getFechaDeCreacion(),
                videojuegoActual.getCosto() != null ? videojuegoUpdateDto.getCosto() : videojuegoActual.getCosto(),
                videojuegoActual.getCreatedAt(),
                LocalDateTime.now(),
                videojuegoActual.getUuid()
        );
    }

    public VideojuegoResponseDto toVideojuegoResponseDto(Videojuego videojuego) {
        return new VideojuegoResponseDto(
                videojuego.getId(),
                videojuego.getNombre(),
                videojuego.getGenero(),
                videojuego.getAlmacenamiento(),
                videojuego.getFechaDeCreacion(),
                videojuego.getCosto(),
                videojuego.getCreatedAt(),
                videojuego.getUpdatedAt(),
                videojuego.getUuid()
        );
    }

    public List<VideojuegoResponseDto> toResponseDtoList(List<Videojuego> videojuegos) {
        return videojuegos.stream()
                .map(this::toVideojuegoResponseDto)
                .toList();
    }
}
