package com.HNSSpring.HNS.services.Category;

import com.HNSSpring.HNS.dtos.CategoryDTO;
import com.HNSSpring.HNS.models.Category;
import com.HNSSpring.HNS.models.Product;
import com.HNSSpring.HNS.repositories.CategoryRepository;
import com.HNSSpring.HNS.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    @Override
    @Transactional
    public Category createCategory(CategoryDTO categoryDTO) {
        Category newCategory = Category.builder().name(categoryDTO.getName()).build();
        return categoryRepository.save(newCategory);
    }

    @Override
    public Category getCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public Page<Category> getCategoriesByKeyword(String keyword, Pageable pageable) {
        return categoryRepository.findByKeyword(keyword, pageable);
    }


    @Override
    @Transactional
    public Category updateCategory(Integer categoryId, CategoryDTO categoryDTO) {
        Category existingCategory = getCategoryById(categoryId);
        existingCategory.setName(categoryDTO.getName());
        categoryRepository.save(existingCategory);
        return existingCategory;
    }

    @Override
    @Transactional
    public void deleteCategory(Integer id) {

        List<Product> products = productRepository.findByCategoryId(id);
        for (Product product : products) {
            product.setCategory(null);
        }
        //xóa xong
        categoryRepository.deleteById(id);
    }
}
