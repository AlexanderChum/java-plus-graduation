package ru.yandex.practicum.category;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.category.dtos.CategoryDto;
import ru.yandex.practicum.category.dtos.NewCategoryDto;

@FeignClient(name = "category-service", path = "/admin/categories")
public interface AdminCategoryFeignClient {

    @PostMapping
    CategoryDto createNewCategory(@Valid @RequestBody NewCategoryDto categoryDto);

    @DeleteMapping("/{categoryId}")
    void deleteCategory(@PathVariable Long categoryId);

    @PatchMapping("/{categoryId}")
    CategoryDto updateCategory(@PathVariable Long categoryId, @Valid @RequestBody CategoryDto categoryDto);
}
