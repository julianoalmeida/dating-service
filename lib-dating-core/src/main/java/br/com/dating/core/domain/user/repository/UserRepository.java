package br.com.dating.core.domain.user.repository;

import br.com.dating.core.domain.user.entity.UserEntity;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByCode(UUID code);

    @Query("""
        SELECT u FROM UserEntity u
        WHERE u.id NOT IN :excludedIds
        AND u.city = :city
        AND u.state = :state
        AND u.kycStatus = 'APPROVED'
        ORDER BY u.createdAt DESC
        """)
    Page<UserEntity> findPotentialMatches(List<Long> excludedIds, String city, String state, PageRequest pageRequest);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")})
    @Query("SELECT u FROM UserEntity u WHERE u.code = :code") //@todo adicionar active = true
    @Transactional(propagation = Propagation.MANDATORY)
    Optional<UserEntity> findByCodeForUpdate(UUID code);
}
