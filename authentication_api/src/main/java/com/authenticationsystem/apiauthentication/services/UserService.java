package com.authenticationsystem.apiauthentication.services;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.authenticationsystem.apiauthentication.models.User;
import com.authenticationsystem.apiauthentication.repositories.PasswordRestTokenRepository;
import com.authenticationsystem.apiauthentication.repositories.RoleRepository;
import com.authenticationsystem.apiauthentication.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final PasswordRestTokenRepository passwordRestTokenRepository;
	
	
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

