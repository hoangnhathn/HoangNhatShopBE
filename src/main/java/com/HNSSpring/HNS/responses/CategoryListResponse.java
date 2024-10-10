package com.HNSSpring.HNS.responses;

import com.HNSSpring.HNS.models.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class CategoryListResponse {
    List<Category> categories;
    private int totalPages;
}
