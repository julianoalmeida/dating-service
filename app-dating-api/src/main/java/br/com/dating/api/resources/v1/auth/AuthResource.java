package br.com.dating.api.resources.v1.auth;

import br.com.dating.api.HeadersConstants;
import br.com.dating.api.config.jwt.JwtTokenUtil;
import br.com.dating.api.resources.v1.auth.request.AuthLoginRequest;
import br.com.dating.api.resources.v1.auth.request.AuthRefreshTokenRequest;
import br.com.dating.api.resources.v1.auth.response.AuthResponse;
import br.com.dating.api.resources.v1.user.response.UserResponse;
import br.com.dating.core.shared.error.ErrorCode;
import br.com.dating.core.shared.exception.BusinessException;
import br.com.dating.core.domain.user.UserCreatorService;

import br.com.dating.core.domain.user.UserFinderService;

import br.com.dating.core.domain.user.request.CreateUserRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping(
    value = "/auth",
    headers = HeadersConstants.X_API_VERSION_V1
)
@CrossOrigin(origins = "*")
@Tag(name = "Auth", description = "dating Auth API")
public class AuthResource {

    private final AuthenticationManager authenticationManager;

    private final UserCreatorService userCreatorService;

    private final UserFinderService userFinderService;

    private final JwtTokenUtil jwtTokenUtil;

    public AuthResource(AuthenticationManager authenticationManager,
                        UserCreatorService userCreatorService,
                        UserFinderService userFinderService,
                        JwtTokenUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.userCreatorService = userCreatorService;
        this.userFinderService = userFinderService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody CreateUserRequest request) {
        var user = userCreatorService.execute(request);

        var userDetails = userFinderService.loadUserByUsername(user.getEmail());
        var accessToken = jwtTokenUtil.generateToken(userDetails);
        var refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

        var response = new AuthResponse(
            accessToken,
            refreshToken,
            3600000L,
            new UserResponse(user)
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthLoginRequest request) {

        var authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        var userDetails = (UserDetails) authentication.getPrincipal();
        var user = userFinderService.findByEmail(userDetails.getUsername()).orElseThrow();

        var accessToken = jwtTokenUtil.generateToken(userDetails);
        var refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

        var response = new AuthResponse(
            accessToken,
            refreshToken,
            3600000L, // 1 hour
            new UserResponse(user)
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody AuthRefreshTokenRequest request) throws BusinessException {

        var refreshToken = request.getRefreshToken();
        var email = jwtTokenUtil.getUsernameFromToken(refreshToken);

        var userDetails = userFinderService.loadUserByUsername(email);

        if (jwtTokenUtil.validateToken(refreshToken, userDetails)) {
            String newAccessToken = jwtTokenUtil.generateToken(userDetails);

            var response = new AuthResponse();
            response.setAccessToken(newAccessToken);
            response.setTokenType("Bearer");
            response.setExpiresIn(3600000L);

            return ResponseEntity.ok(response);
        } else {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // In a real implementation, you would invalidate the token
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout realizado com sucesso");
        return ResponseEntity.ok(response);
    }
}
