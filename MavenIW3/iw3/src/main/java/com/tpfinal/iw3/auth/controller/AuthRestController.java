package com.tpfinal.iw3.auth.controller;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.tpfinal.iw3.auth.custom.CustomAuthenticationManager;
import com.tpfinal.iw3.auth.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

@RestController
@Tag(name = "Autenticación", description = "Endpoints para autenticación y autorización")
public class AuthRestController {
	
	// Constantes JWT
	private static final long EXPIRATION_TIME = (60 * 60 * 1000); // 1 hora
	private static final String SECRET = "MyVerySecretKey";
	
	@Autowired
	private AuthenticationManager authManager;
	@Autowired
	private PasswordEncoder pEncoder;

	@Data
	public static class LoginRequest {
		private String username;
		private String password;
	}

	@Data
	public static class LoginResponse {
		private String token;
		private String username;
		private String email;
		private Long userId;
		public LoginResponse(String token, String username, String email, Long userId) {
			this.token = token;
			this.username = username;
			this.email = email;
			this.userId = userId;
		}
	}

	@Operation(summary = "Login de usuario", description = "Autentica un usuario y devuelve un token JWT para acceder a los endpoints protegidos")
	@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Login exitoso, retorna token JWT", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))), @ApiResponse(responseCode = "401", description = "Credenciales inválidas"), @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
	@PostMapping(value = "/api/v1/auth/login", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
		try {
			Authentication auth = authManager.authenticate(((CustomAuthenticationManager) authManager).authWrap(loginRequest.getUsername(), loginRequest.getPassword()));
			User user = (User) auth.getPrincipal();
			String token = JWT.create().withSubject(user.getUsername()).withClaim("userId", user.getIdUser()).withClaim("email", user.getEmail()).withClaim("version", "1.0.0").withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME)).sign(Algorithm.HMAC512(SECRET.getBytes()));
			LoginResponse response = new LoginResponse(token, user.getUsername(), user.getEmail(), user.getIdUser());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas: " + e.getMessage());
		}
	}

	@Operation(summary = "Codificar password (solo para desarrollo)", description = "Endpoint de utilidad para generar password codificado con BCrypt")
	@GetMapping(value = "/api/v1/auth/encode-password", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> encodePassword(@RequestParam String password) {
		try {
			return ResponseEntity.ok(pEncoder.encode(password));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
		}
	}
}
