package es.juanjsts.videojuegos.services;

import es.juanjsts.videojuegos.models.Videojuego;
import es.juanjsts.videojuegos.repositories.VideojuegosRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class VideojuegoServiceImpl implements VideojuegosService {
    private final VideojuegosRepository videojuegoRepository;


    @Autowired
    public VideojuegoServiceImpl(VideojuegosRepository videojuegoRepository) {
        this.videojuegoRepository = videojuegoRepository;
    }

    @Override
    public List<Videojuego> findAll(String nombre, String genero) {
        if ((nombre == null || nombre.isEmpty()) && (genero == null || genero.isEmpty())){
            log.info("Buscando todos los videojuegos");
            return videojuegoRepository.findAll();
        }

        if ((nombre == null || !nombre.isEmpty()) && (genero == null || genero.isEmpty())){
            log.info("Buscando videojuegos por nombre: {}", nombre);
            return videojuegoRepository.findAllByNombre(nombre);
        }

        if (nombre == null || nombre.isEmpty()){
            log.info("Buscando videojuegos por genero: {}", genero);
            return videojuegoRepository.findAllByGenero(genero);
        }

        log.info("Buscando videojuegos por nombre: {} y genero: {}", nombre, genero);
        return videojuegoRepository.findAllByNombreAndGenero(nombre, genero);
    }

    @Override
    public Videojuego findById(Long id) {
        log.info("Buscando videojuego con id: {}", id);
        return videojuegoRepository.findById(id).orElse(null);
    }

    @Override
    public Videojuego findByUuid(String uuid) {
        log.info("Buscando videojuego con uuid: {}", uuid);
        var myUUID = UUID.fromString(uuid);
        return videojuegoRepository.findByUuid(myUUID).orElse(null);
    }

    @Override
    public Videojuego save(Videojuego videojuego) {
        log.info("Guardando videojuego: {}", videojuego);
        Long id = videojuegoRepository.nextId();
        Videojuego nuevoVideojuego = new Videojuego(
                id,
                videojuego.getNombre(),
                videojuego.getGenero(),
                videojuego.getAlmacenamiento(),
                videojuego.getFechaDeCreacion(),
                videojuego.getCosto(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                UUID.randomUUID()
        );
        return videojuegoRepository.save(nuevoVideojuego);
    }

    @Override
    public Videojuego update(Long id, Videojuego videojuego) {
        log.info("Actualizando videojuego con id: {}", id);
        var videojuegoActual = this.findById(id);
        Videojuego videojuegoActualizado = new Videojuego(
          videojuegoActual.getId(),
          videojuegoActual.getNombre() != null ? videojuego.getNombre() : videojuegoActual.getNombre(),
          videojuegoActual.getGenero() != null ? videojuego.getGenero() : videojuegoActual.getGenero(),
          videojuegoActual.getAlmacenamiento() != null ? videojuego.getAlmacenamiento() : videojuegoActual.getAlmacenamiento(),
          videojuegoActual.getFechaDeCreacion() != null ? videojuego.getFechaDeCreacion() : videojuegoActual.getFechaDeCreacion(),
          videojuegoActual.getCosto() != null ? videojuego.getCosto() : videojuegoActual.getCosto(),
          videojuegoActual.getCreatedAt(),
          LocalDateTime.now(),
          videojuegoActual.getUuid()
        );
        return videojuegoRepository.save(videojuegoActualizado);
    }

    @Override
    public void deleteById(Long id) {
        log.info("Eliminando videojuego con id: {}", id);
        var videojuegoEncontrado = this.findById(id);
        if (videojuegoEncontrado != null){
            videojuegoRepository.deleteById(id);
        }
    }
}
