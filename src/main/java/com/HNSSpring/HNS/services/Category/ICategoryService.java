package com.HNSSpring.HNS.services.Category;

import com.HNSSpring.HNS.dtos.CategoryDTO;
import com.HNSSpring.HNS.models.Category;
import com.HNSSpring.HNS.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICategoryService {

    Category createCategory(CategoryDTO category);
    Category getCategoryById(Integer id);
    Category updateCategory(Integer categoryId, CategoryDTO category);
    void deleteCategory(Integer id);
    public Page<Category> getCategoriesByKeyword(String keyword, Pageable pageable);
}
