package tools.metricCalculation.metrics.byLevelMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import tools.metricCalculation.metricTools;

// From https://ojs.aaai.org/index.php/AAAI/article/view/28865
public class JensenShannonDivergence2D {
    public static double calculateMetric(String levelText, int windowSize){
        
        boolean ignorePaddingNGrams = true;
        String paddingToken = "~";
        String levelMap = metricTools.getLevelTiles(levelText);
        String gameFile = metricTools.getGameFilePath(levelText);
        ArrayList<Path> examplePaths = new ArrayList<Path>();

        int longestLineLength = 0;
        String[] inputLevelLines = levelMap.trim().split("\n");
        for (String s: inputLevelLines){if (s.length() > longestLineLength){longestLineLength = s.length();}}
        //System.out.println("Longest" + longestLineLength);

        for (int i = 0; i < inputLevelLines.length; i++){
            if (inputLevelLines[i].length() < longestLineLength){
                inputLevelLines[i] += paddingToken.repeat(longestLineLength - inputLevelLines[i].length());                
            }
        }

        // Based on the assumption that each game has 5 samples levels
        int totalSampleLevels = 5;
        for (int i = 0; i < totalSampleLevels; i++){
            examplePaths.add(Path.of(gameFile.split(".txt")[0] + "_lvl" + i + ".txt"));
        }

        HashMap<String, Integer> inputPatternCounts = new HashMap<String, Integer>();
        int totalInputWindows = 0;
        for (int i = 0; i < inputLevelLines.length-windowSize; i++){
            for (int j = 0; j < inputLevelLines[i].length()-windowSize; j++){
                // We will represent our n-gram as a string of length n-gram x n-gram
                //[a, b]
                //[c, d] becomes [abcd]
                // Here, we compose the n-gram of size n-gram x n-gram starting at point [i, j]
                String window = "";
                for (int m = 0; m < windowSize; m++){
                    for (int n = 0; n < windowSize; n++){
                        window += inputLevelLines[m+i].charAt(n+j);
                    }
                }
                inputPatternCounts.put(window, inputPatternCounts.getOrDefault(window, 0)+1);
                totalInputWindows++;
            }
        }

        HashMap<String, Integer> examplesPatternCounts = new HashMap<String, Integer>();
        int totalExamplesWindows = 0;
        for (Path p : examplePaths){
            // Fill existing ngram samples
            try {
                String exampleLevel = Files.readString(p);
                String[] exampleLevelLines = exampleLevel.split("\n");
                for (int i = 0; i < exampleLevelLines.length-windowSize; i++){
                    for (int j = 0; j < exampleLevelLines[i].length()-windowSize; j++){
                        // We will represent our n-gram as a string of length n-gram x n-gram
                        //[a, b]
                        //[c, d] becomes [abcd]
                        // Here, we compose the n-gram of size n-gram x n-gram starting at point [i, j]
                        String window = "";
                        for (int m = 0; m < windowSize; m++){
                            for (int n = 0; n < windowSize; n++){
                                window += exampleLevelLines[m+i].charAt(n+j);
                            }
                        }
                        examplesPatternCounts.put(window, examplesPatternCounts.getOrDefault(window, 0)+1);
                        totalExamplesWindows++;
                    }
                }
            }
            catch (IOException e1) {e1.printStackTrace();}
        }

            //System.out.println(inputPatternCounts);
            //System.out.println(examplesPatternCounts);

            HashMap<String, Double> inputPatternDistribution = new HashMap<String, Double>();
            HashMap<String, Double> examplesPatternDistribution = new HashMap<String, Double>();
            HashMap<String, Double> mixedPatternDistribution = new HashMap<String, Double>();

            // Convert counts to distributions
            for (String pattern : inputPatternCounts.keySet()){
                inputPatternDistribution.put(pattern, inputPatternCounts.get(pattern) / (double) totalInputWindows);
                mixedPatternDistribution.put(pattern, inputPatternCounts.get(pattern) / ((double) totalInputWindows)/2);}
            for (String pattern : examplesPatternCounts.keySet()){
                examplesPatternDistribution.put(pattern, examplesPatternCounts.get(pattern) / (double) totalExamplesWindows);
                mixedPatternDistribution.put(pattern, examplesPatternCounts.get(pattern) / ((double) totalExamplesWindows)/2 + mixedPatternDistribution.getOrDefault(pattern, 0.0));}
                
            // Calculate metric by iterating through the objects present in the input level
            double JSDivergence = 0;
            for (String pattern : inputPatternDistribution.keySet()){
                double p = inputPatternDistribution.get(pattern);
                double q = mixedPatternDistribution.getOrDefault(pattern, 0.0);
                JSDivergence += .5 * (p * Math.log(p / q) / Math.log(2));
                }
            for (String pattern : examplesPatternDistribution.keySet()){
                double p = examplesPatternDistribution.get(pattern);
                double q = mixedPatternDistribution.getOrDefault(pattern, 0.0);
                JSDivergence += .5 * (p * Math.log(p / q) / Math.log(2));
                }
            
            return JSDivergence;
    }
    public static void main(String[] args) throws IOException{
        String testLevel1 = Files.readString(Path.of("generatedExamples/geminiLevelGenerator/aliens/aliens_lvl001.txt"));
        String testLevel2 = Files.readString(Path.of("generatedExamples/geminiLevelGenerator/realsokoban/realsokoban_lvl002.txt"));
        System.out.println(calculateMetric(testLevel2, 4));
    }
    
}


