package br.com.dating.api.resources.v1.auth.response;


import br.com.dating.api.resources.v1.user.response.UserResponse;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de autenticação")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserResponse user;

    // Constructors
    public AuthResponse() {
    }

    public AuthResponse(String accessToken, String refreshToken, Long expiresIn, UserResponse user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.user = user;
    }

    // Getters and Setters
    @Schema(description = "Token de acesso")
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Schema(description = "Token de atualização")
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Schema(description = "Token de atualização")
    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    @Schema(description = "Token de atualização")
    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    @Schema(description = "Token de atualização")
    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }
}
