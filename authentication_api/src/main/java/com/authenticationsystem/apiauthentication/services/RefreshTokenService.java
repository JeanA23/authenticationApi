package com.authenticationsystem.apiauthentication.services;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.authenticationsystem.apiauthentication.models.RefreshToken;
import com.authenticationsystem.apiauthentication.models.User;
import com.authenticationsystem.apiauthentication.repositories.RefreshTokenRepository;
import com.authenticationsystem.apiauthentication.repositories.UserRepository;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class RefreshTokenService {

	@Value("${jwt.refreshExpirationMs}") 
    private Long refreshTokenDurationMs; 

    private  final RefreshTokenRepository refreshTokenRepository; 
    private  final UserRepository userRepository; 

     

    public RefreshToken createRefreshToken (Long userId) { 
    	
    	User existById = userRepository.findById(userId).orElseThrow();
    	
        RefreshToken  token  =  new  RefreshToken(); 
        
        token.setUser(existById); 
        
        token.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs)); 
        
        token.setToken(UUID.randomUUID().toString()); 
        
        return refreshTokenRepository.save(token); 
    } 

    public  boolean  isTokenExpired (RefreshToken token) { 
    	
        return token.getExpiryDate().isBefore(Instant.now()); 
    }
}
