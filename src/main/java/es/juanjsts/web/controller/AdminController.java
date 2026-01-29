package es.juanjsts.web.controller;

import es.juanjsts.rest.videojuegos.dto.VideojuegoCreateDto;
import es.juanjsts.rest.videojuegos.dto.VideojuegoResponseDto;
import es.juanjsts.rest.videojuegos.dto.VideojuegoUpdateDto;
import es.juanjsts.rest.videojuegos.models.Videojuego;
import es.juanjsts.rest.videojuegos.services.VideojuegosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
  private final VideojuegosService videojuegosService;

  @GetMapping("/videojuegos")
  public String videojuegos(Model model,
                            @RequestParam(name = "page", defaultValue = "0") int page,
                            @RequestParam(name = "size", defaultValue = "4") int size){
    Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
    Page<VideojuegoResponseDto> videojuegoPage = videojuegosService.findAll(
      Optional.empty(), Optional.empty(),Optional.empty(), pageable);
    model.addAttribute("page", videojuegoPage);
    return "admin/videojuegos/lista";
  }

  @GetMapping("/videojuegos/{id}")
  public String getById(@PathVariable Long id, Model model){
    Videojuego videojuego = videojuegosService.buscarPorId(id).orElse(null);
    model.addAttribute("videojuego", videojuego);
    return "admin/videojuegos/detalle";
  }

  @GetMapping("/videojuegos/new")
  public String nuevoVideojuego(Model model){

    model.addAttribute("videojuego", VideojuegoCreateDto.builder().build());
    model.addAttribute("modoEditar", false);
    return "admin/videojuegos/form";
  }

  @PostMapping("/videojuegos/new")
  public String nuevoVideojuegoSubmit(@Valid @ModelAttribute("videojuego") VideojuegoCreateDto videojuego, BindingResult bindingResult){
    log.info("Datos recibidos del formulario: {}", videojuego);
    if (bindingResult.hasErrors()){
      log.info("Hay errores en el formulario");
      return "admin/videojuegos/form";
    } else {
      videojuegosService.save(videojuego);
      return "redirect:/admin/videojuegos";
    }
  }

  @GetMapping("/videojuegos/{id}/edit")
  public String editarVideojuegoForm(@PathVariable Long id, Model model){
    Videojuego videojuegoEncontrado = videojuegosService.buscarPorId(id).orElse(null);
    if (videojuegoEncontrado == null){
      return "redirect:/admin/videojuegos/new";
    } else {
      VideojuegoUpdateDto videojuego = VideojuegoUpdateDto.builder()
        .nombre(videojuegoEncontrado.getNombre())
        .genero(videojuegoEncontrado.getGenero())
        .fechaDeCreacion(videojuegoEncontrado.getFechaDeCreacion())
        .almacenamiento(videojuegoEncontrado.getAlmacenamiento())
        .costo(videojuegoEncontrado.getCosto())
        .build();
      model.addAttribute("videojuego", videojuego);
      model.addAttribute("videojuegoId", id);
      model.addAttribute("modoEditar", true);
      return "admin/videojuegos/form";
    }
  }

  @PostMapping("/videojuegos/{id}/edit")
  public String editarVideojuegoSubmit(@PathVariable Long id,
                                       @Valid @ModelAttribute("videojuego") VideojuegoUpdateDto videojuego,
                                       BindingResult bindingResult,
                                       Model model,
                                       RedirectAttributes redirectAttribute){
    if (bindingResult.hasErrors()){
      redirectAttribute.addFlashAttribute("error",
        "Ha ocurrido un error al actualizar el videojuego");
      model.addAttribute("videojuegoId", id);
      model.addAttribute("modoEditar", true);
      return "admin/videojuegos/form";
    }

    videojuegosService.update(id, videojuego);
    redirectAttribute.addFlashAttribute("message",
      "Videojuego actualizado correctamente");
    return "redirect:/admin/videojuegos/{id}";
  }

  @GetMapping("/videojuegos/{id}/delete")
  public String borrarVideojuego(@PathVariable Long id){
    videojuegosService.deleteById(id);
    return "redirect:/admin/videojuegos";
  }
}
