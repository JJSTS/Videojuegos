package es.juanjsts.rest.jugardores.mappers;

import es.juanjsts.rest.plataformas.models.Plataforma;
import es.juanjsts.rest.jugardores.dto.JugadorCreateDto;
import es.juanjsts.rest.jugardores.dto.JugadorResponseDto;
import es.juanjsts.rest.jugardores.dto.JugadorUpdateDto;
import es.juanjsts.rest.jugardores.models.Jugador;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class JugadorMapper {
    public Jugador toVideojuego(JugadorCreateDto videojuegoCreateDto, Plataforma plataforma) {
        return Jugador.builder()
                .id(null)
                .nombre(videojuegoCreateDto.getNombre())
                .genero(videojuegoCreateDto.getGenero())
                .fechaDeCreacion(videojuegoCreateDto.getFechaDeCreacion())
                .almacenamiento(videojuegoCreateDto.getAlmacenamiento())
                .costo(videojuegoCreateDto.getCosto())
                .plataforma(plataforma)
                .uuid(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Jugador toVideojuego(JugadorUpdateDto videojuegoUpdateDto, Jugador videojuegoActual) {
        return Jugador.builder()
                .id(videojuegoActual.getId())
                .nombre(videojuegoUpdateDto.getNombre() != null ? videojuegoUpdateDto.getNombre() : videojuegoActual.getNombre())
                .genero(videojuegoUpdateDto.getGenero() != null ? videojuegoUpdateDto.getGenero() : videojuegoActual.getGenero())
                .fechaDeCreacion(videojuegoUpdateDto.getFechaDeCreacion() != null ? videojuegoUpdateDto.getFechaDeCreacion() : videojuegoActual.getFechaDeCreacion())
                .almacenamiento(videojuegoUpdateDto.getAlmacenamiento() != null ? videojuegoUpdateDto.getAlmacenamiento() : videojuegoActual.getAlmacenamiento())
                .costo(videojuegoUpdateDto.getCosto() != null ? videojuegoUpdateDto.getCosto() : videojuegoActual.getCosto())
                .plataforma(videojuegoActual.getPlataforma())
                .createdAt(videojuegoActual.getCreatedAt())
                .uuid(videojuegoActual.getUuid())
                .build();
    }

    public JugadorResponseDto toVideojuegoResponseDto(Jugador videojuego) {
        return JugadorResponseDto.builder()
                .id(videojuego.getId())
                .nombre(videojuego.getNombre())
                .genero(videojuego.getGenero())
                .fechaDeCreacion(videojuego.getFechaDeCreacion())
                .almacenamiento(videojuego.getAlmacenamiento())
                .costo(videojuego.getCosto())
                .plataforma(videojuego.getPlataforma().getNombre())
                .createdAt(videojuego.getCreatedAt())
                .updatedAt(videojuego.getUpdatedAt())
                .uuid(videojuego.getUuid())
                .build();
    }

    public List<JugadorResponseDto> toResponseDtoList(List<Jugador> videojuegos) {
        return videojuegos.stream()
                .map(this::toVideojuegoResponseDto)
                .toList();
    }

    public Page<JugadorResponseDto> toResponseDtoPage(Page<Jugador> videojuegos) {
        return videojuegos.map(this::toVideojuegoResponseDto);
    }
}
