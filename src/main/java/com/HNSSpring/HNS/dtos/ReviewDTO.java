package com.HNSSpring.HNS.dtos;

import com.HNSSpring.HNS.models.Category;
import com.HNSSpring.HNS.models.Product;
import com.HNSSpring.HNS.models.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewDTO {

    @JsonProperty("product_id")
    private Integer productId;

    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("rating")
    private Integer rating;

    @JsonProperty("comment")
    private String comment;

}
