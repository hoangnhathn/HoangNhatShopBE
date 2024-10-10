package com.HNSSpring.HNS.services.OrderDetail;

import com.HNSSpring.HNS.dtos.OrderDetailDTO;
import com.HNSSpring.HNS.models.OrderDetail;

import java.util.List;

public interface IOrderDetailService {
    OrderDetail createOrderDetail(OrderDetailDTO newOrderDetail) throws Exception;
    OrderDetail getOrderDetail(Integer id) throws Exception;
    OrderDetail updateOrderDetail(Integer id, OrderDetailDTO newOrderDetailData) throws Exception;
    void deleteById(Integer id);
    List<OrderDetail> findByOrderId(Integer orderId);
}
