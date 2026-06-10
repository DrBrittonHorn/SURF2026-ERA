package tools.metricCalculation.metrics.byLevelMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import tools.metricCalculation.metricTools;

public class NGramSimilarity2D {
    public static double calculateMetric(String levelText, int nGramSize){
        
        boolean ignorePaddingNGrams = true;
        String paddingToken = "~";
        String levelMap = metricTools.getLevelTiles(levelText);
        String gameFile = metricTools.getGameFilePath(levelText);
        ArrayList<Path> examplePaths = new ArrayList<Path>();

        //Add padding characters to levelMap to create inputLevelLines
        int longestLineLength = 0;
        String[] inputLevelLines = levelMap.trim().split(System.lineSeparator());
        for (String s: inputLevelLines){if (s.length() > longestLineLength){longestLineLength = s.length();}}
        //System.out.println("Longest" + longestLineLength);
        for (String s : inputLevelLines){System.out.println("length" + s.length());}
        for (int i = 0; i < inputLevelLines.length; i++){
            if (inputLevelLines[i].length() < longestLineLength){
                for (int j = 0; j < longestLineLength-inputLevelLines[i].length(); j++){
                    inputLevelLines[i] += paddingToken.repeat(longestLineLength - inputLevelLines[i].length());                
                }
            }
        }
        for (String s : inputLevelLines){System.out.println(s);}

        // Based on the assumption that each game has 5 samples levels
        int totalSampleLevels = 5;
        for (int i = 0; i < totalSampleLevels; i++){
            examplePaths.add(Path.of(gameFile.split(".txt")[0] + "_lvl" + i + ".txt"));
        }
        HashSet<String> inputSamples = new HashSet<String>();
        for (int i = 0; i < inputLevelLines.length-nGramSize; i++){
            for (int j = 0; j < inputLevelLines[i].length()-nGramSize; j++){
                // We will represent our n-gram as a string of length n-gram x n-gram
                //[a, b]
                //[c, d] becomes [abcd]
                // Here, we compose the n-gram of size n-gram x n-gram starting at point [i, j]
                String ngram = "";
                for (int m = 0; m < nGramSize; m++){
                    for (int n = 0; n < nGramSize; n++){
                        ngram += inputLevelLines[m+i].charAt(n+j);
                    }
                }
                inputSamples.add(ngram);
            }
        }
        System.out.println("Input Samples: " + inputSamples);
        


        // Mapping of plagiarism level for all corpus levels
        HashMap<Path, Double> examplesToPlagiarismLevel = new HashMap<Path, Double>();
        for (Path p : examplePaths){
            // Fill existing ngram samples
            HashSet<String> existingSamples = new HashSet<String>();
            try {
                String exampleLevel = Files.readString(p);
                String[] exampleLevelLines = exampleLevel.split("\n");
                for (int i = 0; i < exampleLevelLines.length-nGramSize; i++){
            for (int j = 0; j < exampleLevelLines[i].length()-nGramSize; j++){
                // We will represent our n-gram as a string of length n-gram x n-gram
                //[a, b]
                //[c, d] becomes [abcd]
                // Here, we compose the n-gram of size n-gram x n-gram starting at point [i, j]
                String ngram = "";
                for (int m = 0; m < nGramSize; m++){
                    for (int n = 0; n < nGramSize; n++){
                        ngram += exampleLevelLines[m+i].charAt(n+j);
                    }
                }
                existingSamples.add(ngram);
            }
        }
                //System.out.println(existingSamples);
                //System.out.println(existingSamples);

                
                double totalInputSamples = inputSamples.size();
                // Delete n-grams that feature padding tokens due to rough edges
                if (!ignorePaddingNGrams){existingSamples.removeIf(s -> s.contains("~"));};
                // Get intersection of existing vs input level n-grams
                existingSamples.retainAll(inputSamples);
                HashSet<String> overlaps = existingSamples;
                int totalOverlappingSamples = overlaps.size();
                System.out.println(overlaps + " (overlaps for " + p.toString() + ")");

                // Give the input level a plagiarism level in refernce to the iterated existing corpus level
                // This is calculated by the fraction of n-grams in the input level that can be found in the selected corpus level
                examplesToPlagiarismLevel.put(p, totalOverlappingSamples/totalInputSamples);
            }
            catch (IOException e1) {e1.printStackTrace();}

            
        }

        //System.out.println(examplesToPlagiarismLevel);
        double maxPlagiarism = 0;
        for (Path key : examplesToPlagiarismLevel.keySet()){
            if (examplesToPlagiarismLevel.get(key) > maxPlagiarism){
                maxPlagiarism = examplesToPlagiarismLevel.get(key);
            }
        }
        return maxPlagiarism;
        
        
    }
    
    
    
    public static void main(String[] args) throws IOException{
        String testLevel1 = Files.readString(Path.of("generatedExamples\\geminiLevelGenerator\\aliens\\aliens_lvl001.txt"));
        String testLevel2 = Files.readString(Path.of("generatedExamples\\geminiLevelGenerator\\realsokoban\\realsokoban_lvl002.txt"));
        System.out.println(calculateMetric(testLevel2, 2));

    }
}

// Note: As the n-gram size increases, it is possible for plagiarism to increase as well! This is because an increase in n-gram size causes a reduction in the number of total n-grams in the input level, meaning that if it includes even a few plagiarized segments, those segments will be weighted heavily.