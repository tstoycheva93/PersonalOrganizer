package app.category.service;

import app.category.model.Category;
import app.category.repository.CategoryRepository;
import app.task.model.Task;
import app.user.model.User;
import app.exception.*;
import app.user.service.UserService;
import app.web.dto.CategoryCombinedWithTask;
import app.web.dto.CategoryRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserService userService;

    public CategoryService(CategoryRepository categoryRepository, UserService userService) {
        this.categoryRepository = categoryRepository;
        this.userService = userService;
    }

    public List<CategoryCombinedWithTask> makeCombinedObject(List<Category> categories, LocalDate parse) {
        List<CategoryCombinedWithTask> categoryCombinedWithTasks = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        for (Category category : categories) {
            for (Task task : category.getTasks()) {
                if (parse.equals(task.getDueDate().toLocalDate())) {
                    CategoryCombinedWithTask c = new CategoryCombinedWithTask();
                    c.setColor(category.getColor());
                    c.setPriority(task.getPriority());
                    c.setTitleTask(task.getTitle());
                    c.setDate(task.getDueDate().toLocalTime().format(formatter));
                    categoryCombinedWithTasks.add(c);
                }
            }
        }
        return categoryCombinedWithTasks;
    }

    public void createCategory(User user, CategoryRequest categoryRequest) {
        if (categoryRequest.getCategoryName() == null || categoryRequest.getCategoryName().isEmpty()
                || categoryRequest.getCategoryName().length() < 4 || categoryRequest.getCategoryName().length() > 50) {
            throw new CategoryInvalidLengthException("The category size must be between 4 and 50 characters");
        }
        if (categoryRequest.getCategoryColor() == null || categoryRequest.getCategoryColor().isEmpty()
                || categoryRequest.getCategoryColor().length() < 4 || categoryRequest.getCategoryColor().length() > 50) {
            throw new InvalidColorException("The color is invalid");
        }

        Category category = Category.builder()
                .name(categoryRequest.getCategoryName())
                .color(categoryRequest.getCategoryColor())
                .user(user)
                .isDeleted(false)
                .build();
        Category savedCategory = categoryRepository.save(category);
        if (user.getCategories() == null) {
            user.setCategories(new ArrayList<>());
        }
        user.getCategories().add(savedCategory);
        userService.save(user);
    }


    public Category getById(UUID categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found"));
    }

    public void save(Category categoryById) {
        categoryRepository.save(categoryById);
    }

    public Map<String, Integer> getCountOfCategoriesAndTheirTasks(User user) {
        Map<String, Integer> countOfCategoriesAndTheirTasks = new HashMap<>();
        for (Category category : user.getCategories()) {
            countOfCategoriesAndTheirTasks.put(category.getName(), category.getTasks().size());
        }

        return countOfCategoriesAndTheirTasks;
    }

    public List<String> getCategoryNames(User user) {
        List<String> categoryNames = new ArrayList<>();
        for (Category category : user.getCategories()) {
            categoryNames.add(category.getName());
        }
        return categoryNames;
    }
}
