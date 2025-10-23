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
        return Videojuego.builder()
                .id(id).nombre(videojuegoCreateDto.getNombre())
                .genero(videojuegoCreateDto.getGenero())
                .almacenamiento(videojuegoCreateDto.getAlmacenamiento())
                .fechaDeCreacion(videojuegoCreateDto.getFechaDeCreacion())
                .costo(videojuegoCreateDto.getCosto())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(UUID.randomUUID())
                .build();
    }

    public Videojuego toVideojuego(VideojuegoUpdateDto videojuegoUpdateDto, Videojuego videojuegoActual) {
        return Videojuego.builder()
                .id(videojuegoActual.getId())
                .nombre(videojuegoActual.getNombre() != null ? videojuegoUpdateDto.getNombre() : videojuegoActual.getNombre())
                .genero(videojuegoActual.getGenero() != null ? videojuegoUpdateDto.getGenero() : videojuegoActual.getGenero())
                .almacenamiento(videojuegoActual.getAlmacenamiento() != null ? videojuegoUpdateDto.getAlmacenamiento() : videojuegoActual.getAlmacenamiento())
                .fechaDeCreacion(videojuegoActual.getFechaDeCreacion() != null ? videojuegoUpdateDto.getFechaDeCreacion() : videojuegoActual.getFechaDeCreacion())
                .costo(videojuegoActual.getCosto() != null ? videojuegoUpdateDto.getCosto() : videojuegoActual.getCosto())
                .createdAt(videojuegoActual.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .uuid(videojuegoActual.getUuid())
                .build();
    }

    public VideojuegoResponseDto toVideojuegoResponseDto(Videojuego videojuego) {
        return VideojuegoResponseDto.builder()
                .id(videojuego.getId())
                .nombre(videojuego.getNombre())
                .genero(videojuego.getGenero())
                .almacenamiento(videojuego.getAlmacenamiento())
                .fechaDeCreacion(videojuego.getFechaDeCreacion())
                .costo(videojuego.getCosto())
                .createdAt(videojuego.getCreatedAt())
                .updatedAt(videojuego.getUpdatedAt())
                .build();
    }

    public List<VideojuegoResponseDto> toResponseDtoList(List<Videojuego> videojuegos) {
        return videojuegos.stream()
                .map(this::toVideojuegoResponseDto)
                .toList();
    }
}
