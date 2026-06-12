package tools.metricCalculation.metrics.byLevelMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import tools.metricCalculation.metricTools;

public class NGramSimilarity1D {
    public static double calculateMetric(String levelText, int nGramSize){
        String levelMap = metricTools.getLevelTiles(levelText);
        String gameFile = metricTools.getGameFilePath(levelText);
        ArrayList<Path> examplePaths = new ArrayList<Path>();
        
        // Based on the assumption that each game has 5 samples levels
        int totalSampleLevels = 5;
        for (int i = 0; i < totalSampleLevels; i++){
            examplePaths.add(Path.of(gameFile.split(".txt")[0] + "_lvl" + i + ".txt"));
        }
        HashSet<String> inputSamples = new HashSet<String>();
        String[] inputLevelLines = levelMap.split(System.lineSeparator());
        for (int i = 0; i < inputLevelLines.length; i++){
            for (int j = 0; j < inputLevelLines[i].length()-nGramSize; j++){
                inputSamples.add(inputLevelLines[i].substring(j, j+nGramSize));
            }
        }
        //System.out.println("Input Samples: " + inputSamples);
        


        // Mapping of plagiarism level for all corpus levels
        HashMap<Path, Double> examplesToPlagiarismLevel = new HashMap<Path, Double>();
        for (Path p : examplePaths){
            // Fill existing ngram samples
            HashSet<String> existingSamples = new HashSet<String>();
            try {
                String exampleLevel = Files.readString(p);
                String[] exampleLevelLines = exampleLevel.split("\n");
                for (int i = 0; i < exampleLevelLines.length; i++){
                    for (int j = 0; j < exampleLevelLines[i].length()-nGramSize; j++){
                        existingSamples.add(exampleLevelLines[i].substring(j, j + nGramSize));
                    }
                }
                //System.out.println(existingSamples);
                //System.out.println(existingSamples);

                
                double totalInputSamples = inputSamples.size();
                // Get intersection of existing vs input level n-grams
                existingSamples.retainAll(inputSamples);
                HashSet<String> overlaps = existingSamples;
                int totalOverlappingSamples = overlaps.size();
                //System.out.println(overlaps);

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
        System.out.println(calculateMetric(testLevel2, 5));

    }
}

// Note: As the n-gram size increases, it is possible for plagiarism to increase as well! This is because an increase in n-gram size causes a reduction in the number of total n-grams in the input level, meaning that if it includes even a few plagiarized segments, those segments will be weighted heavily.