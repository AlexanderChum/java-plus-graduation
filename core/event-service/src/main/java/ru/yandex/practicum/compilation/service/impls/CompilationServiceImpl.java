package ru.yandex.practicum.compilation.service.impls;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.compilation.mapper.CompilationMapper;
import ru.yandex.practicum.compilation.repository.CompilationRepository;
import ru.yandex.practicum.compilation.dto.CompilationDto;
import ru.yandex.practicum.compilation.dto.CompilationUpdateDto;
import ru.yandex.practicum.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.compilation.model.Compilation;
import ru.yandex.practicum.compilation.service.CompilationService;
import ru.yandex.practicum.errors.exceptions.NotFoundException;
import ru.yandex.practicum.event.dtos.EventShortDto;
import ru.yandex.practicum.service.PrivateEventService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class CompilationServiceImpl implements CompilationService {
    CompilationRepository repository;
    CompilationMapper mapper;
    PrivateEventService eventService;

    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        log.info("Получение запроса на создание подборки в сервисе");
        Compilation compilation = mapper.toEntity(newCompilationDto);

        if (newCompilationDto.getPinned() == null) {
            compilation.setPinned(false);
        } else {
            compilation.setPinned(newCompilationDto.getPinned());
        }

        log.info("Маппинг и проверка статуса пройдены");

        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            compilation.setEvents(newCompilationDto.getEvents().stream().toList());
        } else {
            compilation.setEvents(new ArrayList<>());
        }

        log.info("Добавление событий в подборку");

        List<EventShortDto> events = eventService.findAllById(compilation.getEvents());
        Compilation savedCompilation = repository.save(compilation);
        log.info("Создаем подборку");
        return mapper.toDto(savedCompilation, events);
    }

    @Override
    public void deleteCompilation(Long compId) {
        if (!repository.existsById(compId)) {
            throw new NotFoundException("Событие с id " + compId + " не найдено");
        }
        log.info("Удаляем подборку id={}", compId);
        repository.deleteById(compId);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, CompilationUpdateDto updateDto) {
        Compilation compilation = repository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Событие с id " + compId + " не найдено"));
        if (updateDto.getTitle() != null && !updateDto.getTitle().isBlank()) {
            compilation.setTitle(updateDto.getTitle());
        }
        if (updateDto.getPinned() != null) {
            compilation.setPinned(updateDto.getPinned());
        }
        List<EventShortDto> events = new ArrayList<>();
        if (updateDto.getEvents() != null && !updateDto.getEvents().isEmpty()) {
            List<Long> eventIds = new ArrayList<>(updateDto.getEvents());
            events = eventService.findAllById(eventIds);
            compilation.setEvents(eventIds);
        }
        log.info("Обновляем подборку id={}", compId);
        return mapper.toDto(compilation, events);
    }

    @Transactional(readOnly = true)
    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = repository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Событие с id " + compId + " не найдено"));
        List<EventShortDto> events = new ArrayList<>();
        if (compilation.getEvents() != null && !compilation.getEvents().isEmpty()) {
            events = eventService.findAllById(compilation.getEvents());
        }
        return mapper.toDto(compilation, events);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from, size);
        List<Compilation> compilations;
        if (pinned != null) {
            compilations = repository.findAllByPinned(pinned, pageRequest);
        } else {
            compilations = repository.findAll(pageRequest).getContent();
        }

        Set<Long> allEventIds = new HashSet<>();
        for (Compilation compilation : compilations) {
            allEventIds.addAll(compilation.getEvents());
        }
        List<EventShortDto> allEvents = eventService.findAllById(new ArrayList<>(allEventIds));

        Map<Long, EventShortDto> eventMap = new HashMap<>();
        for (EventShortDto event : allEvents) {
            eventMap.put(event.getId(), event);
        }

        List<CompilationDto> result = new ArrayList<>();
        for (Compilation compilation : compilations) {
            List<EventShortDto> eventsForCompilation = new ArrayList<>();
            for (Long eventId : compilation.getEvents()) {
                EventShortDto event = eventMap.get(eventId);
                if (event != null) {
                    eventsForCompilation.add(event);
                }
            }
            CompilationDto compilationDto = mapper.toDto(compilation, eventsForCompilation);
            result.add(compilationDto);
        }

        return result;
    }
}
