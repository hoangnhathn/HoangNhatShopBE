package com.HNSSpring.HNS.repositories;

import com.HNSSpring.HNS.models.Product;
import com.HNSSpring.HNS.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<User> findByPhoneNumber(String phoneNumber);

    @Query("SELECT u FROM User u " +
            "WHERE (:roleId IS NULL OR :roleId = 0 OR u.role.id = :roleId) " +
            "AND (:keyword IS NULL OR :keyword = '' " +
            "OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(CAST(u.id AS string)) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<User> searchUsers(@Param("keyword") String keyword,
                           @Param("roleId") Integer roleId,
                           Pageable pageable);
}
