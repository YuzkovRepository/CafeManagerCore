package com.example.CafeManagerCore.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SuccessResponseWithDataDTO<T> {
    private int status;
    private String message;
    private T data;
}
