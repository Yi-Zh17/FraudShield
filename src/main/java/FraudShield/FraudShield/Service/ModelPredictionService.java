package FraudShield.FraudShield.Service;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;

import java.nio.FloatBuffer;
import java.util.Collections;

public class ModelPredictionService implements AutoCloseable{

    private final OrtEnvironment env;
    private final OrtSession session;

    public ModelPredictionService(String modelPath) throws OrtException {
        env = OrtEnvironment.getEnvironment();
        session = env.createSession(modelPath, new OrtSession.SessionOptions());
    }

    public double predict(double[] features) throws OrtException {
        // Convert double to float32
        float[] inputData = new float[features.length];
        for (int i = 0; i < features.length; ++i) {
            inputData[i] = (float) features[i];
        }

        // Create ONNX tensor
        long[] shape = new long[]{1, features.length};
        OnnxTensor inputTensor = OnnxTensor.createTensor(env, FloatBuffer.wrap(inputData), shape);

        // Run inference
        OrtSession.Result result = session.run(Collections.singletonMap("input", inputTensor));

        // Extract output
        float[] output = (float[]) result.get(0).getValue();
        return output[0];
    }

    @Override
    public void close() throws Exception {
        session.close();
        env.close();
    }
}
