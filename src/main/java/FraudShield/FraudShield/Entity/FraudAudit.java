package FraudShield.FraudShield.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fraud_audit_logs")
public class FraudAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // A subset of input data to audit
    private double amount;
    private String state;
    private String merchant;

    // The ML model's output
    private double predictedProbability;
    private boolean isFraud;

    // Immutable timestamp generated upon row creation
    @Column(updatable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }

    // Standard Setters required to populate the object
    public void setAmount(double amount) { this.amount = amount; }
    public void setState(String state) { this.state = state; }
    public void setMerchant(String merchant) { this.merchant = merchant; }
    public void setPredictedProbability(double predictedProbability) { this.predictedProbability = predictedProbability; }
    public void setFraud(boolean fraud) { isFraud = fraud; }
    
}