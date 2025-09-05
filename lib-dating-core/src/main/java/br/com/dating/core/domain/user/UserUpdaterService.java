package br.com.dating.core.domain.user;

import br.com.dating.core.shared.error.ErrorCode;
import br.com.dating.core.shared.exception.NotFoundException;
import br.com.dating.core.domain.user.entity.UserEntity;
import br.com.dating.core.domain.user.repository.UserRepository;

import br.com.dating.core.domain.user.request.UpdateUserRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserUpdaterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserUpdaterService.class);

    private final UserRepository userRepository;

    public UserUpdaterService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity execute(UpdateUserRequest request) throws NotFoundException {

        LOGGER.info("stage=init, request={}", request);

        var user = userRepository.findByCodeForUpdate(request.userCode())
            .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        //@todo complete all data update
        user.setBio(request.bio());
        user.setCity(request.city());

        user = userRepository.save(user);

        LOGGER.info("stage=completed, request={}", request);

        return user;
    }
}
