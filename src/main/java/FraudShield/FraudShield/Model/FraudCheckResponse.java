package FraudShield.FraudShield.Model;

public record FraudCheckResponse(
        boolean is_fraud,
        double prob
) { }
