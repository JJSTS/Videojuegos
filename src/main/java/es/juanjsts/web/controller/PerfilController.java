package es.juanjsts.web.controller;

import es.juanjsts.rest.users.models.User;
import es.juanjsts.rest.users.services.UsersService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@RequiredArgsConstructor
@Controller
@RequestMapping("/app/perfil")
public class PerfilController {
    private final UsersService usersService;

    @GetMapping
    public String showProfile(Model model, HttpServletRequest request){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = usersService.findByUsername(username).orElse(null);
        model.addAttribute("usuario", user);

        //Cookie de última conexión
        String penultimaConexion = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("penultimaConexion".equals(cookie.getName())) {
                    penultimaConexion = cookie.getValue();
                    break;
                }
            }
        }

        if (penultimaConexion != null) {
            try {
                LocalDateTime fechaHora = LocalDateTime.parse(penultimaConexion, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                DateTimeFormatter formateador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                String fechaFormateada = fechaHora.format(formateador);
                model.addAttribute("ultimaConexion", fechaFormateada);
            } catch (DateTimeParseException e) {
                // Si hay error al parsear, ignorar
                model.addAttribute("ultimaConexion", null);
            }
        } else {
            model.addAttribute("ultimaConexion", null);
        }

        return "app/perfil";
    }

    @PostMapping("/edit")
    public String updateProfile(@Valid @ModelAttribute("usuario") User updatedUser,
                                BindingResult bindingResult,
                                Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("mensaje", "Ha ocurrido un error al actualizar el perfil.");
            return "app/perfil";
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User existingUser = usersService.findByUsername(username).orElse(null);

        // Update only allowed fields
        if (existingUser != null) {
            existingUser.setNombre(updatedUser.getNombre());
            existingUser.setApellidos(updatedUser.getApellidos());
        }

        usersService.save(existingUser);
        model.addAttribute("mensaje", "Perfil actualizado correctamente");
        model.addAttribute("usuario", existingUser);

        return "redirect:/app/perfil";
    }
 }

