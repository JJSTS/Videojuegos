package es.juanjsts.rest.jugardores.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.juanjsts.config.websockets.WebSocketConfig;
import es.juanjsts.config.websockets.WebSocketHandler;
import es.juanjsts.rest.plataformas.models.Plataforma;
import es.juanjsts.rest.plataformas.repositories.PlataformaRepository;
import es.juanjsts.rest.plataformas.services.PlataformaService;
import es.juanjsts.rest.jugardores.dto.JugadorCreateDto;
import es.juanjsts.rest.jugardores.dto.JugadorResponseDto;
import es.juanjsts.rest.jugardores.dto.JugadorUpdateDto;
import es.juanjsts.rest.jugardores.exceptions.JugadorBadRequestException;
import es.juanjsts.rest.jugardores.exceptions.JugadorBadUuidException;
import es.juanjsts.rest.jugardores.exceptions.JugadorNotFoundException;
import es.juanjsts.rest.jugardores.mappers.JugadorMapper;
import es.juanjsts.rest.jugardores.models.Jugador;
import es.juanjsts.rest.jugardores.repositories.JugadorRepository;
import es.juanjsts.websockets.notifications.dto.VideojuegoNotificationResponse;
import es.juanjsts.websockets.notifications.mappers.VideojuegoNotificationMapper;
import es.juanjsts.websockets.notifications.models.Notificacion;
import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
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
public class JugadorServiceImpl implements JugadorService, InitializingBean {
    private final JugadorRepository videojuegoRepository;
    private final JugadorMapper videojuegoMapper;
    private final PlataformaService plataformaService;
    private final PlataformaRepository plataformaRepository;

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
    public Page<JugadorResponseDto> findAll(Optional<String> nombre, Optional<String> plataforma, Optional<Boolean> isDeleted, Pageable pageable) {
        log.info("Buscando videojuegos por nombre: {}, genero: {}, isDeleted: {}", nombre, plataforma, isDeleted);
        //Criterio de búsqueda por nombre
        Specification<Jugador> specNombreVideojuego = (root, query, criteriaBuilder) ->
                nombre.map(n -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + n.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true))); //Si no hay nombre, no filtramos

        //Criterio de búsqueda por plataforma
        Specification<Jugador> specPlataformaVideojuego = (root, query, criteriaBuilder) ->
                plataforma.map(p -> {
                    Join<Jugador, Plataforma> plataformaJoin = root.join("plataforma");
                    return criteriaBuilder.like(criteriaBuilder.lower(plataformaJoin.get("nombre")), "%" + p.toLowerCase() + "%");

                }).orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true))); //Si no hay plataforma, no filtramos

        //Criterio de búsqueda por isDeleted
        Specification<Jugador> specIsDeleted = (root, query, criteriaBuilder) ->
                isDeleted.map(d -> criteriaBuilder.equal(root.get("isDeleted"), d))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        //Combinamos las especificaciones
        Specification<Jugador> criterio = Specification.allOf(specNombreVideojuego, specPlataformaVideojuego, specIsDeleted);

        return videojuegoRepository.findAll(criterio, pageable)
                .map(videojuegoMapper::toVideojuegoResponseDto);
    }

    @Cacheable(key = "#id")
    @Override
    public JugadorResponseDto findById(Long id) {
        log.info("Buscando tarjeta por id {}", id);
        return videojuegoMapper.toVideojuegoResponseDto(videojuegoRepository.findById(id)
                .orElseThrow(()-> new JugadorNotFoundException(id)));
    }

    @Cacheable(key = "#id")
    @Override
    public JugadorResponseDto findByUuid(String uuid) {
        log.info("Buscando tarjeta por uuid {}", uuid);
        try {
            var myUUID = UUID.fromString(uuid);
            return videojuegoMapper.toVideojuegoResponseDto(videojuegoRepository.findByUuid(myUUID)
                    .orElseThrow(()-> new JugadorNotFoundException(myUUID)));

        } catch (IllegalArgumentException e){
            throw new JugadorBadUuidException(uuid);
        }
    }

    @Override
    public Page<JugadorResponseDto> findByUsuarioId(Long usuarioId, Pageable pageable) {
        log.info("Buscando videojuegos del usuario por id {}", usuarioId);
        return videojuegoRepository.findByUsuarioId(usuarioId, pageable)
                .map(videojuegoMapper::toVideojuegoResponseDto);
    }

    @Override
    public JugadorResponseDto findByUsuarioId(Long usuarioId, Long idVideojuego) {
        log.info("Obteniendo videojuego del usuario por id: {}", usuarioId);
        var videojuego = videojuegoRepository.findByUsuarioId(usuarioId);
        var videojuegoEncontrado = videojuego.stream().filter(v -> v.getId().equals(idVideojuego))
                .findFirst().orElse(null);
        if (videojuegoEncontrado == null) {
            throw new JugadorBadRequestException("El videojuego " + idVideojuego + " no pertenece a este usuario");
        }
        return videojuegoMapper.toVideojuegoResponseDto(videojuegoEncontrado);
    }

    private Plataforma checkPlataforma(String nombrePlataforma){
        log.info("Buscando videojuego por nombre: {}", nombrePlataforma);
        var plataforma = plataformaRepository.findByNombreEqualsIgnoreCase(nombrePlataforma);
        if (plataforma.isEmpty() || plataforma.get().getIsDeleted()){
            throw new JugadorBadRequestException("La plataforma " + nombrePlataforma + " no existe o está borrado");
        }
        return plataforma.get();
    }

    @CachePut(key = "#result.id")
    @Override
    public JugadorResponseDto save(JugadorCreateDto videojuegocreateDto) {
        log.info("Guardando videojuego: {}", videojuegocreateDto);
        Plataforma plataforma = checkPlataforma(videojuegocreateDto.getPlataforma());
        Jugador nuevoVideojuego = videojuegoRepository.save(
                videojuegoMapper.toVideojuego(videojuegocreateDto, plataforma));
        onChange(Notificacion.Tipo.CREATE, nuevoVideojuego);
        return videojuegoMapper.toVideojuegoResponseDto(nuevoVideojuego);
    }

    @CachePut(key = "#result.id")
    @Override
    public JugadorResponseDto save(JugadorCreateDto videojuegoCreateDto, Long usuarioId){
        log.info("Guardando videojuego: {} de usuarioId: {}", videojuegoCreateDto, usuarioId);
        Plataforma plataforma = checkPlataforma(videojuegoCreateDto.getPlataforma());
        var usuario = plataforma.getUsuario();
        if ((usuario != null) && (!usuario.getId().equals(usuarioId))){
            throw new JugadorBadRequestException("La usuario no se corresponde con la plataforma");
        }
        Jugador nuevoVideojuego = videojuegoRepository.save(
                videojuegoMapper.toVideojuego(videojuegoCreateDto, plataforma));
        onChange(Notificacion.Tipo.CREATE, nuevoVideojuego);
        return videojuegoMapper.toVideojuegoResponseDto(nuevoVideojuego);
    }

    @CachePut(key = "#result.id")
    @Override
    public JugadorResponseDto update(Long id, JugadorUpdateDto videojuegoupdateDto) {
        log.info("Actualizando videojuego con id: {} con videojuego: {}", id, videojuegoupdateDto);
        var videojuegoActual = videojuegoRepository.findById(id)
                .orElseThrow(()-> new JugadorNotFoundException(id));

        Jugador videojuegoActualizado = videojuegoRepository.save(
                videojuegoMapper.toVideojuego(videojuegoupdateDto, videojuegoActual)
        );

        onChange(Notificacion.Tipo.UPDATE, videojuegoActualizado);
        return videojuegoMapper.toVideojuegoResponseDto(videojuegoActualizado);
    }

    @CachePut(key = "#result.id")
    @Override
    public JugadorResponseDto update(Long id, JugadorUpdateDto videojuegoupdateDto, Long usuarioId){
        log.info("Actualizando videojuego por id: {}", id);
        var videojuegoActual = videojuegoRepository.findById(id).orElseThrow(()-> new JugadorNotFoundException(id));
        var usuario = videojuegoActual.getPlataforma().getUsuario();
        if ((usuario != null) && (!usuario.getId().equals(usuarioId))){
            throw new JugadorBadRequestException("El videojuego " + videojuegoupdateDto.getNombre() + " no pertenece al usuario");
        }
        Jugador videojuegoUpdated = videojuegoRepository.save(
                videojuegoMapper.toVideojuego(videojuegoupdateDto, videojuegoActual));
        onChange(Notificacion.Tipo.UPDATE, videojuegoUpdated);
        return videojuegoMapper.toVideojuegoResponseDto(videojuegoUpdated);
    }

    @CacheEvict(key = "#id")
    @Override
    public void deleteById(Long id) {
        log.debug("Eliminando videojuego con id: {}", id);
        Jugador videojuegoDeleted = videojuegoRepository.findById(id).orElseThrow(()-> new JugadorNotFoundException(id));
        videojuegoRepository.deleteById(id);
        onChange(Notificacion.Tipo.DELETE, videojuegoDeleted);
    }

    @CacheEvict(key = "#id")
    @Override
    public void deleteById(Long id, Long usuarioId){
        log.debug("Eliminando videojuego con id: {}", id);
        Jugador videojuegoDeleted = videojuegoRepository.findById(id).orElseThrow(()-> new JugadorNotFoundException(id));
        var usuario = videojuegoDeleted.getPlataforma().getUsuario();
        if ((usuario != null) && (!usuario.getId().equals(usuarioId))){
            throw new JugadorBadRequestException("El videojuego " + id + " no pertenece al usuario");
        }
        videojuegoRepository.deleteById(id);
        onChange(Notificacion.Tipo.DELETE, videojuegoDeleted);
    }

    void onChange(Notificacion.Tipo tipo, Jugador data){
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
