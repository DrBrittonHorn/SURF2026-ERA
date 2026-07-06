package tools.metricCalculation.metrics.byLevelMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import tools.metricCalculation.metricTools;

// Computes and averages the Levenshtein Distance between each of the original corpus levels
// The higher the metric, the lower the similarity
// In computation, distances are normalized by the length of the largest string
public class LevenshteinDistance {
    public static double calculateMetric(String levelPath) throws IOException{
        String levelText = Files.readString(Path.of(levelPath));
        String levelMap = metricTools.getLevelTiles(levelText);
        
        String gameName = levelPath.split("\\\\|/")[2];
        double accumulatedDifference = 0;
        ArrayList<Path> examplePaths = new ArrayList<Path>();
        
        // Based on the assumption that each game has 5 samples levels
        int totalSampleLevels = 5;
        for (int i = 0; i < totalSampleLevels; i++){
            examplePaths.add(Path.of("examples/selectedGameFiles/" + gameName + "_lvl" + i + ".txt"));
        }

        for (Path p : examplePaths){
            String exampleMap = metricTools.getLevelTiles(Files.readString(p));
            //System.out.println(exampleMap);
            double distance = levenshteinFullMatrixNormalized(levelMap, exampleMap);
            accumulatedDifference += distance;
        }

        //System.out.println(levelMap);
        return accumulatedDifference / 5; // Divide by number of levels
    }   
    
    public static void main(String[] args) throws IOException{
        System.out.println("AAAAAAAAAAAAAAAA");
        String testLevel1 = Files.readString(Path.of("generatedExamples\\geminiLevelGenerator\\aliens\\aliens_lvl001.txt"));
        String testLevel2 = Files.readString(Path.of("generatedExamples\\geminiLevelGenerator\\aliens\\aliens_lvl000.txt"));
        System.out.println(calculateMetric(testLevel2));
    }


    public static double levenshteinFullMatrixNormalized(String str1, String str2) {
        int m = str1.length();
        int n = str2.length();

        // Create a 2D array to store the dynamic programming results
        int[][] dp = new int[m + 1][n + 1];

        // Initialize the base cases
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }

        // Fill in the DP array using the recurrence relation
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    // Characters match, no operation needed
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    // Characters don't match, consider the minimum of insert, remove, and replace
                    dp[i][j] = 1 + Math.min(
                            // Insert
                            dp[i][j - 1],
                            Math.min(
                                    // Remove
                                    dp[i - 1][j],
                                    // Replace
                                    dp[i - 1][j - 1]));
                }
            }
        }

        // Result is stored in the bottom-right cell of the DP array
        int result =  dp[m][n];
        return result / (double) Math.max(str1.length(), str2.length());
    }

}
