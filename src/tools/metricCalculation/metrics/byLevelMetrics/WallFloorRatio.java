package tools.metricCalculation.metrics.byLevelMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;

import tools.metricCalculation.metricTools;

// This metric uses spatial preprocessing, walls are counted as any tile that is non-traversible
public class WallFloorRatio {
    public static double calculateMetric(String levelPath) throws IOException{
        //System.out.println(levelText);
        String levelText = Files.readString(Path.of(levelPath));
        levelText = metricTools.applySpatialMapping(levelText);
        //System.out.println(levelText);
        //Based on the assumption that all generators use this character to represent blank space
        HashSet<Character> traversable = new HashSet<Character>(); // Based on the tile conversion for spatial preprocessing
        traversable.add(' '); // Empty Blocks
        traversable.add('.'); // Empty Blocks
        traversable.add('G'); // Goal
        traversable.add('T'); // "Transparent Block"
        traversable.add('B'); // Breakable and movable Blocks
        //traversible.add('E'); // Enemies // Commented out because some enemies never move

        String map = levelText;
        double totalArea = 0;
        double traversableArea = 0;

        // Split level and description
        if (levelText.contains("LevelDescription")){
            String[] level = levelText.split("LevelDescription");
            map = level[1];
        }
        //System.out.println(map);
        for (int i = 0; i < map.length(); i++){
            if (map.charAt(i) != '\n'){
                if (traversable.contains(map.charAt(i))){
                
                traversableArea++;
                }
            }
            totalArea++;

        }
        
        //TODO Determine limits for precision
        if (totalArea > 0){
            // Walls / floor
            return (totalArea - traversableArea) / traversableArea;
        }
        else{
            return -1; // No level area to divide by
        }
        
    }


    public static void main(String[] args) throws IOException{

        String testLevel1 = Files.readString(Path.of("generatedExamples/geminiLevelGenerator/dungeon/dungeon_lvl003.txt"));
        testLevel1 = Files.readString(Path.of("generatedExamples/geminiLevelGenerator/aliens/aliens_lvl001.txt"));
        testLevel1 = Files.readString(Path.of("generatedExamples/geneticLevelGenerator/realsokoban/realsokoban_lvl706.txt"));
        //System.out.println(testLevel1);
        System.out.println("Wall Floor Ratio is... " + calculateMetric(testLevel1));
    }
}