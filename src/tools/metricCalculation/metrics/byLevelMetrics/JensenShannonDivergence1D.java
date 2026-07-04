package tools.metricCalculation.metrics.byLevelMetrics;

import java.io.IOException;

public class JensenShannonDivergence1D {
    public static double calculateMetric(String levelText) throws IOException{
        return JensenShannonDivergence2D.calculateMetric(levelText, 1);
    }
}
