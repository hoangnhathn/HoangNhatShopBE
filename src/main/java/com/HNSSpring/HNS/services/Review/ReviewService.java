package com.HNSSpring.HNS.services.Review;

import com.HNSSpring.HNS.dtos.ReviewDTO;
import com.HNSSpring.HNS.exception.DataNotFoundException;
import com.HNSSpring.HNS.models.Product;
import com.HNSSpring.HNS.models.Review;
import com.HNSSpring.HNS.models.User;
import com.HNSSpring.HNS.repositories.ProductRepository;
import com.HNSSpring.HNS.repositories.ReviewRepository;
import com.HNSSpring.HNS.repositories.UserRepository;
import com.HNSSpring.HNS.responses.ReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService{
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private final ReviewRepository reviewRepository;
    @Override
    @Transactional
    public Review insertReview(ReviewDTO reviewDTO) {
        // Tìm Product dựa trên productId từ reviewDTO
        Product product = productRepository.findById(reviewDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        User user = userRepository.findById(reviewDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Review newReview = Review.builder()
                .product(product)
                .user(user)
                .rating(reviewDTO.getRating())
                .comment(reviewDTO.getComment())
                .build();
        return reviewRepository.save(newReview);
    }

    @Override
    @Transactional
    public void deleteReview(Integer reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    @Override
    @Transactional
    public void updateReview(Integer id, ReviewDTO reviewDTO) throws Exception{
        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(()-> new DataNotFoundException("review not found"));
        existingReview.setComment(reviewDTO.getComment());
        reviewRepository.save(existingReview);

    }


    @Override
    public List<ReviewResponse> getCommentsByUserAndProduct(Integer userId, Integer productId) {
        List<Review> reviews = reviewRepository.findByUserIdAndProductId(userId, productId);
        return reviews.stream()
                .map(review ->  ReviewResponse.fromReview(review))
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getCommentsProduct(Integer productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);
        return reviews.stream()
                .map(review ->  ReviewResponse.fromReview(review))
                .collect(Collectors.toList());
    }
}
