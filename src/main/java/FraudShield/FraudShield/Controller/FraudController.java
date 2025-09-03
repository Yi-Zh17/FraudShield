package FraudShield.FraudShield.Controller;

import FraudShield.FraudShield.Model.FraudCheckRequest;
import FraudShield.FraudShield.Model.FraudCheckResponse;
import FraudShield.FraudShield.Service.FraudCheckService;
import FraudShield.FraudShield.Service.JobFreqService;
import FraudShield.FraudShield.Service.MerchantFreqService;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/fraud")
public class FraudController {
    @Autowired
    private FraudCheckService fraudCheckService;

    private MerchantFreqService merchFreqService;
    private JobFreqService jobFreqService;

    @GetMapping
    public String fraudForm(Model model) {
        Map<String, Integer> categoryMap = new HashMap<>();
        categoryMap.put("entertainment", 0);
        categoryMap.put("food_dining", 1);
        categoryMap.put("gas_transport", 2);
        categoryMap.put("grocery_net", 3);
        categoryMap.put("grocery_pos", 4);
        categoryMap.put("health_fitness", 5);
        categoryMap.put("home", 6);
        categoryMap.put("kids_pets", 7);
        categoryMap.put("misc_net", 8);
        categoryMap.put("misc_pos", 9);
        categoryMap.put("personal_care", 10);
        categoryMap.put("shopping_net", 11);
        categoryMap.put("shopping_pos", 12);
        categoryMap.put("travel", 13);

        // Store all states
        Map<String, Integer> stateMap = new HashMap<>();
        stateMap.put("AK", 0);
        stateMap.put("AL", 1);
        stateMap.put("AR", 2);
        stateMap.put("AZ", 3);
        stateMap.put("CA", 4);
        stateMap.put("CO", 5);
        stateMap.put("CT", 6);
        stateMap.put("DC", 7);
        stateMap.put("DE", 8);
        stateMap.put("FL", 9);
        stateMap.put("GA", 10);
        stateMap.put("HI", 11);
        stateMap.put("IA", 12);
        stateMap.put("ID", 13);
        stateMap.put("IL", 14);
        stateMap.put("IN", 15);
        stateMap.put("KS", 16);
        stateMap.put("KY", 17);
        stateMap.put("LA", 18);
        stateMap.put("MA", 19);
        stateMap.put("MD", 20);
        stateMap.put("ME", 21);
        stateMap.put("MI", 22);
        stateMap.put("MN", 23);
        stateMap.put("MO", 24);
        stateMap.put("MS", 25);
        stateMap.put("MT", 26);
        stateMap.put("NC", 27);
        stateMap.put("ND", 28);
        stateMap.put("NE", 29);
        stateMap.put("NH", 30);
        stateMap.put("NJ", 31);
        stateMap.put("NM", 32);
        stateMap.put("NV", 33);
        stateMap.put("NY", 34);
        stateMap.put("OH", 35);
        stateMap.put("OK", 36);
        stateMap.put("OR", 37);
        stateMap.put("PA", 38);
        stateMap.put("RI", 39);
        stateMap.put("SC", 40);
        stateMap.put("SD", 41);
        stateMap.put("TN", 42);
        stateMap.put("TX", 43);
        stateMap.put("UT", 44);
        stateMap.put("VA", 45);
        stateMap.put("VT", 46);
        stateMap.put("WA", 47);
        stateMap.put("WI", 48);
        stateMap.put("WV", 49);
        stateMap.put("WY", 50);

        merchFreqService = new MerchantFreqService();
        Map<String, Double> merchFreqMap = merchFreqService.getMerchMap();

        jobFreqService = new JobFreqService();
        Map<String, Double> jobFreqMap = jobFreqService.getJobFreqMap();

        model.addAttribute("categories", categoryMap);
        model.addAttribute("states", stateMap);
        model.addAttribute("merchants", merchFreqMap);
        model.addAttribute("jobs", jobFreqMap);

        return "fraud";
    }

    @PostMapping("/check")
    public String checkFraud(@ModelAttribute FraudCheckRequest request, Model model) {
        try {
            FraudCheckResponse response = fraudCheckService.checkFraud(request);
            double prob = 1.0 / (1.0 + Math.exp(-response.prob())) * 100;
            BigDecimal bd = new BigDecimal(prob, new MathContext(4));
            model.addAttribute("result", response.is_fraud());
            model.addAttribute("probability", bd);
            return "fraud-result"; // Render result page
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "fraud"; // Show form again with error
        }
    }
}