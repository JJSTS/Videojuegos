package es.juanjsts.rest.jugadores.mappers;

import es.juanjsts.rest.jugadores.dto.JugadorCreatedDto;
import es.juanjsts.rest.jugadores.dto.JugadorUpdateDto;
import es.juanjsts.rest.jugadores.models.Jugador;
import org.springframework.stereotype.Component;

@Component
public class JugadorMapper {
    public Jugador toPlataforma(JugadorCreatedDto jugadorCreatedDto) {
        return Jugador.builder()
                .id(null)
                .nombre(jugadorCreatedDto.getNombre())
                .build();
    }

    public Jugador toPlataforma(JugadorUpdateDto jugadorUpdateDto, Jugador jugadorActual) {
        return Jugador.builder()
                .id(jugadorActual.getId())
                .nombre(jugadorUpdateDto.getNombre() != null ? jugadorUpdateDto.getNombre() : jugadorActual.getNombre())
                .createdAt(jugadorActual.getCreatedAt())
                .isDeleted(jugadorUpdateDto.getIsDeleted() != null ? jugadorUpdateDto.getIsDeleted() : jugadorActual.getIsDeleted())
                .build();
    }
}
