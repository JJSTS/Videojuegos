package es.juanjsts.videojuegos.mappers;

import es.juanjsts.plataformas.mappers.PlataformaMapper;
import es.juanjsts.plataformas.models.Plataforma;
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
    public Videojuego toVideojuego(VideojuegoCreateDto videojuegoCreateDto, Plataforma plataforma) {
        return Videojuego.builder()
                .id(null)
                .nombre(videojuegoCreateDto.getNombre())
                .genero(videojuegoCreateDto.getGenero())
                .almacenamiento(videojuegoCreateDto.getAlmacenamiento())
                .plataforma(plataforma)
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
                .nombre(videojuegoUpdateDto.getNombre() != null ? videojuegoUpdateDto.getNombre() : videojuegoActual.getNombre())
                .genero(videojuegoUpdateDto.getGenero() != null ? videojuegoUpdateDto.getGenero() : videojuegoActual.getGenero())
                .almacenamiento(videojuegoUpdateDto.getAlmacenamiento() != null ? videojuegoUpdateDto.getAlmacenamiento() : videojuegoActual.getAlmacenamiento())
                .plataforma(videojuegoActual.getPlataforma())
                .fechaDeCreacion(videojuegoUpdateDto.getFechaDeCreacion() != null ? videojuegoUpdateDto.getFechaDeCreacion() : videojuegoActual.getFechaDeCreacion())
                .costo(videojuegoUpdateDto.getCosto() != null ? videojuegoUpdateDto.getCosto() : videojuegoActual.getCosto())
                .createdAt(videojuegoActual.getCreatedAt())
                .uuid(videojuegoActual.getUuid())
                .build();
    }

    public VideojuegoResponseDto toVideojuegoResponseDto(Videojuego videojuego) {
        return VideojuegoResponseDto.builder()
                .id(videojuego.getId())
                .nombre(videojuego.getNombre())
                .genero(videojuego.getGenero())
                .almacenamiento(videojuego.getAlmacenamiento())
                .plataforma(videojuego.getPlataforma().getNombre())
                .fechaDeCreacion(videojuego.getFechaDeCreacion())
                .costo(videojuego.getCosto())
                .createdAt(videojuego.getCreatedAt())
                .updatedAt(videojuego.getUpdatedAt())
                .uuid(videojuego.getUuid())
                .build();
    }

    public List<VideojuegoResponseDto> toResponseDtoList(List<Videojuego> videojuegos) {
        return videojuegos.stream()
                .map(this::toVideojuegoResponseDto)
                .toList();
    }
}
