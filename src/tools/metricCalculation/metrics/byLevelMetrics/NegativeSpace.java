package tools.metricCalculation.metrics.byLevelMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import tools.metricCalculation.metricTools;

public class NegativeSpace {
    public static double calculateMetric(String levelPath) throws IOException{
        //Based on the assumption that all generators use this character to represent blank space
        String levelText = Files.readString(Path.of(levelPath));
        levelText = metricTools.applySpatialMapping(levelText); // Applies spatial preprocessing to remedy temporary issues with levels not using '.' as the empty space
        char emptyChar = '.';
        String map = levelText;
        String characterMapping;
        double totalArea = 0;
        double totalNegativeArea = 0;

        // Split level and description
        if (levelText.contains("LevelDescription")){
            String[] level = levelText.split("LevelDescription");
            characterMapping = level[0];
            map = level[1];
        }
        //System.out.println(map);
        for (int i = 0; i < map.length(); i++){
            
            if (map.charAt(i) == emptyChar){
                totalNegativeArea++;
                totalArea++;
            }
            // The letter/digit check eliminates interference from newLine or other special characters
            if (Character.isLetterOrDigit(map.charAt(i))){totalArea++;}          
        }
        //System.out.println("tOA" + totalOccupiedArea);
        //System.out.println("tA" + totalArea);
        if (totalArea > 0){
            return totalNegativeArea/totalArea;
        }
        else{
            return -1; // No level area
        }
    }
        

    public static void main(String[] args) throws IOException{
        String testLevel = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/aliens/aliens_lvl000.txt"));
        System.out.println("Negative Space is... " + calculateMetric(testLevel));
    }
}
