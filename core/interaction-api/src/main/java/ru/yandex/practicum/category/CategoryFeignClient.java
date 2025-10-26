package ru.yandex.practicum.category;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.category.dtos.CategoryDto;

@FeignClient(name = "category-service", path = "/categories")
public interface CategoryFeignClient {

    @GetMapping("/{categoryId}")
    CategoryDto getCategoryById(@PathVariable Long categoryId);
}
