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
public class MerchantFreqService {

    // Hash map to store info
    private Map<String, Double> merchFreqMap = new HashMap<>();

    public MerchantFreqService() {
        loadMerchantFrequencies();
    }

    @PostConstruct
    public void loadMerchantFrequencies() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = new ClassPathResource("static/merchant_frequencies.json").getInputStream();
            merchFreqMap = mapper.readValue(is, new TypeReference<>() { });
        } catch (Exception e) {
            throw  new RuntimeException("Failed to load merchant frequency map.");
        }
    }

    public double getFrequency(String merchant) {
        return merchFreqMap.getOrDefault(merchant, 0.0);
    }

    public Map<String, Double> getMerchMap() {
        return merchFreqMap;
    }
}
