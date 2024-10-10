package com.HNSSpring.HNS.responses;

import com.HNSSpring.HNS.models.Order;
import com.HNSSpring.HNS.models.OrderDetail;
import com.HNSSpring.HNS.models.Product;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
@Getter
@Setter
public class OrderDetailResponse {
    private Integer id;

    @JsonProperty("order_id")
    private Integer orderId;

    @JsonProperty("product_id")
    private Integer productId;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("thumbnail")
    private String thumbnail;

    @JsonProperty("price")
    private Double price;

    @JsonProperty("number_of_products")
    private Integer numberOfProducts;

    @JsonProperty("total_money")
    private Double totalMoney;

    @JsonProperty("color")
    private String color;

    public static OrderDetailResponse fromOrderDetail(OrderDetail orderDetail){

        return OrderDetailResponse
                .builder()
                .id(orderDetail.getId())
                .orderId(orderDetail.getOrder().getId())
                .productId(orderDetail.getProduct().getId())
                .productName(orderDetail.getProduct().getName())
                .thumbnail(orderDetail.getProduct().getThumbnail())
                .price(orderDetail.getPrice())
                .numberOfProducts(orderDetail.getNumberOfProducts())
                .totalMoney(orderDetail.getTotalMoney())
                .color(orderDetail.getColor())
                .build();

    }
}
