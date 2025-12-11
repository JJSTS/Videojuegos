package es.juanjsts.plataformas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import es.juanjsts.users.models.User;
import es.juanjsts.videojuegos.models.Videojuego;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "PLATAFORMAS")
public class Plataforma {
    //Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Atributos
    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String fabricante;

    @Column(nullable = false, length = 20)
    private String tipo;

    @Column(nullable = false)
    private LocalDate fechaDeLanzamiento;

    //Relacion y atributo
    @JsonIgnoreProperties("plataforma")
    @OneToMany(mappedBy = "plataforma")
    private List<Videojuego> videojuegos;

    //Atributos de la tabla
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    // nueva columna
    @Column(columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isDeleted = false;

    @OneToOne(mappedBy = "plataforma")
    private User usuario;
}
