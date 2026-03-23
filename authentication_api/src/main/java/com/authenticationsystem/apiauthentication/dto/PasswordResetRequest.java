package com.authenticationsystem.apiauthentication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordResetRequest {

	@NotBlank
	private String token;

	@NotBlank
	@Size(min = 6)
	private String newPassword;
}
