package com.authenticationsystem.apiauthentication.services;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.authenticationsystem.apiauthentication.models.RefreshToken;
import com.authenticationsystem.apiauthentication.repositories.RefreshTokenRepository;
import com.authenticationsystem.apiauthentication.repositories.UserRepository;



@Service
public class RefreshTokenService {

	@Value("${jwt.refreshExpirationMs}") 
    private Long refreshTokenDurationMs; 

    private  final RefreshTokenRepository refreshTokenRepository; 
    private  final UserRepository userRepository; 

    public  RefreshTokenService (RefreshTokenRepository repo, UserRepository userRepo) { 
        this .refreshTokenRepository = repo; 
        this .userRepository = userRepo; 
    } 

    public RefreshToken createRefreshToken (Long userId) { 
    	
        RefreshToken  token  =  new  RefreshToken (); 
        
        token.setUser(userRepository.findById(userId).get()); 
        
        token.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs)); 
        
        token.setToken(UUID.randomUUID().toString()); 
        
        return refreshTokenRepository.save(token); 
    } 

    public  boolean  isTokenExpired (RefreshToken token) { 
        return token.getExpiryDate().isBefore(Instant.now()); 
    }
}
