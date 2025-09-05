package br.com.dating.core.domain.match.entity;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

import static br.com.dating.core.domain.match.entity.MatchEntity.ENTITY_NAME;
import static br.com.dating.core.domain.match.entity.MatchEntity.TABLE_NAME;

@Entity(name = ENTITY_NAME)
@Table(name = TABLE_NAME)
public class MatchEntity {

    protected static final String ENTITY_NAME = "MatchEntity";
    protected static final String TABLE_NAME = "matches";
    private static final String SEQUENCE_NAME = "sq_match_id";

    /**
     * Identificação da entidade no banco.
     */
    @Id
    @Column(nullable = false, length = 19)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(
        name = SEQUENCE_NAME,
        sequenceName = SEQUENCE_NAME, allocationSize = 1
    )
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "target_user_id", nullable = false)
    private Long targetUserId;

    @Column(name = "is_like", nullable = false)
    private Boolean isLike = false;

    @Column(name = "is_mutual", nullable = false)
    private Boolean isMutual = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public MatchEntity() {
    }

    public MatchEntity(Long userId, Long targetUserId, Boolean isLike) {
        this.userId = userId;
        this.targetUserId = targetUserId;
        this.isLike = isLike;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public Boolean getIsLike() {
        return isLike;
    }

    public void setIsLike(Boolean isLike) {
        this.isLike = isLike;
    }

    public Boolean getIsMutual() {
        return isMutual;
    }

    public void setIsMutual(Boolean isMutual) {
        this.isMutual = isMutual;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MatchEntity matchEntity = (MatchEntity) o;
        return Objects.equal(id, matchEntity.id) && Objects.equal(targetUserId, matchEntity.targetUserId) && Objects.equal(isLike,
            matchEntity.isLike
        ) && Objects.equal(isMutual, matchEntity.isMutual) && Objects.equal(createdAt, matchEntity.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, targetUserId, isLike, isMutual, createdAt);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("id", id)
            .add("targetUserId", targetUserId)
            .add("isLike", isLike)
            .add("isMutual", isMutual)
            .add("createdAt", createdAt)
            .toString();
    }
}
