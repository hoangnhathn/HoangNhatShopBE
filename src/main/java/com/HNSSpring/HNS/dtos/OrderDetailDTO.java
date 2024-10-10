package com.HNSSpring.HNS.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDTO {
    @JsonProperty("order_id")
    @Min(value = 1, message = "Order's ID must be > 0")
    private Integer orderId;

    @Min(value = 1,message = "Product's ID must be > 0")
    @JsonProperty("product_id")
    private Integer productId;

    @Min(value = 0,message = "Price must be >= 0")
    private Double price;

    @Min(value = 1,message = "Number product must be > 0")
    @JsonProperty("number_of_products")
    private Integer numberOfProducts;

    @Min(value = 0,message = "Product's ID must be >= 0")
    @JsonProperty("total_money")
    private Double totalMoney;

    private String color;
}
