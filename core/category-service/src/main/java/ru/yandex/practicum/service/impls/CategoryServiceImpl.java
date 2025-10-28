package ru.yandex.practicum.service.impls;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.category.dtos.CategoryDto;
import ru.yandex.practicum.errors.exceptions.NotFoundException;
import ru.yandex.practicum.mapper.CategoryMapper;
import ru.yandex.practicum.model.Category;
import ru.yandex.practicum.repository.CategoryRepository;
import ru.yandex.practicum.service.CategoryService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    CategoryMapper mapper;
    CategoryRepository repository;

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        log.info("Запрос в сервис на получение нескольких категорий");
        return repository.findAll().stream()
                .skip(from)
                .limit(size)
                .map(mapper::toCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        log.info("Запрос в сервис на получение категории по id");
        Category category = repository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Категория с таким id = " + categoryId + " не найдена"));
        return mapper.toCategoryDto(category);
    }
}
