package com.parpet.customer_management.dto.mapper;

import com.parpet.customer_management.dto.outgoing.CustomerDetails;
import com.parpet.customer_management.model.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {
    public CustomerDetails entitiesToDto(Customer customer) {
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setId(customer.getId());
        customerDetails.setName(customer.getName());
        customerDetails.setAge(customer.getAge());
        customerDetails.setDateOfBirth(customer.getDateOfBirth());
        customerDetails.setAddress(customer.getAddress());
        customerDetails.setGender(customer.getGender());
        return customerDetails;
    }
}
