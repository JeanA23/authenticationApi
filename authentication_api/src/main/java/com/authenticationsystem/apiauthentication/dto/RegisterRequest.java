package com.authenticationsystem.apiauthentication.dto;

import java.util.ArrayList;
import java.util.List;

import com.authenticationsystem.apiauthentication.models.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

	@NotBlank
	@Size(min = 3, max = 20)
	private String name;
	
	@NotBlank
	@Size(min = 3, max = 20)
	private String username;
	
	@NotBlank
	@Size(max = 50)
	@Email(message = "Please provide a valid email address")
	private String email;
	
	@NotBlank
	@Size(max = 50)
	private String password;
	
}
