package com.HNSSpring.HNS.services.OrderDetail;

import com.HNSSpring.HNS.dtos.OrderDetailDTO;
import com.HNSSpring.HNS.exception.DataNotFoundException;
import com.HNSSpring.HNS.models.Order;
import com.HNSSpring.HNS.models.OrderDetail;
import com.HNSSpring.HNS.models.Product;
import com.HNSSpring.HNS.repositories.OrderDetailRepository;
import com.HNSSpring.HNS.repositories.OrderRepository;
import com.HNSSpring.HNS.repositories.ProductRepository;
import com.HNSSpring.HNS.services.OrderDetail.IOrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailService implements IOrderDetailService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    @Override
    @Transactional
    public OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO) throws Exception{
        //tìm xem orderId có tồn ti ko
        Order order = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(()->new DataNotFoundException("Cannot find Order with id: "+orderDetailDTO.getOrderId()));
        //tìm product theo id
        Product product = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(()->new DataNotFoundException("Cannot find Order with id: "+orderDetailDTO.getProductId()));
        OrderDetail orderDetail = OrderDetail.builder()
                .order(order)
                .product(product)
                .numberOfProducts(orderDetailDTO.getNumberOfProducts())
                .price(orderDetailDTO.getPrice())
                .totalMoney(orderDetailDTO.getTotalMoney())
                .color(orderDetailDTO.getColor())
                .build();
        return orderDetailRepository.save(orderDetail);
    }

    @Override
    public OrderDetail getOrderDetail(Integer id) throws Exception{
        return orderDetailRepository.findById(id)
                .orElseThrow(()->new DataNotFoundException("Cannot not find OrderDetail with id: "+id));
    }

    @Override
    @Transactional
    public OrderDetail updateOrderDetail(Integer id, OrderDetailDTO orderDetailDTO) throws Exception {
        //tìm xem order detail có tồn tại ko
        OrderDetail existingOrderDetail = orderDetailRepository.findById(id)
                .orElseThrow(()->new DataNotFoundException("Cannot find order detail with id: "+id));
        Order existingOrder = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(()->new DataNotFoundException("Cannot find order with id: "+orderDetailDTO.getOrderId()));
        Product existingProduct = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(()->new DataNotFoundException("Cannot find Order with id: "+orderDetailDTO.getProductId()));
        existingOrderDetail.setPrice(orderDetailDTO.getPrice());
        existingOrderDetail.setNumberOfProducts(orderDetailDTO.getNumberOfProducts());
        existingOrderDetail.setTotalMoney(orderDetailDTO.getTotalMoney());
        existingOrderDetail.setColor(orderDetailDTO.getColor());
        existingOrderDetail.setOrder(existingOrder);
        existingOrderDetail.setProduct(existingProduct);

        return orderDetailRepository.save(existingOrderDetail);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        orderDetailRepository.deleteById(id);
    }

    @Override
    public List<OrderDetail> findByOrderId(Integer orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }
}
