package com.authenticationsystem.apiauthentication.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authenticationsystem.apiauthentication.models.User;
import com.authenticationsystem.apiauthentication.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

	private final UserService userService;
	
	
	@PostMapping("/create")
	public User CreateUser(@RequestBody User user) {
		
		return userService.CreateUser(user);
	}
}
