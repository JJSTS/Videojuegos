package es.juanjsts.web.controller;

import es.juanjsts.rest.videojuegos.dto.VideojuegoResponseDto;
import es.juanjsts.rest.videojuegos.services.VideojuegosService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.awt.print.Pageable;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/public")
public class ZonaPublicaController {
    private final VideojuegosService videojuegosService;

    @GetMapping({"","/","/index"})
    public String index(Model model){
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<VideojuegoResponseDto> videojuegoPage = videojuegosService.findAll(
                Optional.empty(), Optional.empty(),Optional.empty(), pageable);
        model.addAttribute("videojuegos", videojuegoPage);
        return "index";
    }
}
