package com.authenticationsystem.apiauthentication.dto;

import java.util.List;

import com.authenticationsystem.apiauthentication.models.Erole;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

	private Long id;
    private String username;
    private String email;

    @JsonProperty("is_active")
    private Boolean isActive;

    private List<Erole> roles;
}
