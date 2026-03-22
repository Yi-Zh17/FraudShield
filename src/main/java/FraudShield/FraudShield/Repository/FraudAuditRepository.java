package FraudShield.FraudShield.Repository;

import FraudShield.FraudShield.Entity.FraudAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FraudAuditRepository extends JpaRepository<FraudAudit, Long> {
}