package br.com.dating.api.resources.v1.user;

import br.com.dating.api.HeadersConstants;
import br.com.dating.api.PageableResponse;
import br.com.dating.api.message.ResponseError;
import br.com.dating.api.resources.v1.user.response.UserResponse;

import br.com.dating.core.domain.user.UserFinderService;


import com.google.common.collect.Lists;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping(
    value = "/users/me",
    headers = HeadersConstants.X_API_VERSION_V1
)
@CrossOrigin(origins = "*")
@Tag(name = "User authenticated", description = "dating User Authenticated API")

public class UserAuthenticatedResource {

    private final UserFinderService userFinderService;

    public UserAuthenticatedResource(UserFinderService userFinderService) {
        this.userFinderService = userFinderService;
    }

    @Operation(summary = "Retorna os dados do usuário autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Perfil não encontrado",
            content = {@Content(schema = @Schema(implementation = ResponseError.class))})
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> getUser(Authentication authentication) {

        var email = authentication.getName();

        var user = userFinderService.findByEmail(email);

        return user.map(UserResponse::new)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Atualiza os dados do usuário autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso")
    })
    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> updateUser(Authentication authentication) {

        var email = authentication.getName();

        var user = userFinderService.findByEmail(email).orElseThrow();

        return ResponseEntity.ok(new UserResponse(user));
    }

    @Operation(summary = "Buscar perfis disponíveis para curtir (descoberta geográfica ou preferências)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuários encontrados com sucesso")
    })
    @Parameter(
        name = "page",
        in = ParameterIn.QUERY,
        description = "Número da página que deseja visualizar, o valor default é 0")
    @Parameter(
        name = "offset",
        in = ParameterIn.QUERY,
        description = "Número de registros por páginas, o valor default é 10. Máximo 100 registros por página")
    @GetMapping(path = "/discover", produces = MediaType.APPLICATION_JSON_VALUE)
    public PageableResponse<UserResponse> discover(
        Authentication authentication,
        @RequestParam(name = "page", required = false, defaultValue = "0") @Min(0) Integer page,
        @RequestParam(name = "offset", required = false, defaultValue = "10") @Min(1) @Max(20) Integer offset
    ) {

        var email = authentication.getName();

        var user = userFinderService.findByEmail(email).orElseThrow();

        var pageRequest = PageRequest.of(page, offset);

        var users = userFinderService.findPotentialMatch(Lists.newArrayList(user.getId()), "Teste", "Teste", pageRequest);

        return new PageableResponse<>(
            users.getTotalElements(),
            users.getNumber(),
            users.getSize(),
            users.getTotalPages(),
            users.isLast(),
            users.getContent().stream().map(UserResponse::new).toList()
        );
    }

    @GetMapping(path = "/limits", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserLimits(Authentication authentication) {

        var email = authentication.getName();

        var user = userFinderService.findByEmail(email).orElseThrow();

        Map<String, Object> limits = new HashMap<>();

        limits.put("dailyLikes", user.getDailyLikes());
        limits.put("dailyMessages", user.getDailyMessages());
        limits.put("maxDailyLikes", user.isPremium() ? -1 : 10); // -1 = unlimited
        limits.put("maxDailyMessages", user.isPremium() ? -1 : 10);
//        limits.put("canLike", userFinderService.canLike(user));
//        limits.put("canSendMessage", userFinderService.canSendMessage(user));

        return ResponseEntity.ok(limits);
    }
}
