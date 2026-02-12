package es.juanjsts.config.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static final String COOKIE_NAME = "visitasApp";
    private static final String LAST_CONNECTION_COOKIE = "ultimaConexion";
    private static final int MAX_AGE = 365 * 24 * 60 * 60; // 1 año en segundos

    public LoginSuccessHandler() {
      setDefaultTargetUrl("/public");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws
      IOException, ServletException{
      int val = 0;
      String lastConnectionTime = null;

      Cookie[] cookies = request.getCookies();
      if (cookies != null) {
        for (Cookie c : cookies) {
          if (COOKIE_NAME.equals(c.getName())) {
            try {
              val = Integer.parseInt(c.getValue());
            } catch (NumberFormatException ignored) {}
          } else if (LAST_CONNECTION_COOKIE.equals(c.getName())) {
            lastConnectionTime = c.getValue();
          }
        }
      }
      val++;

      // Actualizar cookie de visitas
      Cookie visitasCookie = new Cookie(COOKIE_NAME, Integer.toString(val));
      visitasCookie.setPath("/");
      visitasCookie.setMaxAge(MAX_AGE);
      visitasCookie.setHttpOnly(false);
      visitasCookie.setSecure(request.isSecure());
      response.addCookie(visitasCookie);

      // Si ya existía una última conexión, guardarla en una cookie de "penúltima conexión"
      // para mostrarla en el perfil
      if (lastConnectionTime != null) {
        Cookie penultimaConexionCookie = new Cookie("penultimaConexion", lastConnectionTime);
        penultimaConexionCookie.setPath("/");
        penultimaConexionCookie.setMaxAge(MAX_AGE);
        penultimaConexionCookie.setHttpOnly(false);
        penultimaConexionCookie.setSecure(request.isSecure());
        response.addCookie(penultimaConexionCookie);
      }

      // Guardar la conexión actual como "última conexión"
      String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
      Cookie ultimaConexionCookie = new Cookie(LAST_CONNECTION_COOKIE, currentDateTime);
      ultimaConexionCookie.setPath("/");
      ultimaConexionCookie.setMaxAge(MAX_AGE);
      ultimaConexionCookie.setHttpOnly(false);
      ultimaConexionCookie.setSecure(request.isSecure());
      response.addCookie(ultimaConexionCookie);

      super.onAuthenticationSuccess(request, response, authentication);
    }
}
