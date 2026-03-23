package com.authenticationsystem.apiauthentication.web;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authenticationsystem.apiauthentication.dto.LoginRequest;
import com.authenticationsystem.apiauthentication.dto.PasswordRedeemRequest;
import com.authenticationsystem.apiauthentication.dto.PasswordResetRequest;
import com.authenticationsystem.apiauthentication.dto.RegisterRequest;
import com.authenticationsystem.apiauthentication.repositories.RefreshTokenRepository;
import com.authenticationsystem.apiauthentication.securityJwt.JwtUtils;
import com.authenticationsystem.apiauthentication.services.AuthService;
import com.authenticationsystem.apiauthentication.services.RefreshTokenService;
import com.authenticationsystem.apiauthentication.utils.Response;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final RefreshTokenRepository refreshTokenRepository;
	private final RefreshTokenService refreshTokenService;
	private final JwtUtils jwtUtils;

	@PostMapping(path = "/register")
	public ResponseEntity<Object> register(@Valid @RequestBody RegisterRequest request) {

		return ResponseEntity.ok(authService.register(request));
	}

	@PostMapping("/login")
	public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest request) {

		return ResponseEntity.ok(authService.login(request));
	}

	@GetMapping("/users")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<Object> getUser() {

		return ResponseEntity.ok(authService.getUser());
	}

	@PostMapping("/redeem-password")
	public ResponseEntity<Response<Object>> redeemPassword(@RequestBody @Valid PasswordRedeemRequest request) {

		return ResponseEntity.ok(authService.redeemPassword(request));
	}

	@PostMapping("/reset-password")
	public ResponseEntity<Response<Object>> resetPassword(@RequestBody @Valid PasswordResetRequest request) {

		return ResponseEntity.ok(authService.resetPassword(request));
	}

	@PostMapping("/refresh")
	public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> payload) {

		String requestToken = payload.get("refreshToken");

		return refreshTokenRepository.findByToken(requestToken).map(token -> {

			if (refreshTokenService.isTokenExpired(token)) {

				refreshTokenRepository.delete(token);

				return ResponseEntity.badRequest().body("Refresh token expired. Please login again.");
			}

			String newJwt = jwtUtils.generateTokenFromUsername(token.getUser().getUsername());

			return ResponseEntity.ok(Map.of("token", newJwt));
		}).orElse(ResponseEntity.badRequest().body("Invalid refresh token."));
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logoutUser(@RequestBody Map<String, String> payload) {
		String requestToken = payload.get("refreshToken");

		if (requestToken == null || requestToken.isBlank()) {
			return ResponseEntity.badRequest().body("Refresh token is required.");
		}

		return refreshTokenRepository.findByToken(requestToken).map(token -> {
			refreshTokenRepository.delete(token);
			return ResponseEntity.ok("Logged out successfully.");
		}).orElse(ResponseEntity.badRequest().body("Invalid refresh token."));
	}
}
