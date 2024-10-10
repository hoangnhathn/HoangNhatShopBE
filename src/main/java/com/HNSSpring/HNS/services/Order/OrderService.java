package com.HNSSpring.HNS.services.Order;

import com.HNSSpring.HNS.dtos.CartItemDTO;
import com.HNSSpring.HNS.dtos.OrderDTO;
import com.HNSSpring.HNS.exception.DataNotFoundException;
import com.HNSSpring.HNS.models.*;
import com.HNSSpring.HNS.repositories.OrderDetailRepository;
import com.HNSSpring.HNS.repositories.OrderRepository;
import com.HNSSpring.HNS.repositories.ProductRepository;
import com.HNSSpring.HNS.repositories.UserRepository;
import com.HNSSpring.HNS.responses.OrderResponse;
import com.HNSSpring.HNS.services.Order.IOrderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Override
    @Transactional
    public Order createOrder(OrderDTO orderDTO) throws Exception {
        //tìm kiếm user id có tồn tại ko
        User user= userRepository
                .findById(orderDTO.getUserId())
                .orElseThrow(()->new DataNotFoundException("Cannot find user with id: "+orderDTO.getUserId()));
        //convert orderDTO => order
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper ->mapper.skip(Order::setId));
        Order order = new Order();
        modelMapper.map(orderDTO,order);
        order.setUser(user);
        order.setOrderDate(LocalDate.now());//lấy thời gian hiện tại
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress(orderDTO.getShippingAddress());
        order.setAddress(user.getAddress());
        //kiểm tra shipping date phải >= ngày hôm nay
        LocalDate shippingDate = orderDTO.getShippingDate() == null ? LocalDate.now(): orderDTO.getShippingDate();
        if(shippingDate.isBefore(LocalDate.now())){
            throw new DataNotFoundException("Date must be at least today!");
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);
        order.setTotalMoney(orderDTO.getTotalMoney());


        //tạo danh sách đối tượng từ cartItemDTO
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartItemDTO cartItemDTO: orderDTO.getCartItems()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);

            Integer productId = cartItemDTO.getProductId();
            Integer quantity = cartItemDTO.getQuantity();

            Product product = productRepository.findById(productId)
                    .orElseThrow(()->new DataNotFoundException("Cannot find product with id: "+productId));

            orderDetail.setProduct(product);
            orderDetail.setNumberOfProducts(quantity);
            orderDetail.setPrice(product.getPrice());
            orderDetail.setTotalMoney(product.getPrice()*quantity);
            orderDetail.setColor("");
            orderDetails.add(orderDetail);
        }
        // Kiểm tra nếu danh sách orderDetails rỗng
        if (orderDetails.isEmpty()) {
            throw new Exception("Order details cannot be empty!");
        }
        orderRepository.save(order);
        orderDetailRepository.saveAll(orderDetails);
        return order;
    }

    @Override
    public Order getOrder(Integer id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Order updateOrder(Integer id, OrderDTO orderDTO) throws Exception{
        Order order = orderRepository
                .findById(id).orElseThrow(()->new DataNotFoundException("Cannot find order with id: "+id));
        User existingUser = userRepository
                .findById(orderDTO.getUserId()).orElseThrow(()->
                        new DataNotFoundException("Cannot find user with id: "+orderDTO.getUserId()));
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper ->mapper.skip(Order::setId))
                .addMappings(mapper -> mapper.skip(Order::setOrderDate));
        modelMapper.map(orderDTO,order);
        order.setUser(existingUser);
        order.setAddress(existingUser.getAddress());
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Integer id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null){
//            xóa mềm
            order.setActive(false);
            orderRepository.save(order);
//            xóa cứng
//            orderRepository.delete(order);
        }
    }

    @Override
    public List<OrderResponse> findByUserId(Integer userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        List<OrderResponse> orderResponses = orders.stream().map(OrderResponse::fromOrder).collect(Collectors.toList());
        return orderResponses;
    }

    @Override
    public Page<Order> getOrdersByKeyword(String keyword, Pageable pageable) {
        return orderRepository.findByKeyword(keyword, pageable);
    }
}
