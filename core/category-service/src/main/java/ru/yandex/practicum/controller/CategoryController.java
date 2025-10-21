package ru.yandex.practicum.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.category.CategoryFeignClient;
import ru.yandex.practicum.category.dtos.CategoryDto;
import ru.yandex.practicum.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController implements CategoryFeignClient {
    CategoryService service;

    @Override
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        log.info("Получен запрос на получение категорий");
        return service.getCategories(from, size);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategoryById(Long categoryId) {
        log.info("Получен запрос на получение категории по id");
        return service.getCategoryById(categoryId);
    }
}
