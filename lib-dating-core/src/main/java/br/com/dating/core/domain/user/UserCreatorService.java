package br.com.dating.core.domain.user;

import br.com.dating.core.domain.user.entity.UserEntity;

import br.com.dating.core.domain.user.repository.UserRepository;
import br.com.dating.core.domain.user.request.CreateUserRequest;

import br.com.dating.core.shared.outbox.OutboxCreationService;

import br.com.dating.core.shared.outbox.enums.EventType;

import br.com.dating.core.shared.utils.PublicFormats;
import dating.event.EventMetadata;
import dating.event.UserEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserCreatorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserCreatorService.class);

    private final UserRepository userRepository;
    private final OutboxCreationService outboxCreationService;
    private final String topic;

    public UserCreatorService(UserRepository userRepository,
                              OutboxCreationService outboxCreationService,
                              @Value("${kafka.producer.topic.user}") String topic) {
        this.userRepository = userRepository;
        this.outboxCreationService = outboxCreationService;
        this.topic = topic;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity execute(CreateUserRequest request) {

        LOGGER.info("stage=init, request={}", request);

        var user = new UserEntity();
        user.setCode(UUID.randomUUID());
        user.setName(request.name());
        user.setEmail(request.email());
        // @todo aplicar hashing com salt
        user.setPassword(request.password());
        user.setBirthDate(request.birthDate());
        user.setBio(request.bio());
        user.setProfession(request.profession());
        user.setEducation(request.education());
        user.setHeight(request.height());
        user.setLifestyle(request.lifestyle());
        user.setLookingFor(request.lookingFor());
        user.setCity(request.city());
        user.setState(request.state());
        user.setCountry(request.country());
        user.setKycStatus(UserEntity.KycStatus.PENDING);
        user.setPremium(false);
        user.setDailyLikes(0);
        user.setDailyMessages(0);
        user.setLastResetDate(LocalDate.now());

        user = userRepository.save(user);

        createOutboxEvent(request, user);

        LOGGER.info("stage=completed, request={}", request);

        return user;
    }

    private void createOutboxEvent(CreateUserRequest request, UserEntity user) {

        var eventId = UUID.randomUUID();

        var userEvent = UserEvent.newBuilder()
            .setEventType(EventType.USER_CREATED.name())
            .setName(request.name())
            .setCreatedAt(LocalDateTime.now().format(PublicFormats.EVENT_PATTERN))
            .setMetadata(EventMetadata.newBuilder()
                .setEventId(eventId.toString())
                .setEventDate(LocalDateTime.now().format(PublicFormats.EVENT_PATTERN))
                .setPartitionKey(user.getCode().toString())
                .setOrigin(request.origin().toString())
                .build()
            )
            .build();

        outboxCreationService.execute(topic, user.getCode().toString(), eventId, EventType.USER_CREATED, userEvent);
    }
}
