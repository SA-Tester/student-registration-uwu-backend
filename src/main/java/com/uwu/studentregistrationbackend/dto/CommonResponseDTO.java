package com.uwu.studentregistrationbackend.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CommonResponseDTO<T> {
    private String message;
    private T data;
    private String error;
}
