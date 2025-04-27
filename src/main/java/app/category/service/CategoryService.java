package app.category.service;

import app.category.model.Category;
import app.category.repository.CategoryRepository;
import app.task.model.Task;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.CategoryCombinedWithTask;
import app.web.dto.CategoryRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        Category category = Category.builder()
                .name(categoryRequest.getCategoryName())
                .color(categoryRequest.getCategoryColor())
                .user(user)
                .isDeleted(false)
                .build();
        Category savedCategory = categoryRepository.save(category);
        if(user.getCategories()==null){
            user.setCategories(new ArrayList<>());
        }
        user.getCategories().add(savedCategory);
        userService.save(user);
    }


    public Category getById(UUID categoryId) {
       return categoryRepository.findById(categoryId).orElseThrow(()->new RuntimeException("Category not found"));
    }

    public void save(Category categoryById) {
        categoryRepository.save(categoryById);
    }
}
