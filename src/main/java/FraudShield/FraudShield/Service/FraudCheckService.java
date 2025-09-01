package FraudShield.FraudShield.Service;


import FraudShield.FraudShield.Model.FraudCheckRequest;
import FraudShield.FraudShield.Model.FraudCheckResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class FraudCheckService {
    private final ModelPredictionService model;

    public FraudCheckService() throws Exception {
        ClassPathResource modelRes = new ClassPathResource("static/model.onnx");
        ClassPathResource dataRes = new ClassPathResource("static/model.onnx.data");

        Path tempDir = Files.createTempDirectory("fraud_model");
        Path modelPath = tempDir.resolve("model.onnx");
        Path dataPath = tempDir.resolve("model.onnx.data");

        Files.copy(modelRes.getInputStream(), modelPath, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(dataRes.getInputStream(), dataPath, StandardCopyOption.REPLACE_EXISTING);

        this.model = new ModelPredictionService(modelPath.toString());
    }

    public FraudCheckResponse checkFraud(FraudCheckRequest request) throws Exception {
        // Preprocess input
        double[] features = process(request);
        checkIntegrity(features);

        // Inference
        double prob = model.predict(features);

        boolean is_fraud = prob >= 0.5;

        return new FraudCheckResponse(is_fraud, prob);
    }

    // Preprocess the raw data before inference
    private double[] process(FraudCheckRequest request) {
        // Instantiate a util
        FraudCheckUtil util = new FraudCheckUtil();

        /* Order of features:
          Normalizations: amt, city_pop, age;
          One-hot encoding: category, state;
          Frequency encoding: merchant;
          Binary encoding: gender;
          Frequency encoding: job;
          cyclic encoding: hour, day of week
         */
        // Normalizations
        double amt = util.normalization("amt", request.amount());
        double city_pop = util.normalization("city_pop", request.city_pop());
        double age = util.normalization("age", request.age());

        // One-hot encoding
        double[] category = util.categoryEncoding(request.category());
        double[] state = util.stateEncoding(request.state());

        // Frequency encoding: Merchant
        double merchant = util.getMerchFreq(request.merchant());

        // Binary encoding
        double gender = util.genderEncoding(request.gender());

        // Frequency encoding: Job
        double job = util.getJobFreq(request.job());

        // Cyclic encoding
        double[] hour = util.hourEncoding(request.transaction_hour());
        double[] day = util.dayEncoding(request.transaction_dayOfWeek());

        // Input array
        double[] result = new double[75];

        // Assemble data
        result[0] = amt;
        result[1] = city_pop;
        result[2] = age;
        System.arraycopy(category, 0, result, 3, 14);
        System.arraycopy(state, 0, result, 17, 51);
        result[68] = merchant;
        result[69] = gender;
        result[70] = job;
        System.arraycopy(hour, 0, result, 71, 2);
        System.arraycopy(day, 0, result, 73, 2);

        return result;
    }

    void checkIntegrity(double[] features) {
        // Amt, city_pop, and age should not return -1
        if(features[0] == -1 || features[1] == -1 || features[2] == -1) {
            throw new RuntimeException("Invalid input feature in the first three column.");
        }

        // category and state arrays should contain only one 1.0
        double sum = 0;
        int i = 3;

        // category
        while(i < 17) {
            sum += features[i];
            ++i;
        }
        if(sum != 1.0) throw new RuntimeException("Invalid category encoding.");

        // State
        sum = 0;
        while(i < 67) {
            sum += features[i];
            ++i;
        }
        if(sum != 1.0) throw new RuntimeException("Invalid state encoding");

        // No need to check for merchant and job as they provide a default
        // value of 0.0 if no match found in the database

        // Check gender
        if(features[69] == -1) throw new RuntimeException("Invalid gender input");

        // Check hour and day
        if(features[71] == 0.0 && features[72] == 0.0) throw new RuntimeException("Invalid hour input");
        if(features[73] == 0.0 && features[74] == 0.0) throw new RuntimeException("Invalid day input");
    }

}
