package es.juanjsts.web.controller;

import es.juanjsts.rest.videojuegos.dto.VideojuegoResponseDto;
import es.juanjsts.rest.videojuegos.services.VideojuegosService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("videojuegos")
public class VideojuegosController {
    private final VideojuegosService videojuegosService;

    @GetMapping("/{id}")
    public String getById(@PathVariable Long id, Model model) {
        VideojuegoResponseDto videojuego =  videojuegosService.findById(id);
        model.addAttribute("videojuego", videojuego);
        return "videojuegos/detalle";
    }
}
