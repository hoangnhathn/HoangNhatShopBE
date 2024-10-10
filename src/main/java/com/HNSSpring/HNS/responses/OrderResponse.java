package com.HNSSpring.HNS.responses;

import com.HNSSpring.HNS.models.Order;
import com.HNSSpring.HNS.models.OrderDetail;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.stream.Collectors;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
@Getter
@Setter
public class OrderResponse {

    private Integer id;

    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("fullname")
    private String fullname;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("email")
    private String email;

    @JsonProperty("address")
    private String address;

    @JsonProperty("note")
    private String note;

    @JsonProperty("order_date")
    private LocalDate orderDate;

    @JsonProperty("status")
    private String status;

    @JsonProperty("total_money")
    private Double totalMoney;

    @JsonProperty("shipping_method")
    private String shippingMethod;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("shipping_address")
    private String shippingAddress;

    @JsonProperty("shipping_date")
    private LocalDate shippingDate;

    @JsonProperty("active")
    private Boolean active;

    @JsonProperty("order_details")
    private List<OrderDetailResponse> orderDetails;

    public static OrderResponse fromOrder(Order order) {
        OrderResponse orderResponse = OrderResponse
                .builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .fullname(order.getFullName())
                .phoneNumber(order.getPhoneNumber())
                .email(order.getEmail())
                .address(order.getAddress())
                .note(order.getNote())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalMoney(order.getTotalMoney())
                .shippingMethod(order.getShippingMethod())
                .paymentMethod(order.getPaymentMethod())
                .shippingAddress(order.getShippingAddress())
                .shippingDate(order.getShippingDate())
                .orderDetails(order.getOrderDetails().stream()
                        .map(orderDetail -> OrderDetailResponse.builder()
                                .id(orderDetail.getId())
                                .orderId(orderDetail.getOrder().getId())
                                .productId(orderDetail.getProduct().getId())
                                .productName(orderDetail.getProduct().getName())
                                .thumbnail(orderDetail.getProduct().getThumbnail())
                                .numberOfProducts(orderDetail.getNumberOfProducts())
                                .price(orderDetail.getPrice())
                                .totalMoney(orderDetail.getTotalMoney())
                                .build())
                        .collect(Collectors.toList()))
                .active(order.isActive())
                .build();
        return orderResponse;
    }
}
