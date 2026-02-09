package es.juanjsts.config.auth;

import es.juanjsts.rest.auth.services.jwt.JwtService;
import es.juanjsts.rest.auth.services.users.AuthUsersService;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthUsersService authUsersService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        log.info("Iniciando el filtro de autenticación");
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        UserDetails userDetails = null;
        String userName = null;
        if (!StringUtils.hasText(authHeader) || !StringUtils.startsWithIgnoreCase(authHeader, "Bearer ")) {
            log.info("No se ha encontrado cabecera de autenticación, se ignora");
            filterChain.doFilter(request, response);
            return;
        }

        log.info("Se ha encontrado cabecera de autenticación, se procesa");
        jwt = authHeader.substring(7);

      try {
        userName = jwtService.extractUsername(jwt);
      } catch (Exception e) {
        log.info("Token no válido");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token no autorizado o no válido");
        return;
      }


      log.info("Usuario autenticado: {}", userName);
        if (StringUtils.hasText(userName)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.info("Comprobando usuario y token");
            try {
                userDetails = authUsersService.loadUserByUsername(userName);
            } catch (Exception e) {
                log.info("Usuario no encontrado: {}", userName);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuario no autorizado");
                return;
            }
            log.info("Usuario encontrado: {}", userName);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                log.info("JWT valido");
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
            }
        }
        filterChain.doFilter(request, response);
    }
}
