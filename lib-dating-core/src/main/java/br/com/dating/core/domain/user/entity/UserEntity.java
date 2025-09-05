package br.com.dating.core.domain.user.entity;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalDate;

import java.util.UUID;

import static br.com.dating.core.domain.user.entity.UserEntity.ENTITY_NAME;
import static br.com.dating.core.domain.user.entity.UserEntity.TABLE_NAME;

@Entity(name = ENTITY_NAME)
@Table(name = TABLE_NAME,
    indexes = {
        @Index(name = "USERCODE_UK01", columnList = "USER_CODE, FLG_ACTIVE = 1", unique = true),
        @Index(name = "USEREMAIL_UK01", columnList = "USER_EMAIL, FLG_ACTIVE = 1", unique = true),
        @Index(name = "USERCODE_IDX01", columnList = "USER_CODE"),
    }
)
public class UserEntity implements Serializable {

    protected static final String ENTITY_NAME = "UserEntity";
    protected static final String TABLE_NAME = "users";
    private static final String SEQUENCE_NAME = "sq_user_id";

    /**
     * Identificação da entidade no banco.
     */
    @Id
    @Column(name = "user_id", nullable = false, length = 19)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(
        name = SEQUENCE_NAME,
        sequenceName = SEQUENCE_NAME, allocationSize = 1
    )
    private Long id;

    /**
     * Identificador/Código do usuário
     */
    @Column(unique = true, nullable = false, length = 16)
    private UUID code;

    /**
     * Email do usuário.
     */
    @Column(unique = true, nullable = false)
    @Email
    private String email;

    /**
     * Senha do usuário.
     */
    @Column(name = "USER_PASSWORD", nullable = false)
    @JsonIgnore
    private String password;

    @Column(nullable = false)
    @NotBlank
    private String name;

    @Column(nullable = false)
    @Past
    private LocalDate birthDate;

    @Column
    private String bio;

    @Column
    private String profession;

    @Column
    private String education;

    @Column
    private Integer height;

    @Column
    private String bodyType;

    @Column
    private String lifestyle;

    @Column
    private String lookingFor;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column
    private String city;

    @Column
    private String state;

    @Column
    private String country;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private KycStatus kycStatus = KycStatus.PENDING;

    @Column(nullable = false)
    private Boolean isPremium = false;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column
    private Integer dailyLikes = 0;

    @Column
    private Integer dailyMessages = 0;

    @Column
    private LocalDate lastResetDate;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public UserEntity() {
    }

    public UserEntity(String email, String password, String name, LocalDate birthDate) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.birthDate = birthDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public UUID getCode() {
        return code;
    }

    public void setCode(UUID code) {
        this.code = code;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
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

    public KycStatus getKycStatus() {
        return kycStatus;
    }

    public void setKycStatus(KycStatus kycStatus) {
        this.kycStatus = kycStatus;
    }

    public Boolean isPremium() {
        return isPremium;
    }

    public void setPremium(Boolean premium) {
        isPremium = premium;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
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

    public LocalDate getLastResetDate() {
        return lastResetDate;
    }

    public void setLastResetDate(LocalDate lastResetDate) {
        this.lastResetDate = lastResetDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserEntity userEntity = (UserEntity) o;
        return Objects.equal(id, userEntity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("id", id)
            .add("code", code)
            .add("email", email)
            .add("password", password)
            .add("name", name)
            .add("birthDate", birthDate)
            .add("bio", bio)
            .add("profession", profession)
            .add("education", education)
            .add("height", height)
            .add("bodyType", bodyType)
            .add("lifestyle", lifestyle)
            .add("lookingFor", lookingFor)
            .add("latitude", latitude)
            .add("longitude", longitude)
            .add("city", city)
            .add("state", state)
            .add("country", country)
            .add("kycStatus", kycStatus)
            .add("isPremium", isPremium)
            .add("isActive", isActive)
            .add("dailyLikes", dailyLikes)
            .add("dailyMessages", dailyMessages)
            .add("lastResetDate", lastResetDate)
            .add("createdAt", createdAt)
            .add("updatedAt", updatedAt)
            .toString();
    }

    // Utility methods
    public int getAge() {
        return LocalDate.now().getYear() - birthDate.getYear();
    }

    public boolean canLike() {
        if (isPremium) {
            return true;
        }
        resetDailyLimitsIfNeeded();
        return dailyLikes < 10;
    }

    public boolean canMessage() {
        if (isPremium) {
            return true;
        }
        resetDailyLimitsIfNeeded();
        return dailyMessages < 10;
    }

    private void resetDailyLimitsIfNeeded() {
        LocalDate today = LocalDate.now();
        if (lastResetDate == null || !lastResetDate.equals(today)) {
            dailyLikes = 0;
            dailyMessages = 0;
            lastResetDate = today;
        }
    }

    public enum KycStatus {
        PENDING, UNDER_REVIEW, APPROVED, REJECTED
    }
}
