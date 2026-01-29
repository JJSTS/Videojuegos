package es.juanjsts.web.controller;

import es.juanjsts.rest.users.models.User;
import es.juanjsts.rest.users.services.UsersService;
import es.juanjsts.rest.videojuegos.models.Videojuego;
import es.juanjsts.rest.videojuegos.services.VideojuegosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/app")
public class VideojuegosController {
  private final VideojuegosService videojuegosService;
  private final UsersService usersService;

  @ModelAttribute("videojuegos")
  public List<Videojuego> misVideojuegos(){
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Optional<User> user = usersService.findByUsername(username);
    if(user.isEmpty()){
      return List.of();
    }
    return videojuegosService.buscarPorUsuarioId(user.get().getId());
  }

  @GetMapping("/misvideojuegos")
  public String list(){
    return "app/videojuegos/lista";
  }

  @GetMapping("/misvideojuegos/{id}")
  public String getById(@PathVariable Long id, Model model){
    Videojuego videojuego = videojuegosService.buscarPorId(id).orElse(null);
    model.addAttribute("videojuego", videojuego);
    return "app/videojuegos/detalle";
  }
}
