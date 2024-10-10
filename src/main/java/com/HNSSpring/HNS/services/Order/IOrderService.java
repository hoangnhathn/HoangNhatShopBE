package com.HNSSpring.HNS.services.Order;

import com.HNSSpring.HNS.dtos.OrderDTO;
import com.HNSSpring.HNS.models.Order;
import com.HNSSpring.HNS.responses.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IOrderService {
    Order createOrder(OrderDTO orderDTO) throws Exception;
    Order getOrder(Integer id);
    Order updateOrder(Integer id, OrderDTO orderDTO) throws Exception;
    void deleteOrder(Integer id);
    List<OrderResponse> findByUserId(Integer userId);
    Page<Order> getOrdersByKeyword(String keyword, Pageable pageable);
}
