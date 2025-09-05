package br.com.dating.core.domain.user.request;

import br.com.dating.core.shared.utils.Origin;

import java.time.LocalDate;

public record CreateUserRequest(
    String name,
    String email,
    String password,
    LocalDate birthDate,
    String bio,
    String profession,
    String education,
    Integer height,
    String lifestyle,
    String lookingFor,
    String city,
    String state,
    String country,
    Origin origin
) {
}
