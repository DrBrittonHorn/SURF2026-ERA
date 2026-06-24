package tools.metricCalculation.metrics.byGeneratorMetrics;

import java.io.IOException;

public class OutputJensenShannonDivergence1D {
    public static double calculateMetric(String generatorFolderPath) throws IOException{
        return OutputJensenShannonDivergence2D.calculateMetric(generatorFolderPath, 1);
    }
}
