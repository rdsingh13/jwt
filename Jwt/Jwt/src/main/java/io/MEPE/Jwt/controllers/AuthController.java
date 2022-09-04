package io.MEPE.Jwt.controllers;

import java.util.Collections;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.exceptions.JWTVerificationException;

import io.MEPE.Jwt.entity.User;
import io.MEPE.Jwt.models.LoginCredentials;
import io.MEPE.Jwt.repository.UserRepo;
import io.MEPE.Jwt.security.JWTUtil;
import io.MEPE.Jwt.security.MyUserDetailsService;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private UserRepo userRepo;
	@Autowired
	private JWTUtil jwtUtil;
	@Autowired
	private AuthenticationManager authManager;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private MyUserDetailsService userDetailsService;

	@PostMapping("/register")
	public Map<String, Object> registerHandler(@Valid @RequestBody User user) {
		String encodedPass = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPass);
		user = userRepo.save(user);
		String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
		return Collections.singletonMap("jwt-token", token);
	}

	@PostMapping("/login")
	public Map<String, Object> loginHandler(@RequestBody LoginCredentials body) {
		try {
			UsernamePasswordAuthenticationToken authInputToken = new UsernamePasswordAuthenticationToken(
					body.getEmail(), body.getPassword());

			authManager.authenticate(authInputToken);

			String token = JWTUtil.tokenn.apply(body.getEmail(), body.getRole());
			// String token = jwtUtil.generateToken(body.getEmail(), body.getRole());

			return Collections.singletonMap("jwt-token", token);
		} catch (AuthenticationException authExc) {
			throw new RuntimeException("Invalid Login Credentials");
		}
	}

	@GetMapping(value = "/validate")
	public ResponseEntity<?> getValidity(@RequestHeader("Authorization") final String authHeader) {
		// Returns response after Validating received token

		if (authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")) {
			String jwt = authHeader.substring(7);
			if (jwt == null || jwt.isBlank()) {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			} else {
				try {
					String email = jwtUtil.validateTokenAndRetrieveSubject(jwt);
					UserDetails userDetails = userDetailsService.loadUserByUsername(email);
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email,
							userDetails.getPassword(), userDetails.getAuthorities());
					if (SecurityContextHolder.getContext().getAuthentication() == null) {
						SecurityContextHolder.getContext().setAuthentication(authToken);
					}
				} catch (JWTVerificationException exc) {
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}

			}

		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

}