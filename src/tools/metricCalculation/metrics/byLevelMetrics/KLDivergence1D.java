package tools.metricCalculation.metrics.byLevelMetrics;

import java.io.IOException;

public class KLDivergence1D {
    public static double calculateMetric(String levelText) throws IOException{
        return KLDivergence2D.calculateMetric(levelText, 1);
    }
}
