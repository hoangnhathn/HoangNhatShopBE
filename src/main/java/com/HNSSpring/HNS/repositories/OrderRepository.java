package com.HNSSpring.HNS.repositories;

import com.HNSSpring.HNS.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    //Tìm các đơn hàng của 1 user nào đó
    List<Order> findByUserId(Integer userId);

    @Query("SELECT o FROM Order o WHERE " +
            "(:keyword IS NULL OR :keyword = '' " +
            "OR o.fullName LIKE CONCAT('%', :keyword, '%') " +
            "OR o.address LIKE CONCAT('%', :keyword, '%') " +
            "OR CAST(o.id AS string) LIKE CONCAT('%', :keyword, '%') " +
            "OR o.note LIKE CONCAT('%', :keyword, '%'))")
    Page<Order> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
