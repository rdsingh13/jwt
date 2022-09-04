package io.MEPE.Jwt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.MEPE.Jwt.entity.User;

public interface UserRepo extends JpaRepository<User, Long> {
	
	public Optional<User> findByEmail(String email);
}