package com.parpet.customer_management.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parpet.customer_management.dto.incoming.QueryDto;
import com.parpet.customer_management.dto.incoming.SortDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JsonUtils {
    private JsonUtils() {

    }

    public static List<SortDto> jsonStringToSortDto(String jsonString) {
        try {
            ObjectMapper obj = new ObjectMapper();
            return obj.readValue(jsonString, obj.getTypeFactory().constructCollectionType(List.class, SortDto.class));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON string", e);
        }
    }

    public static PageRequest jsonStringToPageRequest(QueryDto queryDto) {
        List<SortDto> sortDtos;
        try {
            ObjectMapper obj = new ObjectMapper();
            sortDtos = obj.readValue(queryDto.getSort(), obj.getTypeFactory().constructCollectionType(List.class, SortDto.class));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON string", e);
        }

        List<Sort.Order> orders = new ArrayList<>();

        if (sortDtos != null) {
            for (SortDto sortDto : sortDtos) {
                Sort.Direction direction = Objects.equals(sortDto.getDirection(), "desc")
                        ? Sort.Direction.DESC : Sort.Direction.ASC;
                orders.add(new Sort.Order(direction, sortDto.getField()));
            }
        }

        // Create page request with sorting
        return PageRequest.of(
                queryDto.getPage(),
                queryDto.getSize(),
                Sort.by(orders));
    }
}
