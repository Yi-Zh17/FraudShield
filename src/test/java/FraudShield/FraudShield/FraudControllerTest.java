package FraudShield.FraudShield;

import FraudShield.FraudShield.Controller.FraudController;
import FraudShield.FraudShield.Model.FraudCheckRequest;
import FraudShield.FraudShield.Model.FraudCheckResponse;
import FraudShield.FraudShield.Service.FraudCheckService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FraudControllerTest {

    @Test
    void checkFraudFormatsReturnedProbabilityWithoutApplyingSigmoidAgain() throws Exception {
        FraudCheckService fraudCheckService = mock(FraudCheckService.class);
        when(fraudCheckService.checkFraud(any())).thenReturn(new FraudCheckResponse(true, 0.8));

        FraudController controller = new FraudController();
        ReflectionTestUtils.setField(controller, "fraudCheckService", fraudCheckService);

        Model model = new ExtendedModelMap();
        String viewName = controller.checkFraud(request(), model);

        assertEquals("fraud-result", viewName);
        assertEquals(true, model.getAttribute("result"));
        assertEquals(80.0, ((BigDecimal) model.getAttribute("probability")).doubleValue(), 1e-9);
    }

    private FraudCheckRequest request() {
        return new FraudCheckRequest(
                100.0,
                1000,
                35,
                "fraud_Rippin, Kub and Mann",
                "Patent attorney",
                "home",
                "CA",
                "F",
                12,
                2
        );
    }
}
