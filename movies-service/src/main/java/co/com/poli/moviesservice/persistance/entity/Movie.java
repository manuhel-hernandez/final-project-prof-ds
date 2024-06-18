package co.com.poli.moviesservice.persistance.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotEmpty(message = "Por favor ingrese el título de la película.")
    @Column(name = "title")
    private String title;

    @NotEmpty(message = "Por favor ingrese el director de la película.")
    @Column(name = "director")
    private String director;

    @Min(value = 1, message = "El rango de puntuación de las películas es de 1 a 5")
    @Max(value = 5, message = "El rango de puntuación de las películas es de 1 a 5")
    @Column(name = "rating")
    private int rating;

}
