package FraudShield.FraudShield.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class JobFreqService {

    private Map<String, Double> freqMap = new HashMap<>();

    public JobFreqService() {
        loadJobFreqMap();
    }

    @PostConstruct
    public void loadJobFreqMap() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = new ClassPathResource("static/job_frequencies.json").getInputStream();
            freqMap = mapper.readValue(is, new TypeReference<>() { });
        } catch (Exception e) {
            throw new RuntimeException("Failed to load job frequency map.");
        }
    }

    public double getFrequency(String job) {
        return freqMap.getOrDefault(job, 0.0);
    }
}
