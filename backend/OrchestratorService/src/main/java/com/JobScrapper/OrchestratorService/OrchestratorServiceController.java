package com.JobScrapper.OrchestratorService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.JobScrapper.OrchestratorService.models.User;
import com.JobScrapper.OrchestratorService.repositories.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@RestController
@RequestMapping("/api/orchestrator/")
@CrossOrigin(origins = "http://localhost:5173")
class OrchestratorServiceController {
    // Token Id for our app, we will use this to verify the token sent by the
    // frontend
    @Value("${google.oauth.client-id}")
    private String CLIENT_ID; // We
                              // need
                              // to
                              // generate
                              // this
                              // later
                              // from
                              // google
                              // cloud
    @Value("${google.jwt.secret}")
    private String JWT_SECRET; // In prod we will be storing fetching it from vault using
                               // enviroment spec ific secret
    @Value("${google.oauth.client-secret}")
    private String CLIENT_SECRET; // We need to generate this later
                                  // from google cloud console

    @Autowired
    private UserRepository userRepository;

    // Define your REST endpoints here and use orchestratorService to handle the
    // logic
    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "Orchestrator is running");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("version", "v1");
        return response;
    }

    @PostMapping("/test-scrape")
    public Map<String, Object> testScrape(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "Orchestrator is running");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("version", "v1");
        return response;
    }

    @PostMapping("dummyAuth/google")
    public Map<String, Object> dummyGoogleAuth(@RequestBody Map<String, String> request) {

        String token = request.get("token");

        // For now mock verification, later we can verify with Google API'
        Map<String, Object> user = new HashMap<>();
        user.put("name", "Test User");
        user.put("email", "testuser@example.com");

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("jwt", "mock-jwt-token"); // Replace with actual JWT generation logic
        response.put("user", user);

        return response;

    }

    @PostMapping("Auth/google")
    public ResponseEntity<Map<String, Object>> googleAuth(@RequestBody Map<String, String> request) {

        Map<String, Object> response = new HashMap<>();

        try {
            String token = request.get("token"); // exctract the token from the request body

            // Check is token is valid or not
            if (token == null || token.isBlank()) {
                response.put("status", "error");
                response.put("message", "Token is missing");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // Bad request = 400
            }

            // Create the token verifier
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    JacksonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    .build();

            // Verify the sent token against the client id of the app
            GoogleIdToken idToken = verifier.verify(token);

            if (idToken != null) {
                // get user details from verified token
                GoogleIdToken.Payload payload = idToken.getPayload();

                String email = payload.getEmail();
                String name = (String) payload.get("name");

                // Check in DB if user exists, if not create a new user and then generate JWT
                // for the user
                Optional<User> userOpt = userRepository.findByEmail(email);

                if (userOpt.isPresent()) {
                    // ============
                    // Generate JWT
                    // ============

                    SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));

                    String jwt = Jwts.builder()
                            .subject(email)
                            .claim("name", name)
                            .issuedAt(new Date())
                            .expiration(new Date(System.currentTimeMillis() + 86400000))
                            .signWith(key)
                            .compact();

                    User user = new User();
                    user.setEmail(email);
                    user.setName(name);
                    user.setRole("USER");

                    response.put("status", "success");
                    response.put("jwt", jwt);
                    response.put("user", user);
                    return ResponseEntity.ok(response);
                }
                // else
                response.put("status", "new_user");
                response.put("email", email);
                response.put("name", name);
                return ResponseEntity.ok(response);

            }

            // else idToken received is null
            response.put("status", "error");
            response.put("message", "Invalid ID token");
            return ResponseEntity.status(400).body(response);

        } catch (Exception e) { // token is invalid
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        // return response;

    }

    @PostMapping("/auth/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody Map<String, String> request) {

        String email = request.get("email");
        String name = request.get("name");

        Map<String, Object> response = new HashMap<>();

        if (email == null || email.isBlank() || name == null || name.isBlank()) {
            response.put("status", "error");
            response.put("message", "Email and name are required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (userRepository.findByEmail(email).isPresent()) {
            response.put("status", "already_exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // Conflict = 409
        }

        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setRole("USER");

        userRepository.save(user);

        SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        String jwt = Jwts.builder()
                .subject(email)
                .claim("name", name)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(key)
                .compact();

        response.put("status", "registered");
        response.put("jwt", jwt);
        response.put("user", user);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestBody Map<String, String> request) {
        // get the refresh token from the request body
        Map<String, Object> response = new HashMap<>();
        String refreshToken = request.get("refreshToken");
        String email = request.get("email");
        String name = request.get("name");

        if (refreshToken == null || refreshToken.isBlank() || email == null || email.isBlank()) {
            response.put("status", "error");
            response.put("message", "Valid email and refresh token are required");
            return ResponseEntity.status(400).body(response);
        }

        // verify it against the refresh token stored in db for the user
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            response.put("status", "error");
            response.put("message", "User not found");
            return ResponseEntity.status(404).body(response); // 404 = Not found
        }
        User user = userOpt.get();
        if (!refreshToken.equals(user.getRefreshToken())
                || user.getRefreshTokenExpiry().isBefore(LocalDateTime.now())) {
            response.put("status", "Login expired");
            response.put("message", "Invalid or expired refresh token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); // 401 = Unauthorized
        }
        // if valid generate a new access token and refresh token and update the refresh
        SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        // create a new access token
        String accessToken = Jwts.builder()
                .subject(email)
                .claim("name", name)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(key)
                .compact();

        // create new refresh token
        String newRefreshToken = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)) // 7 days
                .signWith(key)
                .compact();
        // token in the db for the user
        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        // send back both access token and refresh token in the response
        response.put("status", "success");
        response.put("accessToken", accessToken);
        response.put("refeshToken", newRefreshToken);
        return ResponseEntity.ok(response);
    }

}

/*
 * Flow:
 * 1. Front end sends the auth token as part of request body
 * 2. We extract the token from requestbody
 * 3. Create the google token verifier
 * 4. Verify the token against our client id
 * 5. If token is valid then we extract user details from the token
 * Check in DB if user exists with the email from the token payload
 * 6. Generate the jwt token
 * 7. Put the user details and jwt token in response and send it back to
 * frontend
 * 8. Else if token is not valid then put the error message in the response
 * 9. Send the response back to frontend
 */