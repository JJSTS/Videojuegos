package es.juanjsts.videojuegos.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.juanjsts.config.websockets.WebSocketConfig;
import es.juanjsts.config.websockets.WebSocketHandler;
import es.juanjsts.plataformas.models.Plataforma;
import es.juanjsts.plataformas.services.PlataformaService;
import es.juanjsts.videojuegos.dto.VideojuegoCreateDto;
import es.juanjsts.videojuegos.dto.VideojuegoResponseDto;
import es.juanjsts.videojuegos.dto.VideojuegoUpdateDto;
import es.juanjsts.videojuegos.exceptions.VideojuegoBadUuidException;
import es.juanjsts.videojuegos.exceptions.VideojuegoNotFoundException;
import es.juanjsts.videojuegos.mappers.VideojuegoMapper;
import es.juanjsts.videojuegos.models.Videojuego;
import es.juanjsts.videojuegos.repositories.VideojuegosRepository;
import es.juanjsts.websockets.notifications.dto.VideojuegoNotificationResponse;
import es.juanjsts.websockets.notifications.mappers.VideojuegoNotificationMapper;
import es.juanjsts.websockets.notifications.models.Notificacion;
import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.rsocket.RSocketProperties;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@CacheConfig(cacheNames = {"videojuegos"})
@Slf4j
@Service
public class VideojuegoServiceImpl implements VideojuegosService, InitializingBean {
    private final VideojuegosRepository videojuegoRepository;
    private final VideojuegoMapper videojuegoMapper;
    private final PlataformaService plataformaService;

    private final WebSocketConfig webSocketConfig;
    private final ObjectMapper objectMapper;
    private final VideojuegoNotificationMapper videojuegoNotificationMapper;
    private WebSocketHandler webSocketService;

    public void afterPropertiesSet(){
        this.webSocketService = this.webSocketConfig.webSocketVideojuegosHandler();
    }

    public void setWebSocketService(WebSocketHandler webSocketHandler) {
        this.webSocketService = webSocketHandler;
    }

    @Override
    public Page<VideojuegoResponseDto> findAll(Optional<String> nombre, Optional<String> plataforma, Optional<Boolean> isDeleted, Pageable pageable) {
        log.info("Buscando videojuegos por nombre: {}, genero: {}, isDeleted: {}", nombre, plataforma, isDeleted);
        //Criterio de búsqueda por nombre
        Specification<Videojuego> specNombreVideojuego = (root, query, criteriaBuilder) ->
                nombre.map(n -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + n.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true))); //Si no hay nombre, no filtramos

        //Criterio de búsqueda por plataforma
        Specification<Videojuego> specPlataformaVideojuego = (root, query, criteriaBuilder) ->
                plataforma.map(p -> {
                    Join<Videojuego, Plataforma> plataformaJoin = root.join("plataforma");
                    return criteriaBuilder.like(criteriaBuilder.lower(plataformaJoin.get("nombre")), "%" + p.toLowerCase() + "%");

                }).orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true))); //Si no hay plataforma, no filtramos

        //Criterio de búsqueda por isDeleted
        Specification<Videojuego> specIsDeleted = (root, query, criteriaBuilder) ->
                isDeleted.map(d -> criteriaBuilder.equal(root.get("isDeleted"), d))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        //Combinamos las especificaciones
        Specification<Videojuego> criterio = Specification.allOf(specNombreVideojuego, specPlataformaVideojuego, specIsDeleted);

        return videojuegoRepository.findAll(criterio, pageable)
                .map(videojuegoMapper::toVideojuegoResponseDto);
    }

    @Cacheable(key = "#id")
    @Override
    public VideojuegoResponseDto findById(Long id) {
        log.info("Buscando tarjeta por id {}", id);
        return videojuegoMapper.toVideojuegoResponseDto(videojuegoRepository.findById(id)
                .orElseThrow(()-> new VideojuegoNotFoundException(id)));
    }

    @Cacheable(key = "#id")
    @Override
    public VideojuegoResponseDto findByUuid(String uuid) {
        log.info("Buscando tarjeta por uuid {}", uuid);
        try {
            var myUUID = UUID.fromString(uuid);
            return videojuegoMapper.toVideojuegoResponseDto(videojuegoRepository.findByUuid(myUUID)
                    .orElseThrow(()-> new VideojuegoNotFoundException(myUUID)));

        } catch (IllegalArgumentException e){
            throw new VideojuegoBadUuidException(uuid);
        }
    }

    @CachePut(key = "#result.id")
    @Override
    public VideojuegoResponseDto save(VideojuegoCreateDto videojuegocreateDto) {
        log.info("Guardando videojuego: {}", videojuegocreateDto);
        var plataforma = plataformaService.findByNombre(videojuegocreateDto.getPlataforma());
        Videojuego nuevoVideojuego = videojuegoMapper.toVideojuego(videojuegocreateDto, plataforma);
        onChange(Notificacion.Tipo.CREATE, nuevoVideojuego);
        return videojuegoMapper.toVideojuegoResponseDto(videojuegoRepository.save(nuevoVideojuego));
    }

    @CachePut(key = "#result.id")
    @Override
    public VideojuegoResponseDto update(Long id, VideojuegoUpdateDto videojuegoupdateDto) {
        log.info("Actualizando videojuego con id: {} con videojuego: {}", id, videojuegoupdateDto);
        var videojuegoActual = videojuegoRepository.findById(id)
                .orElseThrow(()-> new VideojuegoNotFoundException(id));

        Videojuego videojuegoActualizado = videojuegoMapper.toVideojuego(videojuegoupdateDto, videojuegoActual);

        onChange(Notificacion.Tipo.UPDATE, videojuegoActualizado);
        return videojuegoMapper.toVideojuegoResponseDto(videojuegoRepository.save(videojuegoActualizado));
    }

    @CacheEvict(key = "#id")
    @Override
    public void deleteById(Long id) {
        log.debug("Eliminando videojuego con id: {}", id);
        Videojuego videojuegoDeleted = videojuegoRepository.findById(id).orElseThrow(()-> new VideojuegoNotFoundException(id));
        videojuegoRepository.deleteById(id);
        onChange(Notificacion.Tipo.DELETE, videojuegoDeleted);
    }

    void onChange(Notificacion.Tipo tipo, Videojuego data){
        log.debug("Servicio de productos onChange con tipo: {} y datos: {}", tipo, data);

        if (webSocketService == null){
            log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
            webSocketService = this.webSocketConfig.webSocketVideojuegosHandler();
        }

        try {
            Notificacion<VideojuegoNotificationResponse> notificacion = new Notificacion<>(
                    "VIDEOJUEGOS",
                    tipo,
                    videojuegoNotificationMapper.toVideojuegoNotificationDto(data),
                    LocalDateTime.now().toString()
            );

            String json = objectMapper.writeValueAsString((notificacion));

            log.info("Enviando mensaje a los clientes ws");

            Thread senderThread = new Thread(() -> {
                try{
                    webSocketService.sendMessage(json);
                } catch (Exception e){
                    log.error("Error al enviar el mensaje a través del servicio webSocket", e);
                }
            });

            senderThread.setName("WebSocketVideojuego-" + data.getId());
            senderThread.setDaemon(true);
            senderThread.start();
            log.info("Hilo de websocket iniciado: {}", data.getId());
        } catch (JsonProcessingException e){
            log.error("Error al convertir la notificación a JSON", e);
        }
    }
}
