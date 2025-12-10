package es.juanjsts.plataformas.services;

import es.juanjsts.plataformas.dto.PlataformaCreatedDto;
import es.juanjsts.plataformas.dto.PlataformaUpdateDto;
import es.juanjsts.plataformas.exceptions.PlataformaConflictException;
import es.juanjsts.plataformas.exceptions.PlataformaNotFoundException;
import es.juanjsts.plataformas.mappers.PlataformaMapper;
import es.juanjsts.plataformas.models.Plataforma;
import es.juanjsts.plataformas.repositories.PlataformaRepository;
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
@CacheConfig(cacheNames = {"plataformas"})
@Service
public class PlataformaServiceImpl implements PlataformaService{
    private final PlataformaRepository plataformaRepository;
    private final PlataformaMapper plataformaMapper;

    @Override
    public Page<Plataforma> findAll(Optional<String> nombre, Optional<Boolean> isDeleted, Pageable pageable) {
        log.info("Buscando todas las plataformas por nombre: {}, isDeleted {}", nombre, isDeleted);
        Specification<Plataforma> specNombrePlataforma = (root, query, criteriaBuilder) ->
                nombre.map(n -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + n.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Plataforma> specIsDeleted = (root, query, criteriaBuilder) ->
                isDeleted.map(d -> criteriaBuilder.equal(root.get("isDeleted"), d))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));
        Specification<Plataforma> criterio = Specification.allOf(specNombrePlataforma, specIsDeleted);
        return plataformaRepository.findAll(criterio, pageable);
    }

    @Override
    @Cacheable(key = "#nombre")
    public Plataforma findByNombre(String nombre) {
        return plataformaRepository.findByNombreEqualsIgnoreCase(nombre)
                .orElseThrow(() -> new PlataformaNotFoundException(nombre));
    }

    @Override
    @Cacheable(key = "#id")
    public Plataforma findById(Long id) {
        log.info("Buscando plataforma con id: {}", id);
        return plataformaRepository.findById(id)
                .orElseThrow(() -> new PlataformaNotFoundException(id));
    }

    @Override
    @CachePut(key = "#result.id")
    public Plataforma save(PlataformaCreatedDto plataforma) {
        log.info("Guardando plataforma: {}", plataforma);
        plataformaRepository.findByNombreEqualsIgnoreCase(plataforma.getNombre()).ifPresent(tit -> {
            throw new PlataformaConflictException("Ya existe una plataforma con el nombre: " + plataforma.getNombre());
        });
        return plataformaRepository.save(plataformaMapper.toPlataforma(plataforma));
    }

    @Override
    @CachePut(key = "#result.id")
    public Plataforma update(Long id, PlataformaUpdateDto plataforma) {
        Plataforma plataformaActual = findById(id);
        plataformaRepository.findByNombreEqualsIgnoreCase(plataforma.getNombre()).ifPresent(tit -> {
            if (!tit.getId().equals(id)){
                throw new PlataformaConflictException("Ya existe una plataforma con el nombre: " + plataforma.getNombre());
            }
        });
        return plataformaRepository.save(plataformaMapper.toPlataforma(plataforma, plataformaActual));
    }

    @Override
    @CacheEvict(key = "#id")
    @Transactional
    public void deleteById(Long id) {
        log.info("Eliminando plataforma con id: {}", id);
        Plataforma plataforma = findById(id);
        if (plataformaRepository.existsVideojuegoById(id)){
            String mensaje = "No se puede eliminar la plataforma con id: " + id + " porque esta siendo utilizada";
            log.warn(mensaje);
            throw new PlataformaConflictException(mensaje);
        } else {
            plataformaRepository.deleteById(id);

        }
    }
}
