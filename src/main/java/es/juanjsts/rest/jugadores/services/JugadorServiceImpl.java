package es.juanjsts.rest.jugadores.services;

import es.juanjsts.rest.jugadores.dto.JugadorCreatedDto;
import es.juanjsts.rest.jugadores.dto.JugadorUpdateDto;
import es.juanjsts.rest.jugadores.exceptions.JugadorConflictException;
import es.juanjsts.rest.jugadores.exceptions.JugadorNotFoundException;
import es.juanjsts.rest.jugadores.mappers.JugadorMapper;
import es.juanjsts.rest.jugadores.models.Jugador;
import es.juanjsts.rest.jugadores.repositories.JugadorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"jugadores"})
@Service
public class JugadorServiceImpl implements JugadorService {
    private final JugadorRepository jugadorRepository;
    private final JugadorMapper jugadorMapper;

    @Override
    public Page<Jugador> findAll(Optional<String> nombre, Optional<Boolean> isDeleted, Pageable pageable) {
        log.info("Buscando todas las jugadores por nombre: {}, isDeleted {}", nombre, isDeleted);
        Specification<Jugador> specNombreJugador = (root, query, criteriaBuilder) ->
                nombre.map(n -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + n.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Jugador> specIsDeleted = (root, query, criteriaBuilder) ->
                isDeleted.map(d -> criteriaBuilder.equal(root.get("isDeleted"), d))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));
        Specification<Jugador> criterio = Specification.allOf(specNombreJugador, specIsDeleted);
        return jugadorRepository.findAll(criterio, pageable);
    }

    @Override
    @Cacheable(key = "#nombre")
    public Jugador findByNombre(String nombre) {
        return jugadorRepository.findByNombreEqualsIgnoreCase(nombre)
                .orElseThrow(() -> new JugadorNotFoundException(nombre));
    }

    @Override
    @Cacheable(key = "#id")
    public Jugador findById(Long id) {
        log.info("Buscando jugador con id: {}", id);
        return jugadorRepository.findById(id)
                .orElseThrow(() -> new JugadorNotFoundException(id));
    }

    @Override
    @CachePut(key = "#result.id")
    public Jugador save(JugadorCreatedDto jugador) {
        log.info("Guardando jugador: {}", jugador);
        jugadorRepository.findByNombreEqualsIgnoreCase(jugador.getNombre()).ifPresent(tit -> {
            throw new JugadorConflictException("Ya existe un jugador con el nombre: " + jugador.getNombre());
        });
        return jugadorRepository.save(jugadorMapper.toJugador(jugador));
    }

    @Override
    @CachePut(key = "#result.id")
    public Jugador update(Long id, JugadorUpdateDto jugador) {
        Jugador jugadorActual = findById(id);
        jugadorRepository.findByNombreEqualsIgnoreCase(jugador.getNombre()).ifPresent(tit -> {
            if (!tit.getId().equals(id)){
                throw new JugadorConflictException("Ya existe un jugador con el nombre: " + jugador.getNombre());
            }
        });
        return jugadorRepository.save(jugadorMapper.toJugador(jugador, jugadorActual));
    }

    @Override
    @CacheEvict(key = "#id")
    @Transactional
    public void deleteById(Long id) {
        log.info("Eliminando jugador con id: {}", id);
        if (jugadorRepository.existsVideojuegoById(id)){
            String mensaje = "No se puede eliminar la jugador con id: " + id + " porque esta siendo utilizada";
            log.warn(mensaje);
            throw new JugadorConflictException(mensaje);
        } else {
            jugadorRepository.deleteById(id);

        }
    }
}
