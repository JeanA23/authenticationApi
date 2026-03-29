package com.authenticationsystem.apiauthentication.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.authenticationsystem.apiauthentication.models.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>{

	Optional<RefreshToken> findByToken (String token) ; 
}
