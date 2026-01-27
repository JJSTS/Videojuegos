package es.juanjsts.web.controller;

import es.juanjsts.rest.users.models.User;
import es.juanjsts.rest.users.services.UsersServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class LoginController {
    private final UsersServiceImpl usersService;

    @GetMapping("/")
    public String welcome(){
        return "redirect:/public/";
    }

    @GetMapping("/auth/login")
    public String login(Model model){
        model.addAttribute("usuario", new User());
        return "login";
    }
}
