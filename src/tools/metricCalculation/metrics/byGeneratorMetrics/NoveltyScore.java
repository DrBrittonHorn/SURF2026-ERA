package tools.metricCalculation.metrics.byGeneratorMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;

import tools.metricCalculation.metricTools;

// Calculates a generator's novelty score (using Hamming distance)
//https://arxiv.org/pdf/2204.06934

public class NoveltyScore {
    public static double calculateMetric(String generatorFolderPath) {
        ArrayList<String> levels = new ArrayList<String>();
        Stream<Path> levelPaths;
        try {
            levelPaths = Files.walk(Path.of(generatorFolderPath)).filter(p -> p.toString().endsWith(".txt"));
            levelPaths.forEach(path -> {
                try {
                    String levelMap = metricTools.getLevelTiles(Files.readString(path));
                    levels.add(levelMap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            double totalNormalizedDistance = 0;
            for (String s1 : levels){
                for (String s2 : levels){
                    //System.out.println(normalizedHammingDistance(s1, s2));
                    totalNormalizedDistance += normalizedHammingDistance(s1, s2);
                }
            }
            return totalNormalizedDistance / levels.size();
        } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
        
        return -1;
    }

    public static double normalizedHammingDistance(String str1, String str2){
        double totalDistance = 0;
        for (int i = 0; i < Math.min(str1.length(), str2.length()); i++){
            if (str1.charAt(i) != str2.charAt(i)){
                totalDistance++;
            }
        }
        totalDistance += Math.max(str1.length(), str2.length()) - Math.min(str1.length(), str2.length());
        return totalDistance / Math.max(str1.length(), str2.length());
    }

    public static void main(String[] args) throws IOException{

        String testFolder = "generatedExamples/geminiLevelGenerator/zelda";
        System.out.println(calculateMetric(testFolder));

    }
}
