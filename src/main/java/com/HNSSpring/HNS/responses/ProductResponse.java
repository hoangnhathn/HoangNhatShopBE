package com.HNSSpring.HNS.responses;

import com.HNSSpring.HNS.models.Product;
import com.HNSSpring.HNS.models.ProductImage;
import com.HNSSpring.HNS.models.Review;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse extends BaseResponse{

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("product_name")
    private String name;

    @JsonProperty("price")
    private Double price;

    @JsonProperty("thumbnail")
    private String thumbnail;

    @JsonProperty("description")
    private String description;

    @JsonProperty("category_id")
    private Integer categoryId;

    @JsonProperty("category_name")
    private String categoryName;

    @JsonProperty("rating") // Thêm trường này
    private Integer rating;

    @JsonProperty("product_images")
    private List<ProductImageResponse> productImages = new ArrayList<>();

    public static ProductResponse formProduct(Product product){
        ProductResponse productResponse = ProductResponse
                .builder()
                .id(product.getId()) // Cập nhật phương thức để thiết lập id
                .name(product.getName())
                .price(product.getPrice())
                .thumbnail(product.getThumbnail())
                .description(product.getDescription())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .build();
        // Cập nhật danh sách productImages
        List<ProductImageResponse> images = product.getProductImages().stream()
                .map(image -> ProductImageResponse.builder()
                        .id(image.getId())
                        .imageUrl(image.getImageUrl())
                        .build())
                .collect(Collectors.toList());
        // Tính trung bình rating
        List<Review> reviews = product.getReviews();

        if (reviews != null && !reviews.isEmpty()) {
            // Tính trung bình điểm rating
            int averageRating = (int) Math.round(reviews.stream()
                    .filter(review -> review.getRating() != null) // Loại bỏ các review có rating null
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0)); // Trả về 0 nếu không có review hợp lệ

            productResponse.setRating(averageRating);
        } else {
            productResponse.setRating(0); // Trả về 0 nếu không có review
        }

        productResponse.setProductImages(images);
        productResponse.setCreatedAt(product.getCreatedAt());
        productResponse.setUpdatedAt(product.getUpdatedAt());
        return productResponse;
    }


}
