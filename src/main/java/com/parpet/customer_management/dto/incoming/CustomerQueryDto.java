package com.parpet.customer_management.dto.incoming;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CustomerQueryDto {
    private Integer page;
    private Integer size;
    private String sort;
}
