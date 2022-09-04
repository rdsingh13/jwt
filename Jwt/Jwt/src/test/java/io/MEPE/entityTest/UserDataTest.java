package io.MEPE.entityTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.MEPE.Jwt.entity.User;



 
public class UserDataTest {

	User auth = new User(1L,"ad", "ad", "ad","ad","ad");
	@Test
	void testPassword() {
		auth.setPassword("Name");
		assertEquals( "Name", auth.getPassword());
	}
	@Test
	void testEmail() {
		auth.setEmail("Name");
		assertEquals( "Name", auth.getEmail());
	}
	@Test
	void testName() {
		auth.setName("Name@gmail.com");
		assertEquals( "Name@gmail.com", auth.getName());
	}
	@Test
	void testRole() {
		auth.setRole("User");
		assertEquals( "User", auth.getRole());
	}
	

}
