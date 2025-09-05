package br.com.dating.core.domain.user.request;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateUserRequest(
    UUID userCode,
    String name,
    String email,
    String password,
    LocalDate birthDate,
    String bio,
    String profession,
    String education,
    Integer height,
    String bodyType,
    String lifestyle,
    String lookingFor,
    String city,
    String state,
    String country
) {
}
