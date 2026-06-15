package tools.metricCalculation.metrics.byLevelMetrics;

public class JensenShannonDivergence1D {
    public static double calculateMetric(String levelText){
        return JensenShannonDivergence2D.calculateMetric(levelText, 1);
    }
}
