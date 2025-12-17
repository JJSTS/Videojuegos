package es.juanjsts.auth.controllers;

import es.juanjsts.auth.dto.JwtAuthResponse;
import es.juanjsts.auth.dto.UserSignInRequest;
import es.juanjsts.auth.dto.UserSignUpRequest;
import es.juanjsts.auth.exceptions.AuthDifferentPasswords;
import es.juanjsts.auth.exceptions.AuthExistingUsernameOrEmail;
import es.juanjsts.auth.exceptions.AuthSignInNotValid;
import es.juanjsts.auth.services.authentication.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationRestControllerTest {

    private final String ENDPOINT = "/api/v1/auth";

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private AuthenticationService authenticationService;

    @Test
    void signup() {
        String requestBody = """
                {
                "nombre" : "Miguel",
                "apellidos" : "Mendoza Neira",
                "username" : "miguel",
                "email" : "miguel@pachamanca.com",
                "password" : "12345",
                "passwordComprobacion" : "12345"
                }
                """;
        var jwtAuthResponse = JwtAuthResponse.builder().token("token").build();

        //Arrange
        when(authenticationService.signUp(any(UserSignUpRequest.class))).thenReturn(jwtAuthResponse);

        //Llamada al ENDPOINT
        var result = mockMvcTester.post()
                .uri(ENDPOINT + "/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        //Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .convertTo(JwtAuthResponse.class)
                .isEqualTo(jwtAuthResponse);

        //Verify
        verify(authenticationService, times(1)).signUp(any(UserSignUpRequest.class));
    }

    @Test
    void signUp_WhenPasswordsDoNotMatch_ShouldThrowException() {
        String requestBody = """
                {
                "nombre" : "Miguel",
                "apellidos" : "Mendoza Neira",
                "username" : "miguel",
                "email" : "miguel@pachamanca.com",
                "password" : "12345",
                "passwordComprobacion" : "52131"
                }
                """;

        //Mock del service
        when(authenticationService.signUp(any(UserSignUpRequest.class)))
                .thenThrow(new AuthDifferentPasswords("Las contraseñas no coinciden"));

        //Llamada al ENDPOINT
        var result = mockMvcTester.post()
                .uri(ENDPOINT + "/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        //Assert
        assertThat(result)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .hasFailed().failure()
                .isInstanceOf(AuthDifferentPasswords.class)
                .hasMessageContaining("Las contraseñas no coinciden");

        //Verify
        verify(authenticationService, times(1)).signUp(any(UserSignUpRequest.class));
    }

    @Test
    void signUp_WhenUsernameOrEmailAlreadyExist_ShouldThrowException() {
        String requestBody = """
                {
                "nombre" : "Miguel",
                "apellidos" : "Mendoza Neira",
                "username" : "miguel",
                "email" : "miguel@pachamanca.com",
                "password" : "12345",
                "passwordComprobacion" : "12345"
                }
                """;

        //Mock del service
        when(authenticationService.signUp(any(UserSignUpRequest.class)))
                .thenThrow(new AuthExistingUsernameOrEmail("El usuario con username XXX o email XXX ya existe"));

        //Llamada al ENDPOINT
        var result = mockMvcTester.post()
                .uri(ENDPOINT + "/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        //Assert
        assertThat(result)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .hasFailed().failure()
                .isInstanceOf(AuthExistingUsernameOrEmail.class)
                .hasMessageContaining("ya existe");

    }

    @Test
    void signUp_BadRequest_When_Nombre_Apellidos_Email_Username_Empty_ShouldThrowException() {
        String requestBody = """
                {
                "nombre" : "",
                "apellidos" : "",
                "username" : "",
                "email" : "",
                "password" : "12345",
                "passwordComprobacion" : "12345"
                }
                """;

        // Llamada al ENDPOINT
        var result = mockMvcTester.post()
                .uri(ENDPOINT + "/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        //Assert
        assertThat(result)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .hasPathSatisfying("$.errores", path ->{
                    assertThat(path).hasFieldOrProperty("nombre");
                    assertThat(path).hasFieldOrProperty("apellidos");
                    assertThat(path).hasFieldOrProperty("username");
                    assertThat(path).hasFieldOrProperty("email");
                });

        //Verify
        verify(authenticationService, never()).signUp(any(UserSignUpRequest.class));
    }

    @Test
    void signIn() {
        String requestBody = """
                {
                "username" : "miguel",
                "password" : "12345"
                }
                """;
        var jwtAuthResponse = JwtAuthResponse.builder().token("token").build();

        //Arrange
        when(authenticationService.signIn(any(UserSignInRequest.class))).thenReturn(jwtAuthResponse);

        //Llamada al ENDPOINT
        var result = mockMvcTester.post()
                .uri(ENDPOINT + "/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .convertTo(JwtAuthResponse.class)
                .isEqualTo(jwtAuthResponse);

        //Verify
        verify(authenticationService, times(1)).signIn(any(UserSignInRequest.class));

    }

    @Test
    void signIn_NotValid() {
        String requestBody = """
                {
                "username" : "miguel",
                "password" : "pantalla"
                }
                """;
        //Mock del service
        when(authenticationService.signIn(any(UserSignInRequest.class)))
                .thenThrow(new AuthSignInNotValid("Usuario o contraseña incorrectos"));

        //Llamada al EndPoint
        var result = mockMvcTester.post()
                .uri(ENDPOINT + "/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        //Assert
        assertThat(result)
                .hasStatus4xxClientError()
                .hasFailed().failure()
                .isInstanceOf(AuthSignInNotValid.class)
                .hasMessageContaining("incorrectos");

        verify(authenticationService, times(1)).signIn(any(UserSignInRequest.class));
    }

    @Test
    void signIn_BadRequest_When_Username_Password_Empty_ShouldThrowException() {
        String requestBody = """
                {
                "username" : "",
                "password" : ""
                }
        """;

        //Llamada al EndPoint
        var result = mockMvcTester.post()
                .uri(ENDPOINT + "/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        //Assert
        assertThat(result)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .hasPathSatisfying("$.errores", path ->{
                    assertThat(path).hasFieldOrProperty("username");
                    assertThat(path).hasFieldOrProperty("password");
                });

        //Verify
        verify(authenticationService, never()).signIn(any(UserSignInRequest.class));
    }
}