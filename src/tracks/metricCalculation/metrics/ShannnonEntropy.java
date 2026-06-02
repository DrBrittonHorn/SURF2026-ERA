package tracks.metricCalculation.metrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

//See https://www.youtube.com/watch?v=YtebGVx-Fxw
public class ShannnonEntropy {
    // Calculates Shannon Entropy (in Bits)
    public static double calculateMetric(String levelText){
        //Based on the assumption that all generators use this character to represent blank space
        char emptyChar = '.';
        String map = levelText;
        String characterMapping;
        double totalArea = 0.0;
        HashMap<Character, Integer> totals = new HashMap<Character, Integer>();
        HashMap<Character, Double> probs = new HashMap<Character, Double>();

        // Split level and description
        if (levelText.contains("LevelDescription")){
            String[] level = levelText.split("LevelDescription");
            characterMapping = level[0];
            map = level[1];
        }
        //System.out.println(map);
        for (int i = 0; i < map.length(); i++){
            // If character is a part of the map
            char c = map.charAt(i);
            if (c == emptyChar || Character.isLetterOrDigit(c)){
                totalArea++;
                totals.put(c, totals.getOrDefault(c, 0)+1);
            }    
        }
        double t = totalArea; // Used to get around "Local variable (totalArea) defined in an enclosing scope must be final or effectively final"
        totals.forEach((k, v) -> {
            probs.put(k, totals.get(k)/t);
        });

        //System.out.println(probs.toString());

        double totalEntropy = 0;
        for (Map.Entry<Character, Double> entry: probs.entrySet()){
            //The "surprise" value of one of our entries times the probability of receiving that "surprise".
            totalEntropy += (Math.log(1/entry.getValue())/Math.log(2)) * (entry.getValue());
        }
        
        return totalEntropy;



    }

    //Main function for testing
    public static void main(String[] args) throws IOException{
        String testString1 = "a b b bbbb bbb"; // (Spaces should be ignored)
        String testString2 = "abc da bcd abcd";
        System.out.println("Shannon Entropy is... " + calculateMetric(testString1)); // should be .469 bits of entropy
        System.out.println("Shannon Entropy is... " + calculateMetric(testString2)); // should be .469 bits of entropy

        String testLevel = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/frogs/frogs_lvl001.txt"));
        System.out.println(testLevel);
        System.out.println("Shannon Entropy is... " + calculateMetric(testLevel));
    }
}