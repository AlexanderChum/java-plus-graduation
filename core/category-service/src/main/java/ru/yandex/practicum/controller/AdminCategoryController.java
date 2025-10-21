package ru.yandex.practicum.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.category.AdminCategoryFeignClient;
import ru.yandex.practicum.category.dtos.CategoryDto;
import ru.yandex.practicum.category.dtos.NewCategoryDto;
import ru.yandex.practicum.service.AdminCategoryService;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminCategoryController implements AdminCategoryFeignClient {
    AdminCategoryService service;

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createNewCategory(NewCategoryDto categoryDto) {
        log.info("Получен запрос на создание новой категории");
        return service.createNewCategory(categoryDto);
    }

    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(Long categoryId) {
        log.info("Получен запрос на удаление категории");
        service.deleteCategory(categoryId);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto) {
        log.info("Получен запрос на обновление категории");
        return service.updateCategory(categoryId, categoryDto);
    }
}
