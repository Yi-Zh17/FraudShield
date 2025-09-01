package FraudShield.FraudShield.Service;

import java.util.HashMap;
import java.util.Map;

public class FraudCheckUtil {
    // Normalization (overloads)
    public double normalization(String name, double num) {
        if(name.equals("amt")) {
            final double amt_mean = 70.35103545607033; // mean of amt
            final double amt_std = 160.3159767533844; // std of amt
            return (num - amt_mean) / amt_std;
        } else {
            return -1;
        }
    }

    public double normalization(String name, int num) {
        if(name.equals("city_pop")) {
            final double city_pop_mean = 88824.44056297839; // mean of city population
            final double city_pop_std = 301956.2442538002; // std of city population
            return ((double) num - city_pop_mean) / city_pop_std;
        } else if(name.equals("age")) {
            final double age_mean = 46.02929801222357; // mean of age
            final double age_std = 17.382365922077234; // std of age
            return ((double) num - age_mean) / age_std;
        } else {
            return -1;
        }
    }

    // One hot encoding for category and state
    public double[] categoryEncoding(String category) {
        double[] result = new double[14];

        // Store all categories
        Map<String, Integer> map = new HashMap<>();
        map.put("entertainment", 0);
        map.put("food_dining", 1);
        map.put("gas_transport", 2);
        map.put("grocery_net", 3);
        map.put("grocery_pos", 4);
        map.put("health_fitness", 5);
        map.put("home", 6);
        map.put("kids_pets", 7);
        map.put("misc_net", 8);
        map.put("misc_pos", 9);
        map.put("personal_care", 10);
        map.put("shopping_net", 11);
        map.put("shopping_pos", 12);
        map.put("travel", 13);

        // Invalid input
        if(!map.containsKey(category)) {
            return result;
        }
        // One hot encoding
        result[map.get(category)] = 1.0;
        return result;
    }

    public double[] stateEncoding(String state) {
        double[] result = new double[51];

        // Store all states
        Map<String, Integer> map = new HashMap<>();
        map.put("AK", 0);
        map.put("AL", 1);
        map.put("AR", 2);
        map.put("AZ", 3);
        map.put("CA", 4);
        map.put("CO", 5);
        map.put("CT", 6);
        map.put("DC", 7);
        map.put("DE", 8);
        map.put("FL", 9);
        map.put("GA", 10);
        map.put("HI", 11);
        map.put("IA", 12);
        map.put("ID", 13);
        map.put("IL", 14);
        map.put("IN", 15);
        map.put("KS", 16);
        map.put("KY", 17);
        map.put("LA", 18);
        map.put("MA", 19);
        map.put("MD", 20);
        map.put("ME", 21);
        map.put("MI", 22);
        map.put("MN", 23);
        map.put("MO", 24);
        map.put("MS", 25);
        map.put("MT", 26);
        map.put("NC", 27);
        map.put("ND", 28);
        map.put("NE", 29);
        map.put("NH", 30);
        map.put("NJ", 31);
        map.put("NM", 32);
        map.put("NV", 33);
        map.put("NY", 34);
        map.put("OH", 35);
        map.put("OK", 36);
        map.put("OR", 37);
        map.put("PA", 38);
        map.put("RI", 39);
        map.put("SC", 40);
        map.put("SD", 41);
        map.put("TN", 42);
        map.put("TX", 43);
        map.put("UT", 44);
        map.put("VA", 45);
        map.put("VT", 46);
        map.put("WA", 47);
        map.put("WI", 48);
        map.put("WV", 49);
        map.put("WY", 50);

        // Invalid input
        if(!map.containsKey(state)) {
            return result;
        }
        result[map.get(state)] = 1.0;
        return result;
    }

    // Merchant and job (frequency encoding)
    public double getMerchFreq(String merchant) {
        MerchantFreqService merchService = new MerchantFreqService();
        return merchService.getFrequency(merchant);
    }

    public double getJobFreq(String job) {
        JobFreqService jobService = new JobFreqService();
        return jobService.getFrequency(job);
    }

    // Gender: male -> 0, female -> 1
    public double genderEncoding(String gender) {
        if(gender.equals("M")) {
            return 0.0;
        } else if (gender.equals("F")) {
            return 1.0;
        } else {
            return -1;
        }
    }

    // Hour and day -> cyclic encoding
    public double[] hourEncoding(int num) {
        double[] result = new double[2];
        if(num < 0 || num > 23) {
            return result;
        }
        result[0] = Math.sin(2 * Math.PI * num / 24);
        result[1] = Math.cos(2 * Math.PI * num / 24);
        return result;
    }

    public double[] dayEncoding(int num) {
        double[] result = new double[2];
        if(num < 0 || num > 6) {
            return result;
        }
        result[0] = Math.sin(2 * Math.PI * num / 7);
        result[1] = Math.cos(2 * Math.PI * num / 7);
        return result;
    }

}
