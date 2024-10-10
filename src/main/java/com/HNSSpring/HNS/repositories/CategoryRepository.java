package com.HNSSpring.HNS.repositories;

import com.HNSSpring.HNS.models.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    @Query("SELECT c  FROM Category c WHERE"+
            "(:keyword IS NULL OR :keyword = '' OR c.name LIKE %:keyword% OR CAST(c.id AS string) LIKE %:keyword%)")
    Page<Category> findByKeyword(@Param("keyword")String keyword, Pageable pageable);
}
