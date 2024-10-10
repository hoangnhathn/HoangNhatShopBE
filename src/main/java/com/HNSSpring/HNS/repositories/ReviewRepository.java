package com.HNSSpring.HNS.repositories;

import com.HNSSpring.HNS.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByUserIdAndProductId(Integer userId, Integer productId);
    List<Review> findByProductId(Integer productId);
}
