package tools.metricCalculation.metrics.byLevelMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import tools.metricCalculation.metricTools;

// Higher value -> More novel
public class NoveltyScoreToCorpus {
    public static double calculateMetric(String levelText){
        String gameFile = metricTools.getGameFilePath(levelText);
        double totalNovelty = 0;
        ArrayList<Path> examplePaths = new ArrayList<Path>();
        
        // Based on the assumption that each game has 5 samples levels
        int totalSampleLevels = 5;
        for (int i = 0; i < totalSampleLevels; i++){
            examplePaths.add(Path.of(gameFile.split(".txt")[0] + "_lvl" + i + ".txt"));
        }
        for (Path p : examplePaths){ // If we have manually changed level mappings, then comparisons between generated and existing levels will not be accurate
            try {
                totalNovelty += normalizedHammingDistance(metricTools.getLevelTiles(levelText), metricTools.getLevelTiles(Files.readString(p)));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return totalNovelty / totalSampleLevels;
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
        String testLevel1 = Files.readString(Path.of("generatedExamples\\randomLevelGenerator\\aliens\\aliens_lvl001.txt"));
        String testLevel2 = Files.readString(Path.of("generatedExamples\\randomLevelGenerator\\aliens\\aliens_lvl002.txt"));
        System.out.println(calculateMetric(testLevel2));
    }
}
