package com.JobScrapper.OrchestratorService;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/orchestrator/")
@CrossOrigin(origin = "http://localhost:5173")
class OrchestratorServiceController {

    // Define your REST endpoints here and use orchestratorService to handle the logic
    @GetMApping("/health")
    public Map<String, Object> healthCheck(){
        Map<String, Object> response = new HashMap<>();
        response.put("status", "Orchestrator is running");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("version", "v1");
        return response;
    }

    @PostMapping("/test-scrape")
    public Map<String, Object> testScrape(@RequestBody Map<String, String> request){
        Map<String, Object> response = new HashMap<>();
        response.put("status", "Orchestrator is running");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("version", "v1");
        return response;
    }    
}