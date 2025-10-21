package main.server.category.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.category.dtos.CategoryDto;
import ru.yandex.practicum.category.dtos.NewCategoryDto;
import main.server.category.mapper.CategoryMapper;
import main.server.category.model.Category;
import main.server.category.repository.CategoryRepository;
import main.server.events.model.EventModel;
import main.server.events.services.PublicService;
import ru.yandex.practicum.errors.exceptions.ConflictException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.errors.exceptions.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    CategoryRepository categoryRepository;
    PublicService eventService;
    CategoryMapper mapper;

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        log.info("Попытка добавления новой категории");
        validateNameExist(newCategoryDto.getName());
        return mapper.toCategoryDto(categoryRepository.save(mapper.toCategory(newCategoryDto)));
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        log.info("Попытка удаления категории");
        List<EventModel> events = eventService.findAllByCategoryId(catId);
        if (events.isEmpty()) {
            categoryRepository.deleteById(catId);
        } else {
            throw new ConflictException("Категория не может быть удалена пока содержит события");
        }
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        log.info("Попытка обновления категории");
        Category category = findById(catId);
        if (!category.getName().equals(categoryDto.getName())) {
            validateNameExist(categoryDto.getName());
        }
        log.info("Обновление категории и ее возврат как ответа");
        mapper.updateCategoryFromDto(categoryDto, category);
        return mapper.toCategoryDto(category);
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        log.info("Попытка получения списка категорий");
        return categoryRepository.findAll().stream()
                .map(mapper::toCategoryDto)
                .skip(from)
                .limit(size)
                .toList();
    }

    @Override
    public CategoryDto getCategory(Long catId) {
        log.info("Попытка получения категории по id");
        return mapper.toCategoryDto(findById(catId));
    }

    private void validateNameExist(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new ConflictException("Название категории уже существует");
        }
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с id:" + id + " не найдена"));
    }
}