package com.parpet.customer_management.repository;

import com.parpet.customer_management.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

} 