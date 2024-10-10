package com.HNSSpring.HNS.Controller;

import com.HNSSpring.HNS.dtos.ReviewDTO;
import com.HNSSpring.HNS.models.Review;
import com.HNSSpring.HNS.models.User;
import com.HNSSpring.HNS.responses.ProductListResponse;
import com.HNSSpring.HNS.responses.ProductResponse;
import com.HNSSpring.HNS.responses.ReviewResponse;
import com.HNSSpring.HNS.services.Review.ReviewService;
import com.HNSSpring.HNS.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("")
    public ResponseEntity<List<ReviewResponse>> getAllReviews(
            @RequestParam(value = "user_id", required = false) Integer userId,
            @RequestParam("product_id") Integer productId
    ){


        List<ReviewResponse> reviews;
        if(userId == null){
            reviews = reviewService.getCommentsProduct(productId);
        } else {
            reviews = reviewService.getCommentsByUserAndProduct(userId, productId);
        }

        return ResponseEntity.ok(reviews);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(
            @PathVariable Integer id,
            @Valid @RequestBody ReviewDTO reviewDTO
            )
    {
        try{
            User loginUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if(loginUser.getId() != reviewDTO.getUserId()){
                return ResponseEntity.badRequest().body("you cannot update another user's review");
            }
            reviewService.updateReview(id, reviewDTO);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Update review successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during review update");
        }
    }

    @PostMapping("")
    public ResponseEntity<?> insertReview(
        @Valid @RequestBody ReviewDTO reviewDTO
    ) {
        try{
            reviewService.insertReview(reviewDTO);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Insert review succesfully");
            return ResponseEntity.ok(response);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during review insertion");
        }
    }
}
