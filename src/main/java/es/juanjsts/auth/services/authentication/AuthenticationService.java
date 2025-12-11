package es.juanjsts.auth.services.authentication;

import es.juanjsts.auth.dto.JwtAuthResponse;
import es.juanjsts.auth.dto.UserSignInRequest;
import es.juanjsts.auth.dto.UserSignUpRequest;

public interface AuthenticationService {
    JwtAuthResponse signUp(UserSignUpRequest user);

    JwtAuthResponse signIn(UserSignInRequest user);
}
