package es.juanjsts.websockets.notifications.dto;

public record VideojuegoNotificationResponse (
    Long id,
    String nombre,
    String genero,
    String almacenamiento,
    String plataforma,
    String fechaDeCreacion,
    Double costo,

    String createdAt,
    String updatedAt,
    String uuid
) {}
