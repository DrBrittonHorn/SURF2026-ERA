package tools.metricCalculation.metrics.byGeneratorMetrics;

import java.io.IOException;

public class OutputKLDivergence1D {
    public static double calculateMetric(String generatorPath) throws IOException{
        return OutputKLDivergence2D.calculateMetric(generatorPath, 1);
    }
}
