package es.juanjsts.web.controller;

import es.juanjsts.rest.users.models.User;
import es.juanjsts.rest.users.services.UsersServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/app/perfil")
public class PerfilController {
    private UsersServiceImpl usersService;

    @GetMapping
    public String showProfile(Model model){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = usersService.findByUsername(email).orElse(null);
        model.addAttribute("usuario", user);
        return "app/perfil";
    }

     /*

    @PostMapping("/editar")
    public String updateProfile(@Valid @ModelAttribute("usuario") User updatedUser,
                                BindingResult bindingResult,
                                Model model) {

        if (bindingResult.hasErrors()) {
            return "app/perfil";
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User existingUser = userService.findByUsername(email).orElse(null);

        // Update only allowed fields
        if (existingUser != null) {
            existingUser.setNombre(updatedUser.getNombre());
            existingUser.setApellidos(updatedUser.getApellidos());
        }

        userService.save(existingUser);
        model.addAttribute("mensaje", "Perfil actualizado correctamente");
        model.addAttribute("usuario", existingUser);

        return "app/perfil";
    }

     */
    public boolean isExternalUrl(String path){
        return path != null && (path.startsWith("http://") || path.startsWith("https://"));
    }
 }

