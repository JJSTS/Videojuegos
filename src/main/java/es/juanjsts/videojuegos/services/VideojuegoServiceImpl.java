package es.juanjsts.videojuegos.services;

import es.juanjsts.plataformas.services.PlataformaService;
import es.juanjsts.videojuegos.dto.VideojuegoCreateDto;
import es.juanjsts.videojuegos.dto.VideojuegoResponseDto;
import es.juanjsts.videojuegos.dto.VideojuegoUpdateDto;
import es.juanjsts.videojuegos.exceptions.VideojuegoNotFoundException;
import es.juanjsts.videojuegos.mappers.VideojuegoMapper;
import es.juanjsts.videojuegos.models.Videojuego;
import es.juanjsts.videojuegos.repositories.VideojuegosRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@CacheConfig(cacheNames = {"videojuegos"})
@Slf4j
@Service
public class VideojuegoServiceImpl implements VideojuegosService {
    private final VideojuegosRepository videojuegoRepository;
    private final VideojuegoMapper videojuegoMapper;
    private final PlataformaService plataformaService;

    @Override
    public List<VideojuegoResponseDto> findAll(String nombre, String genero) {
        if ((nombre == null || nombre.isEmpty()) && (genero == null || genero.isEmpty())){
            log.info("Buscando todos los videojuegos");
            return videojuegoMapper.toResponseDtoList(videojuegoRepository.findAll());
        }

        if ((nombre == null || !nombre.isEmpty()) && (genero == null || genero.isEmpty())){
            log.info("Buscando videojuegos por nombre: {}", nombre);
            return videojuegoMapper.toResponseDtoList(videojuegoRepository.findAllByNombre(nombre));
        }

        if (nombre == null || nombre.isEmpty()){
            log.info("Buscando videojuegos por genero: {}", genero);
            return videojuegoMapper.toResponseDtoList(videojuegoRepository.findAllByGeneroContainingIgnoreCase(genero));
        }

        log.info("Buscando videojuegos por nombre: {} y genero: {}", nombre, genero);
        return videojuegoMapper.toResponseDtoList(videojuegoRepository.findAllByNombreAndGeneroContainingIgnoreCase(nombre, genero));
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
                    .orElseThrow(()-> new VideojuegoNotFoundException(uuid)));

        } catch (IllegalArgumentException e){
            throw new VideojuegoNotFoundException(uuid);
        }
    }

    @CachePut(key = "#result.id")
    @Override
    public VideojuegoResponseDto save(VideojuegoCreateDto videojuegocreateDto) {
        log.info("Guardando videojuego: {}", videojuegocreateDto);
        var plataforma = plataformaService.findByNombre(videojuegocreateDto.getNombre());
        Videojuego nuevoVideojuego = videojuegoMapper.toVideojuego(videojuegocreateDto, plataforma);
        return videojuegoMapper.toVideojuegoResponseDto(videojuegoRepository.save(nuevoVideojuego));
    }

    @CachePut(key = "#result.id")
    @Override
    public VideojuegoResponseDto update(Long id, VideojuegoUpdateDto videojuegoupdateDto) {
        log.info("Actualizando videojuego con id: {} con videojuego: {}", id, videojuegoupdateDto);
        var videojuegoActual = videojuegoRepository.findById(id)
                .orElseThrow(()-> new VideojuegoNotFoundException(id));

        Videojuego videojuegoActualizado = videojuegoMapper.toVideojuego(videojuegoupdateDto, videojuegoActual);

        return videojuegoMapper.toVideojuegoResponseDto(videojuegoRepository.save(videojuegoActualizado));
    }

    @CacheEvict(key = "#id")
    @Override
    public void deleteById(Long id) {
        log.info("Eliminando videojuego con id: {}", id);
        videojuegoRepository.findById(id).orElseThrow(()-> new VideojuegoNotFoundException(id));
        videojuegoRepository.deleteById(id);
    }
}
