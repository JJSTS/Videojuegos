package es.juanjsts.rest.videojuegos.models;

import es.juanjsts.rest.plataformas.models.Plataforma;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "VIDEOJUEGOS")
public class Videojuego {
    //ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Atributos
    @Column(nullable = false, length = 50)
    private String nombre;
    @Column(nullable = false, length = 50)
    private String genero;
    @Column(nullable = false, length = 8)
    private String almacenamiento;
    @Column(nullable = false)
    private LocalDate fechaDeCreacion;
    @Column(nullable = false)
    private Double costo;

    //Atributos de la tabla
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    @Column(unique = true, updatable = false, nullable = false)
    @Builder.Default
    private UUID uuid = UUID.randomUUID();

    // nueva columna
    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    @Builder.Default
    private Boolean isDeleted = false;

    //Relacion y atributo
    @ManyToOne
    @JoinColumn(name = "plataforma_id")
    private Plataforma plataforma;
}
