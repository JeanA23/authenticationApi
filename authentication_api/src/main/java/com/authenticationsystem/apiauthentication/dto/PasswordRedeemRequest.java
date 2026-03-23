package com.authenticationsystem.apiauthentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordRedeemRequest {

	@Email
	@NotBlank
    private String email;
}
