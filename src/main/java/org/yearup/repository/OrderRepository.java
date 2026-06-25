package org.yearup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yearup.models.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}