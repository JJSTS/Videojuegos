package es.juanjsts.rest.videojuegos.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.juanjsts.config.websockets.WebSocketConfig;
import es.juanjsts.config.websockets.WebSocketHandler;
import es.juanjsts.rest.jugadores.models.Jugador;
import es.juanjsts.rest.jugadores.repositories.JugadorRepository;
import es.juanjsts.rest.jugadores.services.JugadorService;
import es.juanjsts.rest.videojuegos.dto.VideojuegoCreateDto;
import es.juanjsts.rest.videojuegos.dto.VideojuegoResponseDto;
import es.juanjsts.rest.videojuegos.dto.VideojuegoUpdateDto;
import es.juanjsts.rest.videojuegos.exceptions.VideojuegoBadRequestException;
import es.juanjsts.rest.videojuegos.exceptions.VideojuegoBadUuidException;
import es.juanjsts.rest.videojuegos.exceptions.VideojuegoNotFoundException;
import es.juanjsts.rest.videojuegos.mappers.VideojuegoMapper;
import es.juanjsts.rest.videojuegos.models.Videojuego;
import es.juanjsts.rest.videojuegos.repositories.VideojuegosRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@CacheConfig(cacheNames = {"videojuegos"})
@Slf4j
@Service
public class VideojuegoServiceImpl implements VideojuegosService, InitializingBean {
    private final VideojuegosRepository videojuegoRepository;
    private final VideojuegoMapper videojuegoMapper;
    private final JugadorService jugadorService;
    private final JugadorRepository jugadorRepository;

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
    public Page<VideojuegoResponseDto> findAll(Optional<String> nombre, Optional<String> jugador, Optional<Boolean> isDeleted, Pageable pageable) {
        log.info("Buscando videojuegos por nombre: {}, genero: {}, isDeleted: {}", nombre, jugador, isDeleted);
        //Criterio de búsqueda por nombre
        Specification<Videojuego> specNombreVideojuego = (root, query, criteriaBuilder) ->
                nombre.map(n -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + n.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true))); //Si no hay nombre, no filtramos

        //Criterio de búsqueda por jugador
        Specification<Videojuego> specJugadorVideojuego = (root, query, criteriaBuilder) ->
                jugador.map(p -> {
                    Join<Videojuego, Jugador> JugadorJoin = root.join("jugador");
                    return criteriaBuilder.like(criteriaBuilder.lower(JugadorJoin.get("nombre")), "%" + p.toLowerCase() + "%");

                }).orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true))); //Si no hay jugador, no filtramos

        //Criterio de búsqueda por isDeleted
        Specification<Videojuego> specIsDeleted = (root, query, criteriaBuilder) ->
                isDeleted.map(d -> criteriaBuilder.equal(root.get("isDeleted"), d))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        //Combinamos las especificaciones
        Specification<Videojuego> criterio = Specification.allOf(specNombreVideojuego, specJugadorVideojuego, specIsDeleted);

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

    @Override
    public Page<VideojuegoResponseDto> findByUsuarioId(Long usuarioId, Pageable pageable) {
        log.info("Buscando videojuegos del usuario por id {}", usuarioId);
        return videojuegoRepository.findByUsuarioId(usuarioId, pageable)
                .map(videojuegoMapper::toVideojuegoResponseDto);
    }

    @Override
    public VideojuegoResponseDto findByUsuarioId(Long usuarioId, Long idVideojuego) {
        log.info("Obteniendo videojuego del usuario por id: {}", usuarioId);
        var videojuego = videojuegoRepository.findByUsuarioId(usuarioId);
        var videojuegoEncontrado = videojuego.stream().filter(v -> v.getId().equals(idVideojuego))
                .findFirst().orElse(null);
        if (videojuegoEncontrado == null) {
            throw new VideojuegoBadRequestException("El videojuego " + idVideojuego + " no pertenece a este usuario");
        }
        return videojuegoMapper.toVideojuegoResponseDto(videojuegoEncontrado);
    }

    private Jugador checkJugador(String nombreJugador){
        log.info("Buscando videojuego por nombre: {}", nombreJugador);
        var jugador = jugadorRepository.findByNombreEqualsIgnoreCase(nombreJugador);
        if (jugador.isEmpty() || jugador.get().getIsDeleted()){
            throw new VideojuegoBadRequestException("La jugador " + nombreJugador + " no existe o está borrado");
        }
        return jugador.get();
    }

    @CachePut(key = "#result.id")
    @Override
    public VideojuegoResponseDto save(VideojuegoCreateDto videojuegocreateDto) {
        log.info("Guardando videojuego: {}", videojuegocreateDto);
        Jugador jugador = checkJugador(videojuegocreateDto.getJugador());
        Videojuego nuevoVideojuego = videojuegoRepository.save(
                videojuegoMapper.toVideojuego(videojuegocreateDto, jugador));
        onChange(Notificacion.Tipo.CREATE, nuevoVideojuego);
        return videojuegoMapper.toVideojuegoResponseDto(nuevoVideojuego);
    }

    @CachePut(key = "#result.id")
    @Override
    public VideojuegoResponseDto save(VideojuegoCreateDto videojuegoCreateDto, Long usuarioId){
        log.info("Guardando videojuego: {} de usuarioId: {}", videojuegoCreateDto, usuarioId);
        Jugador jugador = checkJugador(videojuegoCreateDto.getJugador());
        var usuario = jugador.getUsuario();
        if ((usuario != null) && (!usuario.getId().equals(usuarioId))){
            throw new VideojuegoBadRequestException("La usuario no se corresponde con la jugador");
        }
        Videojuego nuevoVideojuego = videojuegoRepository.save(
                videojuegoMapper.toVideojuego(videojuegoCreateDto, jugador));
        onChange(Notificacion.Tipo.CREATE, nuevoVideojuego);
        return videojuegoMapper.toVideojuegoResponseDto(nuevoVideojuego);
    }

    @CachePut(key = "#result.id")
    @Override
    public VideojuegoResponseDto update(Long id, VideojuegoUpdateDto videojuegoupdateDto) {
        log.info("Actualizando videojuego con id: {} con videojuego: {}", id, videojuegoupdateDto);
        var videojuegoActual = videojuegoRepository.findById(id)
                .orElseThrow(()-> new VideojuegoNotFoundException(id));

        Videojuego videojuegoActualizado = videojuegoRepository.save(
                videojuegoMapper.toVideojuego(videojuegoupdateDto, videojuegoActual)
        );

        onChange(Notificacion.Tipo.UPDATE, videojuegoActualizado);
        return videojuegoMapper.toVideojuegoResponseDto(videojuegoActualizado);
    }

    @CachePut(key = "#result.id")
    @Override
    public VideojuegoResponseDto update(Long id, VideojuegoUpdateDto videojuegoupdateDto, Long usuarioId){
        log.info("Actualizando videojuego por id: {}", id);
        var videojuegoActual = videojuegoRepository.findById(id).orElseThrow(()-> new VideojuegoNotFoundException(id));
        var usuario = videojuegoActual.getJugador().getUsuario();
        if ((usuario != null) && (!usuario.getId().equals(usuarioId))){
            throw new VideojuegoBadRequestException("El videojuego " + videojuegoupdateDto.getNombre() + " no pertenece al usuario");
        }
        Videojuego videojuegoUpdated = videojuegoRepository.save(
                videojuegoMapper.toVideojuego(videojuegoupdateDto, videojuegoActual));
        onChange(Notificacion.Tipo.UPDATE, videojuegoUpdated);
        return videojuegoMapper.toVideojuegoResponseDto(videojuegoUpdated);
    }

    @CacheEvict(key = "#id")
    @Override
    public void deleteById(Long id) {
        log.debug("Eliminando videojuego con id: {}", id);
        Videojuego videojuegoDeleted = videojuegoRepository.findById(id).orElseThrow(()-> new VideojuegoNotFoundException(id));
        videojuegoRepository.deleteById(id);
        onChange(Notificacion.Tipo.DELETE, videojuegoDeleted);
    }

    @CacheEvict(key = "#id")
    @Override
    public void deleteById(Long id, Long usuarioId){
        log.debug("Eliminando videojuego con id: {}", id);
        Videojuego videojuegoDeleted = videojuegoRepository.findById(id).orElseThrow(()-> new VideojuegoNotFoundException(id));
        var usuario = videojuegoDeleted.getJugador().getUsuario();
        if ((usuario != null) && (!usuario.getId().equals(usuarioId))){
            throw new VideojuegoBadRequestException("El videojuego " + id + " no pertenece al usuario");
        }
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

    @Override
    public List<Videojuego> buscarPorUsuarioId(Long usuarioId) {
      return videojuegoRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public Optional<Videojuego> buscarPorId(Long id) {
      return videojuegoRepository.findById(id);
    }
}
