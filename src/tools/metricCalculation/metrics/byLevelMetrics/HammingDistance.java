package tools.metricCalculation.metrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HammingDistance {
    
    /**
     * Calculates the Hamming Distance of two levels
     * @param Level1Text the description of the first level
     * @param Level2Text the description of the second level
     * @return the Hamming Distance from the first level to the second level as a double
     */
    public static double calculateMetric(String Level1Text, String Level2Text) {
        double HD = 0;
        int totalArea = 0;
        double difference = 0; // the difference in size between the two levels

        String firstMap = null;
        String secondMap = null;

        // splits level to get the actual time map
        if (Level1Text.contains("LevelDescription")) {
            String[] level = Level1Text.split("LevelDescription");
            firstMap = level[1].strip();
        }
        if (Level2Text.contains("LevelDescription")) {
            String[] level = Level2Text.split("LevelDescription");
            secondMap = level[1].strip();
        }

        System.out.println(firstMap);
        System.out.println(String.valueOf(firstMap.length()));
        System.out.println(secondMap);
        System.out.println(String.valueOf(secondMap.length()));

        // Determines which level to cycle through based on length
        if (firstMap.length() < secondMap.length()) {
            difference = Math.abs(secondMap.length() - firstMap.length());
            for (int i = 0; i < firstMap.length(); i++){
                if (!(firstMap.charAt(i) == secondMap.charAt(i))) {
                    HD++;
                    totalArea++;
                }
                if (Character.isLetterOrDigit(firstMap.charAt(i))) {totalArea++;}          
            }
        }
        else if (firstMap.length() > secondMap.length()) {
            difference = Math.abs(secondMap.length() - firstMap.length());
            for (int i = 0; i < secondMap.length(); i++){
                if (!(secondMap.charAt(i) == secondMap.charAt(i))) {
                    HD++;
                    totalArea++;
                }
                if (Character.isLetterOrDigit(secondMap.charAt(i))) {totalArea++;}          
            }
        }

        if (totalArea > 0){
            return HD + difference;
        }
        else{
            return -1; // something went wrong
        }

    }

    public static void main(String[] args) throws IOException {
        String testLevel1 = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/zelda/zelda_lvl001.txt"));
        String testLevel2 = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/zelda/zelda_lvl000.txt"));
        System.out.println("Hamming Distance is " + calculateMetric(testLevel1, testLevel2));
    }
}
