package com.authenticationsystem.apiauthentication.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.authenticationsystem.apiauthentication.models.PasswordResetToken;
import com.authenticationsystem.apiauthentication.models.User;

public interface PasswordRestTokenRepository extends JpaRepository<PasswordResetToken, Long> {

	Optional<PasswordResetToken> findByToken(String token);

	Optional<PasswordResetToken> findByUser(User user);
}
