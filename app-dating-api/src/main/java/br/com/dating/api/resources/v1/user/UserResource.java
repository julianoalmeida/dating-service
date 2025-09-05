package br.com.dating.api.resources.v1.user;

import java.util.UUID;

import br.com.dating.api.PageableResponse;
import br.com.dating.api.resources.v1.user.request.RegisterUserRequest;
import br.com.dating.core.domain.user.UserCreatorService;
import br.com.dating.core.shared.utils.Origin;
import jakarta.validation.Valid;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.dating.api.HeadersConstants;
import br.com.dating.api.message.ResponseError;
import br.com.dating.api.resources.v1.user.response.UserResponse;
import br.com.dating.core.domain.user.UserFinderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Validated
@RestController
@RequestMapping(
    value = "/users",
    headers = HeadersConstants.X_API_VERSION_V1
)
@CrossOrigin(origins = "*")
@Tag(name = "User", description = "dating User API")
public class UserResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

    private final UserFinderService userFinderService;
    private final UserCreatorService userCreatorService;

    public UserResource(UserFinderService userFinderService, UserCreatorService userCreatorService) {
        this.userFinderService = userFinderService;
        this.userCreatorService = userCreatorService;
    }

    @Operation(summary = "Cria um usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário criado com sucesso"),
        @ApiResponse(responseCode = "409", description = "Usuário já existe",
            content = {@Content(schema = @Schema(implementation = ResponseError.class))}
        )
    })
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> create(@RequestHeader(value = "X-Origin") Origin origin,
                                               @Valid @RequestBody RegisterUserRequest request) {

        LOGGER.info("stage=init, origin={}, request={}", origin.getPublicName(), request.toString());

        request.setOrigin(origin);

        final var user = userCreatorService.execute(request.toDomain(request));

        LOGGER.info("stage=created, userCode={}", user.getCode());

        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponse(user));
    }

    @Operation(summary = "Busca um usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
            content = {@Content(schema = @Schema(implementation = ResponseError.class))})
    })
    @Parameter(
        name = "userCode",
        in = ParameterIn.PATH,
        schema = @Schema(format = "string", requiredMode = Schema.RequiredMode.REQUIRED),
        description = "Código do usuário"
    )
    @GetMapping(value = "/{userCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> getUserByCode(@PathVariable UUID userCode, Authentication authentication) {

        var user = userFinderService.findByCode(userCode);

        return user.map(UserResponse::new)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Busca uma lista de usuários")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    })
    @Parameter(
        name = "page",
        in = ParameterIn.QUERY,
        description = "Número da página que deseja visualizar, o valor default é 0")
    @Parameter(
        name = "offset",
        in = ParameterIn.QUERY,
        description = "Número de registros por páginas, o valor default é 10. Máximo 100 registros por página")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public PageableResponse<UserResponse> getUsers(
        @RequestHeader(value = "X-Origin") Origin origin,
        @RequestParam(name = "page", required = false, defaultValue = "0") @Min(0) Integer page,
        @RequestParam(name = "offset", required = false, defaultValue = "10") @Min(1) @Max(100)
        Integer offset,
        Authentication authentication) {

        LOGGER.info("stage=init, origin={}, page={}, offset={}", origin.getPublicName(), page, offset);

        var users = userFinderService.findAll(PageRequest.of(page, offset));

        return new PageableResponse<>(
            users.getTotalElements(),
            users.getNumber(),
            users.getSize(),
            users.getTotalPages(),
            users.isLast(),
            users.getContent().stream().map(UserResponse::new).toList()
        );
    }
}
