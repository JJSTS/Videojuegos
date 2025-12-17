package es.juanjsts.auth.services.authentication;

import es.juanjsts.auth.dto.JwtAuthResponse;
import es.juanjsts.auth.dto.UserSignInRequest;
import es.juanjsts.auth.dto.UserSignUpRequest;
import es.juanjsts.auth.exceptions.AuthDifferentPasswords;
import es.juanjsts.auth.exceptions.AuthExistingUsernameOrEmail;
import es.juanjsts.auth.exceptions.AuthSignInNotValid;
import es.juanjsts.auth.repositories.AuthUsersRepository;
import es.juanjsts.auth.services.jwt.JwtService;
import es.juanjsts.users.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private AuthUsersRepository authUsersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationServiceImpl authenticationServiceImpl;

    @Test
    void testSignUp_WhenPasswordsMatch_ShouldReturnToken() {
        //Datos de la prueba
        UserSignUpRequest request = UserSignUpRequest.builder()
                .nombre("Frank")
                .apellidos("Frank")
                .username("frank")
                .email("frank@frank.com")
                .password("1234")
                .passwordComprobacion("1234")
                .build();

        // Mock del repositorio de usuarios
        User userStored = new User();
        when(authUsersRepository.save(any(User.class))).thenReturn(userStored);

        // Mock del servicio JWT
        String token = "test_token";
        when(jwtService.generateToken(userStored)).thenReturn(token);

        // Llamada al método a probar
        JwtAuthResponse response = authenticationServiceImpl.signUp(request);

        //Verificaciones
        assertAll("Sign Up",
                () -> assertNotNull(response),
                () -> assertEquals(token, response.getToken()),
                () -> verify(authUsersRepository, times(1)).save(any(User.class)),
                () -> verify(jwtService, times(1)).generateToken(userStored)
        );
    }

    @Test
    public void testSignUp_WhenPasswordsDoNotMatch_ShouldTrowException() {
        UserSignUpRequest request = UserSignUpRequest.builder()
                .nombre("Frank")
                .apellidos("Frank")
                .username("frank")
                .email("frank@frank.com")
                .password("1234p")
                .passwordComprobacion("1234a")
                .build();

        assertThrows(AuthDifferentPasswords.class, () -> authenticationServiceImpl.signUp(request));
    }

    @Test
    public void testSignUp_WhenUsernameOrEmailAlreadyExist_ShouldThrowException() {
        UserSignUpRequest request = UserSignUpRequest.builder()
                .nombre("Frank")
                .apellidos("Frank")
                .username("franklin")
                .email("frank@frank.com")
                .password("1234")
                .passwordComprobacion("1234")
                .build();

        //Mock del repositorio de usuarios
        when(authUsersRepository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);

        // Llamada al metodo a probar y verifiación de excepción
        assertThrows(AuthExistingUsernameOrEmail.class, () -> authenticationServiceImpl.signUp(request));
    }

    @Test
    public void testSignIn_WhenValidCredentials_ShouldReturnToken() {
        //Datos de prueba
        UserSignInRequest request = UserSignInRequest.builder()
                .username("franklin")
                .password("1234")
                .build();

        // Mock del repositorio de usuarios
        User user = new User();
        when(authUsersRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));

        //Mock del JWT
        String token = "test_token";
        when(jwtService.generateToken(user)).thenReturn(token);

        //Llamado al metodo probar
        JwtAuthResponse response = authenticationServiceImpl.signIn(request);

        //Verificaciones
        assertAll("Sign In",
                () -> assertNotNull(response),
                () -> assertEquals(token, response.getToken()),
                () -> verify(authenticationManager, times(1))
                        .authenticate(any(UsernamePasswordAuthenticationToken.class)),
                () -> verify(authUsersRepository, times(1)).findByUsername(request.getUsername()),
                () -> verify(jwtService, times(1)).generateToken(user)
        );
    }

    @Test
    public void testSignIn_WhenInvalidCredentials_ShouldThrowException() {
        //Datos de prueba
        UserSignInRequest request = UserSignInRequest.builder()
                .username("franklin")
                .password("1234")
                .build();

        //Mock del repositorio de usuarios
        when(authUsersRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());

        //Llamada al metodo a probar y verificación de excepción
        assertThrows(AuthSignInNotValid.class,  () -> authenticationServiceImpl.signIn(request));
    }
}