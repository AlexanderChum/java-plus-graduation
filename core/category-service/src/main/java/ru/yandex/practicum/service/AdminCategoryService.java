package ru.yandex.practicum.service;

import ru.yandex.practicum.category.dtos.CategoryDto;
import ru.yandex.practicum.category.dtos.NewCategoryDto;

public interface AdminCategoryService {
    CategoryDto createNewCategory(NewCategoryDto categoryDto);

    void deleteCategory(Long categoryId);

    CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto);
}
