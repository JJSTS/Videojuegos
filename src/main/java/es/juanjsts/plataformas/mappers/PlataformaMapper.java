package es.juanjsts.plataformas.mappers;

import es.juanjsts.plataformas.dto.PlataformaCreatedDto;
import es.juanjsts.plataformas.dto.PlataformaResponseDto;
import es.juanjsts.plataformas.dto.PlataformaUpdateDto;
import es.juanjsts.plataformas.models.Plataforma;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class PlataformaMapper {
    public Plataforma toPlataforma(PlataformaCreatedDto plataformaCreatedDto) {
        return Plataforma.builder()
                .id(null)
                .nombre(plataformaCreatedDto.getNombre())
                .fabricante(plataformaCreatedDto.getFabricante())
                .tipo(plataformaCreatedDto.getTipo())
                .fechaDeLanzamiento(plataformaCreatedDto.getFechaDeLanzamiento())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(UUID.randomUUID())
                .build();
    }

    public Plataforma toPlataforma(PlataformaUpdateDto plataformaUpdateDto, Plataforma plataformaActual) {
        return Plataforma.builder()
                .id(plataformaActual.getId())
                .nombre(plataformaUpdateDto.getNombre() != null ? plataformaUpdateDto.getNombre() : plataformaActual.getNombre())
                .fabricante(plataformaUpdateDto.getFabricante() != null ? plataformaUpdateDto.getFabricante() : plataformaActual.getFabricante())
                .tipo(plataformaUpdateDto.getTipo() != null ? plataformaUpdateDto.getTipo() : plataformaActual.getTipo())
                .fechaDeLanzamiento(plataformaUpdateDto.getFechaDeLanzamiento() != null ? plataformaUpdateDto.getFechaDeLanzamiento() : plataformaActual.getFechaDeLanzamiento())
                .createdAt(plataformaActual.getCreatedAt())
                .uuid(plataformaActual.getUuid())
                .isDeleted(plataformaUpdateDto.getIsDeleted() != null ? plataformaUpdateDto.getIsDeleted() : plataformaActual.getIsDeleted())
                .build();
    }

    public PlataformaResponseDto toPlataformaResponseDto(Plataforma plataforma){
        return PlataformaResponseDto.builder()
                .id(plataforma.getId())
                .nombre(plataforma.getNombre())
                .fabricante(plataforma.getFabricante())
                .tipo(plataforma.getTipo())
                .fechaDeLanzamiento(plataforma.getFechaDeLanzamiento())
                .createdAt(plataforma.getCreatedAt())
                .updatedAt(plataforma.getUpdatedAt())
                .uuid(plataforma.getUuid())
                .isDeleted(plataforma.getIsDeleted())
                .build();
    }

    public List<PlataformaResponseDto> toResponseDtoList(List<Plataforma> plataformas){
        return plataformas.stream()
                .map(this::toPlataformaResponseDto)
                .toList();
    }
}
