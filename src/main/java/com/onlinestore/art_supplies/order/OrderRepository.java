package com.onlinestore.art_supplies.order;

import com.onlinestore.art_supplies.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.user.userId = :userId")
    List<Order> findByUser_UserId(@Param("userId") Long userId);

    Boolean existsByUserAndOrderItems_Product_ProductId(User user, Long productId);
}
