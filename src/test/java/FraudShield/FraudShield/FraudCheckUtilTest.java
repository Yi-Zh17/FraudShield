package FraudShield.FraudShield;

import FraudShield.FraudShield.Service.FraudCheckUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FraudCheckUtilTest {
    FraudCheckUtil util = new FraudCheckUtil();
    @Test
    void testNormalization() {
        // Test amt
        assertEquals(-0.4078, util.normalization("amt", 4.97), 1e-4);
        assertEquals(0.9341, util.normalization("amt", 220.11), 1e-4);

        // Test city_pop
        assertEquals(-0.2936, util.normalization("city_pop", 149), 1e-4);
        assertEquals(-0.2877, util.normalization("city_pop", 1939), 1e-4);

        // Test age
        assertEquals(-0.8646, util.normalization("age", 31), 1e-4);
        assertEquals(-0.7495, util.normalization("age", 33), 1e-4);

        // Edge cases
        assertEquals(-1, util.normalization("some other string", 122), 1e-8);
        assertEquals(-1, util.normalization("amt", 10), 1e-8);
    }

    @Test
    void testCategoryEncoding() {
        double[] test1 = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        assertArrayEquals(test1, util.categoryEncoding("home"));

        double[] test2 = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0};
        assertArrayEquals(test2, util.categoryEncoding("shopping_net"));

        // Edge case
        double[] test3 = new double[14];
        assertArrayEquals(test3, util.categoryEncoding("some string"));
    }

    @Test
    void testStateEncoding() {
        double[] test1 = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        assertArrayEquals(test1, util.stateEncoding("LA"));

        double[] test2 = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        assertArrayEquals(test2, util.stateEncoding("NM"));

        double[] test3 = new double[51];
        assertArrayEquals(test3, util.stateEncoding("MADE_UP"));
    }

    @Test
    void testGetMerchFreq() {
        assertEquals(0.000977, util.getMerchFreq("fraud_Rippin, Kub and Mann"), 1e-6);
        assertEquals(0.001228, util.getMerchFreq("fraud_Keeling-Crist"), 1e-6);
    }

    @Test
    void testGetJobFreq() {
        assertEquals(0.000394, util.getJobFreq("Nature conservation officer"), 1e-6);
        assertEquals(0.001951, util.getJobFreq("Patent attorney"), 1e-6);
    }

    @Test
    void testGenderEncoding() {
        assertEquals(0.0, util.genderEncoding("M"));
        assertEquals(1.0, util.genderEncoding("F"));
        assertEquals(-1, util.genderEncoding("Other"));
    }

    @Test
    void testHourDayEncoding() {
        assertArrayEquals(new double[]{0.258819, 0.965925}, util.hourEncoding(1), 1e-6);
        assertArrayEquals(new double[]{0.781831, 0.623489}, util.dayEncoding(1), 1e-6);

        // Edge cases
        assertArrayEquals(new double[2], util.hourEncoding(45));
        assertArrayEquals(new double[2], util.dayEncoding(8));
    }
}
