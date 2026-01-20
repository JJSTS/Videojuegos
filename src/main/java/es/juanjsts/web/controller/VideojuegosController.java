package es.juanjsts.web.controller;

import es.juanjsts.rest.videojuegos.dto.VideojuegoCreateDto;
import es.juanjsts.rest.videojuegos.dto.VideojuegoResponseDto;
import es.juanjsts.rest.videojuegos.services.VideojuegosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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

    @GetMapping({"","/","/lista"})
    public String lista(Model model,
                        @RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "size", defaultValue = "4") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<VideojuegoResponseDto> videojuegoPage = videojuegosService.findAll(
                Optional.empty(), Optional.empty(),Optional.empty(), pageable);
        model.addAttribute("page", videojuegoPage);
        return "/videojuegos/lista";
    }

    @GetMapping("/new")
    public String nuevoVideojuegoForm(Model model){
        model.addAttribute("videojuego", VideojuegoCreateDto.builder().build());
        return "/videojuegos/form";
    }

    @PostMapping("/new")
    public String nuevoVideojuegoSubmit(@Valid @ModelAttribute VideojuegoCreateDto videojuego, BindingResult bindingResult){

        if (bindingResult.hasErrors()){
            return "/videojuegos/form";
        } else {
            videojuegosService.save(videojuego);
            return "redirect:/videojuegos/lista";
        }
    }
}
