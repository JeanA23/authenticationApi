package com.authenticationsystem.apiauthentication.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.authenticationsystem.apiauthentication.models.Erole;
import com.authenticationsystem.apiauthentication.models.Role;
import com.authenticationsystem.apiauthentication.models.User;
import com.authenticationsystem.apiauthentication.repositories.RoleRepository;
import com.authenticationsystem.apiauthentication.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	
	
	public User CreateUser(User user) {
		
		String hashedPassword = passwordEncoder.encode(user.getPassword());
		
		User newUser = User.builder()
				.name(user.getName())
				.email(user.getEmail())
				.password(hashedPassword)
				.build();				
		
		Role adminRole = roleRepository.findByName(Erole.ROLE_USER).orElseThrow();
		
		List<Role> roles = new ArrayList<>();
		roles.add(adminRole);
		newUser.setRoles(roles);
		
		
		return userRepository.save(newUser);
	}
	
	
	public List<User> getAllUsers() {
		
		return userRepository.findAll();
	}
	
	
	public Optional<User> getUserByUser(Long idUser) {
		
		return userRepository.findById(idUser);
	}
	
	public User updateUser(Long idUser, User user) {
		
		User userExists = userRepository.findById(idUser).orElseThrow();
		
		userExists.setName(user.getName());
		userExists.setEmail(user.getEmail());
		userExists.setPassword(user.getPassword());
		
		return userRepository.save(userExists);
			
		}
	
	public void deleteUser(Long idUser) {
		
		 userRepository.deleteById(idUser);
	}
	
	
	}

