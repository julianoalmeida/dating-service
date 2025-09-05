package br.com.dating.core.domain.user;

import br.com.dating.core.shared.error.ErrorCode;
import br.com.dating.core.shared.exception.NotFoundException;
import br.com.dating.core.domain.match.repository.MatchRepository;
import br.com.dating.core.domain.message.repository.MessageRepository;
import br.com.dating.core.domain.user.entity.UserEntity;

import br.com.dating.core.domain.user.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserFinderService {

    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final MessageRepository messageRepository;

    public UserFinderService(UserRepository userRepository,
                             MatchRepository matchRepository,
                             MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.matchRepository = matchRepository;
        this.messageRepository = messageRepository;
    }

    @Transactional(readOnly = true)
    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<UserEntity> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Page<UserEntity> findAll(PageRequest pageRequest) {
        return userRepository.findAll(pageRequest);
    }

    @Transactional(readOnly = true)
    public Optional<UserEntity> findByCode(UUID code) {
        return userRepository.findByCode(code);
    }

    @Transactional(readOnly = true)
    public Page<UserEntity> findPotentialMatch(List<Long> excludedIds,
                                               String city,
                                               String state,
                                               PageRequest pageRequest) {
        return userRepository.findPotentialMatches(excludedIds, city, state, pageRequest);
    }

    @Transactional(readOnly = true)
    public boolean canLike(UUID userCode) throws NotFoundException {

        var user = userRepository.findByCode(userCode)
            .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        if (user.isPremium()) {
            return true; // Premium users have unlimited likes
        }

        // Free users: 10 likes per day
        int dailyLikes = matchRepository.countDailyLikesByUserId(user.getId());
        return dailyLikes < 10;
    }

    public boolean canSendMessage(UUID userCode) throws NotFoundException {

        var user = userRepository.findByCode(userCode)
            .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        if (user.isPremium()) {
            return true; // Premium users have unlimited messages
        }

        // Free users: 10 messages per day
        int dailyMessages = messageRepository.countDailyMessagesByUserId(user.getId());
        return dailyMessages < 10;
    }

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException(ErrorCode.USER_NOT_FOUND + email));

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getEmail())
            .password(user.getPassword())
            .authorities("ROLE_USER")
            .build();
    }
}
