package br.com.dating.core.domain.match.repository;

import br.com.dating.core.domain.match.entity.MatchEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<MatchEntity, Long> {

    Optional<MatchEntity> findByUserIdAndTargetUserId(Long userId, Long targetUserId);

    @Query("""
        SELECT m FROM MatchEntity m WHERE m.userId = :userId AND m.targetUserId = :targetUserId AND m.isMutual = true
        """)
    Optional<MatchEntity> findByUserIdAndTargetUserIdAndIsMutualTrue(
        Long userId, Long targetUserId);

    List<MatchEntity> findByUserIdAndIsMutualTrue(Long userId);

    List<MatchEntity> findByUserIdAndIsLikeTrue(Long userId);

    @Query("SELECT m.targetUserId FROM MatchEntity m WHERE m.userId = :userId")
    List<Long> findTargetUserIdsByUserId(Long userId);

    @Query("SELECT COUNT(m) FROM MatchEntity m WHERE m.userId = :userId AND m.createdAt = CURRENT_DATE")
    int countDailyLikesByUserId(Long userId);
}
