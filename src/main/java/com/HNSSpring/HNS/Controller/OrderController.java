package com.HNSSpring.HNS.Controller;

import com.HNSSpring.HNS.components.LocalizationUtils;
import com.HNSSpring.HNS.dtos.OrderDTO;
import com.HNSSpring.HNS.models.Order;
import com.HNSSpring.HNS.responses.OrderListResponse;
import com.HNSSpring.HNS.responses.OrderResponse;
import com.HNSSpring.HNS.services.Order.IOrderService;
import com.HNSSpring.HNS.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;
    private final LocalizationUtils localizationUtils;
    @PostMapping("")
    public ResponseEntity<?> createOrder(
            @RequestBody @Valid OrderDTO orderDTO,
            BindingResult result
    ){
        try{
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            Order order = orderService.createOrder(orderDTO);
            return ResponseEntity.ok(order);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{user_id}")
    public ResponseEntity<?> getOrders(@Valid @PathVariable("user_id") Integer userId){
        try {
            List<OrderResponse> orders = orderService.findByUserId(userId);
            return ResponseEntity.ok(orders);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@Valid @PathVariable("id") Integer orderId){
        try {
            Order existingOrder = orderService.getOrder(orderId);
            return ResponseEntity.ok(OrderResponse.fromOrder(existingOrder));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(
            @Valid @PathVariable Integer id,
            @Valid @RequestBody OrderDTO orderDTO
    ){
        try {
            Order order = orderService.updateOrder(id, orderDTO);
            return ResponseEntity.ok(OrderResponse.fromOrder(order));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@Valid @PathVariable Integer id){
        orderService.deleteOrder(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", localizationUtils.getLocalizedMessage(MessageKeys.DELETE_ORDER_SUCCESSFULLY));
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/get-orders-by-keyword")
    public ResponseEntity<OrderListResponse> getOrdersByKeyword(
            @RequestParam(defaultValue ="",required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sortField, // Thêm trường sắp xếp
            @RequestParam(defaultValue = "asc") String sortDirection // Thêm hướng sắp xếp
    ){
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        //tạo pageable từ trang và giới hạn
        PageRequest pageRequest = PageRequest.of(page,limit,sort);
        Page<OrderResponse> orderPage = orderService.getOrdersByKeyword(keyword,pageRequest).map(OrderResponse::fromOrder);
        //lấy tổng số trang
        int totalPages = orderPage.getTotalPages();
        List<OrderResponse> orders = orderPage.getContent();

        return ResponseEntity.ok(OrderListResponse
                .builder()
                .orders(orders)
                .totalPages(totalPages)
                .build());
    }
}
