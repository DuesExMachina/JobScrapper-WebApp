package com.JobScrapper.OrchestratorService;

import java.time.LocalDateTime;
import java.util.*;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orchestrator/")
@CrossOrigin(origins = "http://localhost:5173")
class OrchestratorServiceController {
    // Token Id for our app, we will use this to verify the token sent by the
    // frontend
    private static final String CLIENT_ID = "YOUR_GOOGLE_CLIENT_ID"; // We need to generate this later from google cloud
                                                                     // console
    private static final String JWT_SECRET = "my-secret-key"; // In prod we will be storing fetching it from vault using
                                                              // enviroment spec ific secret

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

        String token = request.get("token");

        // Create the token verifier
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance()).setAudience(Collections.singletonList(CLIENT_ID)).build();

        // Verify the sent token against the client id of the app
        GoogleIdToken idToken = verifier.verify(token);

        if (idToken == null) {
            throw new RuntimeException("Invalid ID token");
        }

        // get user details from verified token

        return response;

    }
}