package es.juanjsts.rest.auth.services.users;

import es.juanjsts.rest.auth.repositories.AuthUsersRepository;
import es.juanjsts.rest.users.exceptions.UserNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthUsersServiceImpl implements AuthUsersService {
    private final AuthUsersRepository authUsersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UserNotFound {
        return authUsersRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound("Usuario con username " + username + " no encontrado"));
    }
}
