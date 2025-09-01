package FraudShield.FraudShield.Controller;

import FraudShield.FraudShield.Model.FraudCheckRequest;
import FraudShield.FraudShield.Model.FraudCheckResponse;
import FraudShield.FraudShield.Service.FraudCheckService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/fraud")
public class FraudController {
    private FraudCheckService fraudCheckService;

    @GetMapping
    public String fraudForm() {
        return "fraud";
    }
    @PostMapping("/check")
    public FraudCheckResponse checkFraud(@RequestBody FraudCheckRequest request) throws Exception {
        return fraudCheckService.checkFraud(request);
    }
}