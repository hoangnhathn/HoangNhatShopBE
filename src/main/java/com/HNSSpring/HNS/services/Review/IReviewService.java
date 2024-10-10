package com.HNSSpring.HNS.services.Review;

import com.HNSSpring.HNS.dtos.ReviewDTO;
import com.HNSSpring.HNS.models.Review;
import com.HNSSpring.HNS.responses.ReviewResponse;

import java.util.List;

public interface IReviewService {
    Review insertReview(ReviewDTO reviewDTO);
    void deleteReview(Integer reviewId);
    void updateReview(Integer id, ReviewDTO reviewDTO) throws Exception;
    List<ReviewResponse> getCommentsByUserAndProduct(Integer userId, Integer productId);
    List<ReviewResponse> getCommentsProduct(Integer productId);
}
