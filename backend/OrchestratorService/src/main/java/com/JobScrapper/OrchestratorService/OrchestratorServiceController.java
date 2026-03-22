package com.JobScrapper.OrchestratorService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orchestrator/")
@CrossOrigin(origins = "http://localhost:5173")
class OrchestratorServiceController {

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
}