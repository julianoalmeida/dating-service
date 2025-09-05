package br.com.dating.api.resources.v1.user.response;

import br.com.dating.core.domain.user.entity.UserEntity;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Schema(description = "Dados do usuário")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private Integer age;
    private LocalDate birthDate;
    private String bio;
    private String profession;
    private String education;
    private Integer height;
    private String bodyType;
    private String lifestyle;
    private String lookingFor;
    private String city;
    private String state;
    private String country;
    private Boolean isPremium;
    private UserEntity.KycStatus kycStatus;
    private Integer dailyLikes;
    private Integer dailyMessages;
    private LocalDateTime createdAt;

    // Constructors
    public UserResponse() {
    }

    public UserResponse(UserEntity user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.age = user.getAge();
        this.birthDate = user.getBirthDate();
        this.bio = user.getBio();
        this.profession = user.getProfession();
        this.education = user.getEducation();
        this.height = user.getHeight();
        this.lifestyle = user.getLifestyle();
        this.lookingFor = user.getLookingFor();
        this.city = user.getCity();
        this.state = user.getState();
        this.country = user.getCountry();
        this.isPremium = user.isPremium();
        this.kycStatus = user.getKycStatus();
        this.dailyLikes = user.getDailyLikes();
        this.dailyMessages = user.getDailyMessages();
        this.createdAt = user.getCreatedAt();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
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

    public String getBodyType() {
        return bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
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

    public Boolean getIsPremium() {
        return isPremium;
    }

    public void setIsPremium(Boolean isPremium) {
        this.isPremium = isPremium;
    }

    public UserEntity.KycStatus getKycStatus() {
        return kycStatus;
    }

    public void setKycStatus(UserEntity.KycStatus kycStatus) {
        this.kycStatus = kycStatus;
    }

    public Integer getDailyLikes() {
        return dailyLikes;
    }

    public void setDailyLikes(Integer dailyLikes) {
        this.dailyLikes = dailyLikes;
    }

    public Integer getDailyMessages() {
        return dailyMessages;
    }

    public void setDailyMessages(Integer dailyMessages) {
        this.dailyMessages = dailyMessages;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
