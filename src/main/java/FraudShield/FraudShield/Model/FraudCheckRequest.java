package FraudShield.FraudShield.Model;

public record FraudCheckRequest(
        double amount,
        int city_pop,
        int age,
        String merchant,
        String job,
        String category,
        String state,
        String gender,
        int transaction_hour,
        int transaction_dayOfWeek
) { }
