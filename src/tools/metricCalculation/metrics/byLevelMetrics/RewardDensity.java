package tools.metricCalculation.metrics.byLevelMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import tools.metricCalculation.metricTools;

public class RewardDensity {
    public static double calculateMetric(String levelPath) throws IOException{
        String levelText = Files.readString(Path.of(levelPath));
        String levelMap = metricTools.applySpatialMapping(levelText);
        double totalRewards = 0;
        int totalArea = 0;
        for (int i = 0; i < levelMap.length(); i++){
            if (levelMap.charAt(i) != '\n'){
                if (levelMap.charAt(i) == 'G' || levelMap.charAt(i) == 'C'){
                    totalRewards += 1;
                }
                totalArea += 1;
            }
        }
        return totalRewards/totalArea;
    }

    public static void main(String[] args) throws IOException{
        String testLevel = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/dungeon/dungeon_lvl002.txt"));
        System.out.println("Reward Density is... " + calculateMetric(testLevel));
    }
}
