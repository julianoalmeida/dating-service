package br.com.dating.api.resources.v1.user.request;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

import br.com.dating.api.MessageCodes;

import br.com.dating.core.domain.user.request.CreateUserRequest;
import br.com.dating.core.shared.utils.Origin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import com.google.common.base.MoreObjects;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Payload para criação de um usuário")
public class RegisterUserRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -5106167612791940672L;

    @Valid
    @Schema(description = "Nome do usuário")
    @NotEmpty(message = MessageCodes.REQUIRED_FIELD)
    @Size(max = 250)
    private String name;

    @Valid
    @Schema(description = "E-mail do usuário")
    @NotEmpty(message = MessageCodes.REQUIRED_FIELD)
    @Size(max = 250)
    private String email;

    @Valid
    @Schema(description = "Senha do usuário")
    @NotEmpty(message = MessageCodes.REQUIRED_FIELD)
    @Size(max = 10)
    private String password;

    @Valid
    @Past(message = "Data de nascimento deve ser no passado")
    private LocalDate birthDate;

    @Valid
    @Schema(description = "Biografia")
    @NotEmpty(message = MessageCodes.REQUIRED_FIELD)
    @Size(max = 250)
    private String bio;

    @Valid
    @Schema(description = "Profissão")
    @NotEmpty(message = MessageCodes.REQUIRED_FIELD)
    @Size(max = 250)
    private String profession;

    @Valid
    @Schema(description = "Cidade do usuário")
    @NotEmpty(message = MessageCodes.REQUIRED_FIELD)
    @Size(max = 250)
    private String education;

    @Valid
    @Schema(description = "Peso")
    @NotNull(message = MessageCodes.REQUIRED_FIELD)
    @Min(20)
    @Max(250)
    private Integer height;

    @Valid
    @Schema(description = "Estilo de vida")
    @NotEmpty(message = MessageCodes.REQUIRED_FIELD)
    @Size(max = 250)
    private String lifestyle;

    @Valid
    @Schema(description = "O que o usuário procura")
    @NotEmpty(message = MessageCodes.REQUIRED_FIELD)
    @Size(max = 250)
    private String lookingFor;

    @Valid
    @Schema(description = "Cidade do usuário")
    @NotEmpty(message = MessageCodes.REQUIRED_FIELD)
    @Size(max = 250)
    private String city;

    @Valid
    @Schema(description = "Estado do usuário")
    @NotEmpty(message = MessageCodes.REQUIRED_FIELD)
    @Size(max = 250)
    private String state;

    @Valid
    @Schema(description = "País do usuário")
    @NotEmpty(message = MessageCodes.REQUIRED_FIELD)
    @Size(max = 250)
    private String country;

    @JsonIgnore
    private Origin origin;

    public RegisterUserRequest() {
    }

    public RegisterUserRequest(String name,
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
                               String changeAgent) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
        this.bio = bio;
        this.profession = profession;
        this.education = education;
        this.height = height;
        this.lifestyle = lifestyle;
        this.lookingFor = lookingFor;
        this.city = city;
        this.state = state;
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getLifestyle() {
        return lifestyle;
    }

    public void setLifestyle(String lifestyle) {
        this.lifestyle = lifestyle;
    }

    public String getLookingFor() {
        return lookingFor;
    }

    public void setLookingFor(String lookingFor) {
        this.lookingFor = lookingFor;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Origin getOrigin() {
        return origin;
    }

    public void setOrigin(Origin origin) {
        this.origin = origin;
    }

    public CreateUserRequest toDomain(RegisterUserRequest request) {
        return new CreateUserRequest(
            request.name,
            request.email,
            request.password,
            request.birthDate,
            request.bio,
            request.profession,
            request.education,
            request.height,
            request.lifestyle,
            request.lookingFor,
            request.city,
            request.state,
            request.country,
            origin
        );
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("name", name)
            .add("email", email)
            .add("password", password)
            .add("birthDate", birthDate)
            .add("bio", bio)
            .add("profession", profession)
            .add("education", education)
            .add("height", height)
            .add("lifestyle", lifestyle)
            .add("lookingFor", lookingFor)
            .add("city", city)
            .add("state", state)
            .add("country", country)
            .add("origin", origin)
            .toString();
    }
}
