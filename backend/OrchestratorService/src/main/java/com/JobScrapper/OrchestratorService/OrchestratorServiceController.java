package com.JobScrapper.OrchestratorService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

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
    public Map<String, Object> googleAuth(@RequestBody Map<String, String> request) {

        Map<String, Object> response = new HashMap<>();

        try {
            String token = request.get("token");

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

                // ============
                // Generate JWT
                // ============

                String jwt = Jwts.builder()
                        .setSubject(email)
                        .claim("name", name)
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                        .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
                        .compact();

                Map<String, Object> user = new HashMap<>();
                user.put("name", name);
                user.put("email", email);

                response.put("status", "success");
                response.put("jwt", jwt);
                response.put("user", user);
            } else {
                response.put("status", "Invalid ID token");
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
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
    public Map<String, Object> googleAuth(@RequestBody Map<String, String> request) {

        Map<String, Object> response = new HashMap<>();

        try {
            String token = request.get("token");

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

                // ============
                // Generate JWT
                // ============

                String jwt = Jwts.builder()
                        .setSubject(email)
                        .claim("name", name)
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                        .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
                        .compact();

                Map<String, Object> user = new HashMap<>();
                user.put("name", name);
                user.put("email", email);

                response.put("status", "success");
                response.put("jwt", jwt);
                response.put("user", user);
            } else {
                response.put("status", "Invalid ID token");
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        return response;

    }
}