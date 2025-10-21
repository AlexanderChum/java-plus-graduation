package ru.yandex.practicum.service.impls;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.category.dtos.CategoryDto;
import ru.yandex.practicum.category.dtos.NewCategoryDto;
import ru.yandex.practicum.errors.exceptions.NotFoundException;
import ru.yandex.practicum.mapper.CategoryMapper;
import ru.yandex.practicum.model.Category;
import ru.yandex.practicum.repository.CategoryRepository;
import ru.yandex.practicum.service.AdminCategoryService;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Transactional
public class AdminCategoryServiceImpl implements AdminCategoryService {
    CategoryMapper mapper;
    CategoryRepository repository;

    @Override
    public CategoryDto createNewCategory(NewCategoryDto categoryDto) {
        log.info("Запрос в сервис на создание новой категории");
        Category category = repository.save(mapper.toCategoryEntity(categoryDto));
        log.debug("Категория с id = {} создана", category.getId());
        return mapper.toCategoryDto(category);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        log.info("Запрос в сервис на удаление категории");
        repository.deleteById(categoryId);
        log.info("Запрос на удаление успешно выполнен");
    }

    @Override
    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto) {
        log.info("Запрос в сервис на обновление категории");
        Category category = repository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Категория с id " + categoryId + " не найдена"));
        mapper.updateCategoryFromDto(categoryDto, category);
        category = repository.save(category);
        log.debug("Категория с id = {} успешно обновлена", category.getId());
        return mapper.toCategoryDto(category);
    }
}
