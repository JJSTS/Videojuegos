package es.juanjsts.videojuegos;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import static java.lang.IO.*;

@EnableCaching
@SpringBootApplication
public class VideojuegosApplication implements CommandLineRunner {

    static void main(String[] args) {
        SpringApplication.run(VideojuegosApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception{
        println("Iniciando Spring Boot Application");
    }
}
