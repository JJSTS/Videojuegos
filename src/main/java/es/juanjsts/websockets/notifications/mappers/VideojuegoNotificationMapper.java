package es.juanjsts.websockets.notifications.mappers;

import es.juanjsts.videojuegos.models.Videojuego;
import es.juanjsts.websockets.notifications.dto.VideojuegoNotificationResponse;
import org.springframework.stereotype.Component;

@Component
public class VideojuegoNotificationMapper {
    public VideojuegoNotificationResponse toVideojuegoNotificationDto(Videojuego videojuego) {
        return new VideojuegoNotificationResponse(
                videojuego.getId(),
                videojuego.getNombre(),
                videojuego.getGenero(),
                videojuego.getAlmacenamiento(),
                videojuego.getPlataforma().getNombre(),
                videojuego.getFechaDeCreacion().toString(),
                videojuego.getCosto(),
                videojuego.getCreatedAt().toString(),
                videojuego.getUpdatedAt().toString(),
                videojuego.getUuid().toString()
        );
    }
}
