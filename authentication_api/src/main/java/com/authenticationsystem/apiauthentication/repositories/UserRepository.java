package com.authenticationsystem.apiauthentication.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.authenticationsystem.apiauthentication.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByName(String name);

	Optional<User> findByEmail(String email);

	Boolean existsByEmail(String email);

	Boolean existsByUsername(String username);
}
