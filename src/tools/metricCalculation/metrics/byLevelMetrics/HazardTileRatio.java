package tools.metricCalculation.metrics.byLevelMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import tools.metricCalculation.metricTools;

public class HazardTileRatio {
    public static double calculateMetric(String levelPath) throws IOException{
        String levelText = Files.readString(Path.of(levelPath));
        double totalDeco = 0;
        double totalArea = 0;
        // The ratio of tiles that can harm to player to total tiles in the level
        String levelMap = metricTools.applySpatialMapping(levelText, levelPath.split("\\\\|/")[2]);
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
