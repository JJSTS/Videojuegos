package es.juanjsts.rest.jugadores.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import es.juanjsts.rest.users.models.User;
import es.juanjsts.rest.videojuegos.models.Videojuego;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "JUGADORES")
public class Jugador {
    //Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Atributos
    @Column(nullable = false, length = 50)
    private String nombre;

    //Relacion y atributo
    @JsonIgnoreProperties("jugador")
    @OneToMany(mappedBy = "jugador")
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

    @OneToOne(mappedBy = "jugador")
    private User usuario;
}
