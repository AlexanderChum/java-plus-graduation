package ru.yandex.practicum.category;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.category.dtos.CategoryDto;

import java.util.List;

@FeignClient(name = "category-service", contextId = "PublicCategory", path = "/categories")
public interface CategoryFeignClient {

    @GetMapping
    List<CategoryDto> getCategories(@PositiveOrZero
                                    @RequestParam(name = "from", defaultValue = "0")
                                    Integer from,

                                    @Positive
                                    @RequestParam(name = "size", defaultValue = "10")
                                    Integer size);

    @GetMapping("/{categoryId}")
    CategoryDto getCategoryById(@PathVariable Long categoryId);
}
