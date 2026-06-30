package tools.metricCalculation.metrics.byLevelMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.logging.Level;

import tools.metricCalculation.calculateMetrics;

public class TileFrequency {
    
    /**
     * calculates the frequency of each tile of a level in that level
     * @param LevelText the description of the level
     * @return the frequency of each Character tile as a Double in a HashMap
     */
    public static HashMap<Character,Double> calculateMetric(String LevelText) {
        HashMap<Character, Double> count = new HashMap<>();
        HashMap<Character, Double> frequency = new HashMap<>();
        int totalArea = 0;

        String map = null;

        // splits level to get the actual time map
        if (LevelText.contains("LevelDescription")) {
            String[] level = LevelText.split("LevelDescription");
            map = level[1];
        }

        for (int i = 0; i < map.length(); i++) {
            count.merge(map.charAt(i), 1.0, Double::sum);
            totalArea++;
            if (Character.isLetterOrDigit(map.charAt(i))) {totalArea++;}
        }

        for (Character key : count.keySet()) {
            frequency.put(key, (count.get(key) / totalArea));
        }        

        return frequency;
    }

    /**
     * calculates the frequency of each tile of a level in that level
     * USED FOR WITHIN A LEVEL WHEN "LevelDescription" IS NOT PRESENT
     * @param LevelText the description of the level
     * @return the frequency of each Character tile as a Double in a HashMap
     */
    public static HashMap<Character,Double> calculateMetricOther(String LevelText) {
        HashMap<Character, Double> count = new HashMap<>();
        HashMap<Character, Double> frequency = new HashMap<>();
        int totalArea = 0;

        String map = LevelText.strip();

        // splits level to get the actual time map
        if (LevelText.contains("LevelDescription")) {
            String[] level = LevelText.split("LevelDescription");
            map = level[0];
        }

        for (int i = 0; i < map.length(); i++) {
            count.merge(map.charAt(i), 1.0, Double::sum);
            totalArea++;
            if (Character.isLetterOrDigit(map.charAt(i))) {totalArea++;}
        }

        for (Character key : count.keySet()) {
            frequency.put(key, (count.get(key) / totalArea));
        }        

        return frequency;
    }

        /**
         * calculates the frequency of each tile of a level in that level
         * USED FOR WITHIN A LEVEL AND TO JUST RETURN WHOLE NUMBERS
         * @param LevelText the description of the level
         * @return the frequency of each Character tile as a Double in a HashMap
         */
        @SuppressWarnings("unused")
        public static HashMap<Character,Double> calculateMetricOtherOther(String LevelText) {
        HashMap<Character, Double> count = new HashMap<>();
        HashMap<Character, Double> frequency = new HashMap<>();
        int totalArea = 0;

        String map = LevelText.strip();

        // splits level to get the actual time map
        if (LevelText.contains("LevelDescription")) {
            String[] level = LevelText.split("LevelDescription");
            map = level[0];
        }

        for (int i = 0; i < map.length(); i++) {
            count.merge(map.charAt(i), 1.0, Double::sum);
            totalArea++;
            if (Character.isLetterOrDigit(map.charAt(i))) {totalArea++;}
        }

        for (Character key : count.keySet()) {
            frequency.put(key, (count.get(key)));
        }        

        return frequency;
    }


    public static void main(String[] args) throws IOException {
        String testLevel = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/zelda/zelda_lvl000.txt"));
        System.out.println("Tile Frequencies are " + calculateMetric(testLevel));
    }

}
