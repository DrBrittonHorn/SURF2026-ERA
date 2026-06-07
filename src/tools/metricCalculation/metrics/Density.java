package tools.metricCalculation.metrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import tools.metricCalculation.metricTools;

public class Density {
    public static double calculateMetric(String levelText){
        //Based on the assumption that all generators use this character to represent blank space
        levelText = metricTools.applySpatialMapping(levelText); // Applies spatial preprocessing to remedy temporary issues with levels not using '.' as the empty space
        char emptyChar = '.';
        String map = levelText;
        String characterMapping;
        double totalArea = 0;
        double totalOccupiedArea = 0;

        // Split level and description
        if (levelText.contains("LevelDescription")){
            String[] level = levelText.split("LevelDescription");
            characterMapping = level[0];
            map = level[1];
        }
        //System.out.println(map);
        for (int i = 0; i < map.length(); i++){
            
            if (map.charAt(i) == emptyChar){totalArea++;}
            // The letter/digit check eliminates interference from newLine or other special characters
            else if (Character.isLetterOrDigit(map.charAt(i))){
                totalOccupiedArea++;
                totalArea++;
            }            
        }
        //System.out.println("tOA" + totalOccupiedArea);
        //System.out.println("tA" + totalArea);

        //TODO Determine limits for precision
        if (totalArea > 0){
            return totalOccupiedArea / totalArea;
        }
        else{
            return -1; // No level area
        }
        
    }

    //Main function for testing
    public static void main(String[] args) throws IOException{
        String testLevel = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/aliens/aliens_lvl001.txt"));
        System.out.println("Density is... " + calculateMetric(testLevel));
    }
}
