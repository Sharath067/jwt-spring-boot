package com.ecommerce.winz.dto;

import lombok.Data;

@Data
public class ForgotPasswordDTO {
    private String email;
    private String newPassword;
    private String confirmPassword;
}
