package es.juanjsts.rest.auth.services.authentication;

import es.juanjsts.rest.auth.dto.JwtAuthResponse;
import es.juanjsts.rest.auth.dto.UserSignInRequest;
import es.juanjsts.rest.auth.dto.UserSignUpRequest;

public interface AuthenticationService {
    JwtAuthResponse signUp(UserSignUpRequest user);

    JwtAuthResponse signIn(UserSignInRequest user);
}
