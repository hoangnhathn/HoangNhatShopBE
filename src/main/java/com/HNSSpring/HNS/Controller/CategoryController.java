package com.HNSSpring.HNS.Controller;

import com.HNSSpring.HNS.components.LocalizationUtils;
import com.HNSSpring.HNS.dtos.CategoryDTO;
import com.HNSSpring.HNS.models.Category;
import com.HNSSpring.HNS.responses.CategoryListResponse;
import com.HNSSpring.HNS.responses.UpdateCategoryResponse;
import com.HNSSpring.HNS.services.Category.ICategoryService;
import com.HNSSpring.HNS.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor

public class CategoryController {
    private final ICategoryService categoryService;
    private final LocalizationUtils localizationUtils;
    //hiện tất cả categories
    @GetMapping("") // localhost:8080/api/v1/categories?page=1&limit=10
    public ResponseEntity<CategoryListResponse> getAllCategories(
            @RequestParam(defaultValue ="",required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sortField, // Thêm trường sắp xếp
            @RequestParam(defaultValue = "asc") String sortDirection // Thêm hướng sắp xếp
    ){
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        PageRequest pageRequest = PageRequest.of(page,limit,sort);
        Page<Category> categoryPage = categoryService.getCategoriesByKeyword(keyword,pageRequest);
        int totalPages = categoryPage.getTotalPages();
        List<Category> categories = categoryPage.getContent();
        return ResponseEntity.ok(CategoryListResponse
                .builder()
                .categories(categories)
                .totalPages(totalPages)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable("id") Integer id){
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @PostMapping("")
    public ResponseEntity<?> createCategory(
            @Valid @RequestBody CategoryDTO categoryDTO,
            BindingResult result){
        if (result.hasErrors()){
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(UpdateCategoryResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_CATEGORY_FAILED))
                    .build());
        }
        categoryService.createCategory(categoryDTO);
        return ResponseEntity.ok(UpdateCategoryResponse.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_CATEGORY_SUCCESSFULLY))
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateCategoryResponse> updateCategory(
            @PathVariable Integer id,
            @RequestBody @Valid  CategoryDTO categoryDTO){
        categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(UpdateCategoryResponse.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_CATEGORY_SUCCESSFULLY))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UpdateCategoryResponse> deleteCategory(@PathVariable Integer id){
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(UpdateCategoryResponse.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_CATEGORY_SUCCESSFULLY))
                .build());
    }


}
