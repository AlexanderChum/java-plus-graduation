package ru.yandex.practicum.service;

import ru.yandex.practicum.category.dtos.CategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Long categoryId);
}
