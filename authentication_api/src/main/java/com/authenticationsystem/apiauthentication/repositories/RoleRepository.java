package com.authenticationsystem.apiauthentication.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.authenticationsystem.apiauthentication.models.Erole;
import com.authenticationsystem.apiauthentication.models.Role;

public interface RoleRepository extends JpaRepository<Role, Long>{

	Optional<Role> findByName(Erole name);
}
