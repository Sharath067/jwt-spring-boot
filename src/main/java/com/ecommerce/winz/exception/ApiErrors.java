package com.ecommerce.winz.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiErrors {
    private int status;
    private String error;
    private String message;

//    public ApiErrors(int status, String error, String message) {
//        this.status = status;
//        this.error = error;
//        this.message = message;
//    }
}
