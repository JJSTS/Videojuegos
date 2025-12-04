package es.juanjsts.plataformas.mappers;

import es.juanjsts.plataformas.dto.PlataformaCreatedDto;
import es.juanjsts.plataformas.dto.PlataformaUpdateDto;
import es.juanjsts.plataformas.models.Plataforma;
import org.springframework.stereotype.Component;

@Component
public class PlataformaMapper {
    public Plataforma toPlataforma(PlataformaCreatedDto plataformaCreatedDto) {
        return Plataforma.builder()
                .id(null)
                .nombre(plataformaCreatedDto.getNombre())
                .fabricante(plataformaCreatedDto.getFabricante())
                .tipo(plataformaCreatedDto.getTipo())
                .fechaDeLanzamiento(plataformaCreatedDto.getFechaDeLanzamiento())
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
                .isDeleted(plataformaUpdateDto.getIsDeleted() != null ? plataformaUpdateDto.getIsDeleted() : plataformaActual.getIsDeleted())
                .build();
    }
}
