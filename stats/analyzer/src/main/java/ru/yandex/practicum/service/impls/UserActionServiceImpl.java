package ru.yandex.practicum.service.impls;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.mapper.UserActionMapper;
import ru.yandex.practicum.models.UserAction;
import ru.yandex.practicum.repositories.UserActionRepository;
import ru.yandex.practicum.service.UserActionService;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class UserActionServiceImpl implements UserActionService {
    UserActionMapper mapper;
    UserActionRepository repository;

    @Override
    public void saveUserAction(UserActionAvro request) {
        repository.save(mapper.toEntity(request));
    }

    @Override
    public List<UserAction> getMaxWeighted(List<Long> eventIds) {
        return findMaxWeightedForEvents(eventIds);
    }

    @Override
    public List<UserAction> getByUser(Long userId) {
        return repository.findAllByUserId(userId);
    }

    private List<UserAction> findMaxWeightedForEvents(List<Long> eventIds) {
        List<UserAction> allActions = repository.findByEventIdIn(eventIds);

        return allActions.stream()
                .collect(Collectors.groupingBy(
                        action -> action.getEventId() + "_" + action.getUserId(),
                        Collectors.maxBy(Comparator
                                .comparing(UserAction::getActionWeight)
                                .thenComparing(UserAction::getTimestamp)
                        )
                ))
                .values()
                .stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
