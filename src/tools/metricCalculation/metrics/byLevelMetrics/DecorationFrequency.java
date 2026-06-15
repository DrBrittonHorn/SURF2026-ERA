package tools.metricCalculation.metrics.byLevelMetrics;

import tools.metricCalculation.metricTools;

public class DecorationFrequency {
    public static double calculateMetric(String levelText){
        double totalDeco = 0;
        double totalArea = 0;
        // In https://dl.acm.org/doi/pdf/10.1145/3102071.3102080, decoration was defined as the following: 
        // "Pipe, Enemy, Destructible Block, Question Mark Block, or Bullet Bill Shooter Column" 
        // As such, our implementation counts any non-empty, non-standard block as decoration
        String levelMap = metricTools.applySpatialMapping(levelText);
        for (int i = 0; i < levelMap.length(); i++){
            if (levelMap.charAt(i) != '\n'){
                if (levelMap.charAt(i) != 'S'){
                    totalDeco++;
                }
                totalArea++;
            }
        }
        return totalDeco / totalArea;
    }
}
