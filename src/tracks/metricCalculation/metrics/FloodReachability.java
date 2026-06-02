package tracks.metricCalculation.metrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class FloodReachability {
    public static double calculateMetric(String levelText){
        //Based on the assumption that all generators use this character to represent blank space
        char emptyChar = '.';
        String map = levelText;
        String characterMapping;
        double totalArea = 0.0;
        ArrayList<ArrayList<Character>> levelMatrix = new ArrayList<ArrayList<Character>>();
        HashSet<AbstractMap.SimpleEntry<Integer, Integer>> seen = new HashSet<AbstractMap.SimpleEntry<Integer, Integer>>();


        // Split level and description
        if (levelText.contains("LevelDescription")){
            String[] level = levelText.split("LevelDescription");
            characterMapping = level[0];
            map = level[1];
        }

        //Deletes any errrant newLines
        if (map.charAt(0) == '\n'){map = map.substring(1, map.length());}

        System.out.println(map);
        int currentRow = 0;
        for (int i = 0; i < map.length(); i++){
            // If character is a part of the map
            char c = map.charAt(i);
            if (c == '\n'){currentRow++;}
            else if (c == emptyChar || Character.isLetterOrDigit(c)){
                totalArea++;
            }    
        }
        
        return 0;



    }

    public static void main(String[] args) throws IOException{

        String testLevel1 = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/dungeon/dungeon_lvl003.txt")); // 6 blocks should be reachable
        System.out.println("aaaaaaaaa");
        //System.out.println(testLevel);
        System.out.println("Flood reachability is... " + calculateMetric(testLevel1));
    }

}
