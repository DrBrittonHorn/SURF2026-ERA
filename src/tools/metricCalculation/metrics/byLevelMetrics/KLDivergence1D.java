package tools.metricCalculation.metrics.byLevelMetrics;

public class KLDivergence1D {
    public static double calculateMetric(String levelText){
        return KLDivergence2D.calculateMetric(levelText, 1);
    }
}
