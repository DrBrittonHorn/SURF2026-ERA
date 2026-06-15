package tools.metricCalculation.metrics.byLevelMetrics;

import tools.metricCalculation.metricTools;

public class HazardTileRatio {
    public static double calculateMetric(String levelText){
        double totalDeco = 0;
        double totalArea = 0;
        // The ratio of tiles that can harm to player to total tiles in the level
        String levelMap = metricTools.applySpatialMapping(levelText);
        for (int i = 0; i < levelMap.length(); i++){
            if (levelMap.charAt(i) != '\n'){
                if (levelMap.charAt(i) == 'E' || levelMap.charAt(i) == 'O'){
                    totalDeco++;
                }
                totalArea++;
            }
        }
        return totalDeco / totalArea;
    }
}
