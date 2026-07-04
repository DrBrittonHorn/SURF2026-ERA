package tools.metricCalculation.metrics.byLevelMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import tools.metricCalculation.calculateMetrics;
import tools.metricCalculation.metricTools;

// Todo: This metric, density estimate charts
// Todo: Moving tile frames
public class NaiveSimilarity {
    public static double calculateMetric(String levelPath) throws IOException{
        String levelText = Files.readString(Path.of(levelPath));
        String gameName = levelPath.split("/")[2];
        String gameFile = "examples/selectedGameFiles/" + gameName + ".txt";
        System.out.println(gameFile);
        double accumulatedSimilarity = 0;
        ArrayList<Path> examplePaths = new ArrayList<Path>();
        
        // Based on the assumption that each game has 5 samples levels
        int totalSampleLevels = 5;
        for (int i = 0; i < totalSampleLevels; i++){
            examplePaths.add(Path.of(gameFile.split(".txt")[0] + "_lvl" + i + ".txt"));
        }
        for (Path p : examplePaths){ // If we have manually changed level mappings, then comparisons between generated and existing levels will not be accurate
            try {
                accumulatedSimilarity += metricTools.similarityScore(metricTools.toArray(levelText), metricTools.toArray(Files.readString(p)));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return accumulatedSimilarity / totalSampleLevels;
    }


    public static void main(String[] args) throws IOException{
        String testLevel1 = Files.readString(Path.of("generatedExamples\\geminiLevelGenerator\\aliens\\aliens_lvl001.txt"));
        String testLevel2 = Files.readString(Path.of("generatedExamples\\geminiLevelGenerator\\aliens\\aliens_lvl002.txt"));
        System.out.println(calculateMetric(testLevel2));
    }
}