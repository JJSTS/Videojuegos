package es.juanjsts.plataformas.services;

import es.juanjsts.plataformas.dto.PlataformaCreatedDto;
import es.juanjsts.plataformas.dto.PlataformaUpdateDto;
import es.juanjsts.plataformas.exceptions.PlataformaException;
import es.juanjsts.plataformas.exceptions.PlataformaNotFound;
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
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"plataformas"})
@Service
public class PlataformaServiceImpl implements PlataformaService{
    private final PlataformaRepository plataformaRepository;
    private final PlataformaMapper plataformaMapper;

    @Override
    public List<Plataforma> findAll(String nombre) {
        log.info("Buscando todas las plataformas por nombre: {}", nombre);
        if (nombre == null || nombre.isEmpty()){
            return plataformaRepository.findAll();
        } else {
            return plataformaRepository.findByNombreContainingIgnoreCase(nombre);
        }
    }

    @Override
    @Cacheable(key = "#nombre")
    public Plataforma findByNombre(String nombre) {
        return plataformaRepository.findByNombreEqualsIgnoreCase(nombre)
                .orElseThrow(() -> new PlataformaNotFound(nombre));
    }

    @Override
    @Cacheable(key = "#id")
    public Plataforma findById(Long id) {
        log.info("Buscando plataforma con id: {}", id);
        return plataformaRepository.findById(id)
                .orElseThrow(() -> new PlataformaNotFound(id));
    }



    @Override
    @CachePut(key = "#result.id")
    public Plataforma save(PlataformaCreatedDto plataforma) {
        log.info("Guardando plataforma: {}", plataforma);
        return plataformaRepository.save(plataformaMapper.toPlataforma(plataforma));
    }

    @Override
    @CachePut(key = "#result.id")
    public Plataforma update(Long id, PlataformaUpdateDto plataforma) {
        Plataforma plataformaActual = findById(id);
        return plataformaRepository.save(plataformaMapper.toPlataforma(plataforma, plataformaActual));
    }

    @Override
    @CacheEvict(key = "#id")
    @Transactional
    public void delete(Long id) {
        log.info("Eliminando plataforma con id: {}", id);
        Plataforma plataforma = findById(id);
        if (plataformaRepository.existsVideojuegoById(id)){
            String mensaje = "No se puede eliminar la plataforma con id: " + id + " porque esta siendo utilizada";
            log.warn(mensaje);
            throw new PlataformaException(mensaje);
        } else {
            plataformaRepository.deleteById(id);

        }
    }
}
