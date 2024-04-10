package com.example.demo.model.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorDto {
    private List<String> message;
    
}
