package com.authenticationsystem.apiauthentication.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.authenticationsystem.apiauthentication.dto.LoginRequest;
import com.authenticationsystem.apiauthentication.dto.LoginResponse;
import com.authenticationsystem.apiauthentication.dto.PasswordRedeemRequest;
import com.authenticationsystem.apiauthentication.dto.PasswordResetRequest;
import com.authenticationsystem.apiauthentication.dto.RegisterRequest;
import com.authenticationsystem.apiauthentication.dto.RegisterUserResponse;
import com.authenticationsystem.apiauthentication.dto.UserResponse;
import com.authenticationsystem.apiauthentication.models.Erole;
import com.authenticationsystem.apiauthentication.models.PasswordResetToken;
import com.authenticationsystem.apiauthentication.models.Role;
import com.authenticationsystem.apiauthentication.models.User;
import com.authenticationsystem.apiauthentication.repositories.PasswordRestTokenRepository;
import com.authenticationsystem.apiauthentication.repositories.RoleRepository;
import com.authenticationsystem.apiauthentication.repositories.UserRepository;
import com.authenticationsystem.apiauthentication.security.UserDetailsImpl;
import com.authenticationsystem.apiauthentication.securityJwt.JwtUtils;
import com.authenticationsystem.apiauthentication.utils.Response;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final JwtUtils jwtUtils;

	private final AuthenticationManager authenticationManager;

	private final PasswordEncoder passwordEncoder;

	private final RoleRepository roleRepository;

	private final UserRepository userRepository;
	
	private final PasswordRestTokenRepository tokenRepository;
	
	private final EmailService emailService;

	// Register Function
	@Transactional
	public Response<Object> register(RegisterRequest request) {

		if(userRepository.existsByUsername(request.getUsername())) {

			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already registered");
		}

		if(userRepository.existsByEmail(request.getEmail())) {

			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already registered");
		}

		String hashedPassword = passwordEncoder.encode(request.getPassword());

		User newUser = User.builder()
				.name(request.getName())
				.email(request.getEmail())
				.password(hashedPassword)
				.username(request.getUsername())
				.build();

		Role userRole = roleRepository.findByName(Erole.ROLE_USER)
				.orElseThrow(() -> new RuntimeException("Error: Role is not found."));

		List<Role> roles = new ArrayList<>();
		roles.add(userRole);
		newUser.setRoles(roles);

		userRepository.save(newUser);

		RegisterUserResponse registerUserResponse = RegisterUserResponse.builder()
				.username(newUser.getUsername())
				.email(newUser.getEmail())
				.build();

		return Response.builder()
				.responseCode(200)
				.responseMessage("SUCCESS")
				.data(registerUserResponse)
				.build();
	}

	// Login Function
	@Transactional
	public Response<Object> login(LoginRequest request) {

		userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException("User not found. Please register first"));

		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		String jwt = jwtUtils.generateJwtToken(userDetails);

		List<String> roles = userDetails.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority).toList();

		LoginResponse loginResponse = LoginResponse.builder()
				.username(userDetails.getUsername())
				.email(userDetails.getEmail())
				.roles(roles)
				.accessToken(jwt)
				.tokenType("Bearer")
				.build();

		return Response.builder()
				.responseCode(200)
				.responseMessage("SUCCESS")
				.data(loginResponse)
				.build();
	}

	@Transactional
	public Response<Object> getUser() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();

		Long userId = userDetailsImpl.getId();

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Email not found"));

		UserResponse userResponse = UserResponse.builder().id(user.getId()).username(user.getUsername())
				.email(user.getEmail()).roles(user.getRoles().stream().map(Role::getName).toList()).build();

		return Response.builder().responseCode(200).responseMessage("SUCCESS").data(userResponse).build();
	}
	
	
	@Transactional
	public Response<Object> redeemPassword(PasswordRedeemRequest request) {

	    userRepository.findByEmail(request.getEmail()).ifPresent(user -> {

	        
	        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

	        
	        String token = UUID.randomUUID().toString();
	        PasswordResetToken resetToken = new PasswordResetToken(token, user);
	        tokenRepository.save(resetToken);

	    
	        emailService.sendPasswordResetEmail(user.getEmail(), token);
	    });

	    
	    return Response.builder()
	            .responseCode(200)
	            .responseMessage("SUCCESS")
	            .data("If this email address is registered, a link has been sent.")
	            .build();
	}

	
	@Transactional
	public Response<Object> resetPassword(PasswordResetRequest request) {

	    PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
	            .orElseThrow(() -> new ResponseStatusException(
	                HttpStatus.BAD_REQUEST, "Token invalide"));

	    
	    if (resetToken.isExpired()) {
	        tokenRepository.delete(resetToken);
	        throw new ResponseStatusException(
	            HttpStatus.BAD_REQUEST, "Token expiré, refaites une demande");
	    }

	    
	    User user = resetToken.getUser();
	    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
	    userRepository.save(user);

	    
	    tokenRepository.delete(resetToken);

	    return Response.builder()
	            .responseCode(200)
	            .responseMessage("SUCCESS")
	            .data("Mot de passe mis à jour avec succès.")
	            .build();

	}
}
