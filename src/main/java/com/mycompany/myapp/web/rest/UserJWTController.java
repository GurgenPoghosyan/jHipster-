package com.mycompany.myapp.web.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mycompany.myapp.domain.RefreshToken;
import com.mycompany.myapp.security.RefreshTokenException;
import com.mycompany.myapp.security.jwt.JWTFilter;
import com.mycompany.myapp.security.jwt.RefreshTokenRequest;
import com.mycompany.myapp.security.jwt.RefreshTokenResponse;
import com.mycompany.myapp.security.jwt.TokenProvider;
import com.mycompany.myapp.security.session.SessionUser;
import com.mycompany.myapp.service.RefreshTokenService;
import com.mycompany.myapp.web.rest.vm.LoginVM;
import javax.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller to authenticate users.
 */
@RestController
@RequestMapping("/api")
public class UserJWTController {

    private final TokenProvider tokenProvider;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final RefreshTokenService refreshTokenService;

    public UserJWTController(
        TokenProvider tokenProvider,
        AuthenticationManagerBuilder authenticationManagerBuilder,
        RefreshTokenService refreshTokenService
    ) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<JWTToken> authorize(@Valid @RequestBody LoginVM loginVM) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            loginVM.getUsername(),
            loginVM.getPassword()
        );

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = tokenProvider.createToken(authentication, loginVM.isRememberMe());

        SessionUser sessionUser = refreshTokenService.getSessionUser();
        String refreshToken = refreshTokenService.createRefreshToken(sessionUser.getId()).getToken();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwtToken);
        return new ResponseEntity<>(new JWTToken(jwtToken, refreshToken), httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/refresh_token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService
            .findByToken(requestRefreshToken)
            .map(refreshTokenService::verifyExpiration)
            .map(RefreshToken::getUser)
            .map(
                user -> {
                    String token = tokenProvider.generateTokenFromUsername(user.getLogin());
                    return ResponseEntity.ok(new RefreshTokenResponse(token, requestRefreshToken));
                }
            )
            .orElseThrow(() -> new RefreshTokenException("Refresh token is not in database!"));
    }

    /**
     * Object to return as body in JWT Authentication.
     */
    static class JWTToken {

        private String jwtToken;
        private String refreshToken;

        JWTToken(String jwtToken, String refreshToken) {
            this.jwtToken = jwtToken;
            this.refreshToken = refreshToken;
        }

        @JsonProperty("jwt_token")
        String getJwtToken() {
            return jwtToken;
        }

        void setJwtToken(String jwtToken) {
            this.jwtToken = jwtToken;
        }

        @JsonProperty("refresh_token")
        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }
}
