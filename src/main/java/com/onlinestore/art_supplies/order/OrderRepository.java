package com.onlinestore.art_supplies.order;

import com.onlinestore.art_supplies.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser_UserId(Long userId);

    Boolean existsByUserAndOrderItems_Product_ProductId(User user, Long productId);

}
