package com.HNSSpring.HNS.responses;

import com.HNSSpring.HNS.models.Review;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewResponse {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("user")
    private UserResponse userResponse;

    @JsonProperty("product_id")
    private Integer productId;

    @JsonProperty("rating")
    private Integer rating;

    @JsonProperty("comment")
    private String comment;

    public static ReviewResponse fromReview(Review review){
        ReviewResponse reviewResponsee = ReviewResponse
                .builder()
                .id(review.getId()) // Cập nhật phương thức để thiết lập id
                .userResponse(UserResponse.fromUser(review.getUser()))
                .productId(review.getProduct().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .build();

        return reviewResponsee;
    }
}
