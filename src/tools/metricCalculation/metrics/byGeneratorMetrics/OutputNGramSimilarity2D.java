package tools.metricCalculation.metrics.byGeneratorMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Stream;

import tools.metricCalculation.metricTools;

public class OutputNGramSimilarity2D {
    // Fix later
    public static double calculateMetric(String generatorFolderPath, int nGramSize){
        ArrayList<Path> allLevelPaths = new ArrayList<Path>();
        try {
            Stream<Path> levelStream = Files.list(Path.of(generatorFolderPath)).filter(f -> f.toString().endsWith(".txt"));
            levelStream.forEach(path -> {
               allLevelPaths.add(path);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        double totalPlagiarism = 0;
        for (Path path : allLevelPaths){
            for (Path other : allLevelPaths){
                totalPlagiarism += 1.0/(allLevelPaths.size() * allLevelPaths.size()) * getSimilarity1D(path, other, nGramSize);
            }
        }
        
        return totalPlagiarism;
    }

    public static double getSimilarity1D(Path subjectLevelPath, Path otherLevelPath, int nGramSize){
        
        HashSet<String> inputSamples = new HashSet<String>();
        try {
            String[] subjectLevelLiines = metricTools.getLevelTiles(Files.readString(subjectLevelPath)).split(System.lineSeparator());

            for (int i = 0; i < subjectLevelLiines.length; i++){
                for (int j = 0; j < subjectLevelLiines[i].length()-nGramSize; j++){
                    inputSamples.add(subjectLevelLiines[i].substring(j, j+nGramSize));
                }
                //System.out.println("Input Samples: " + inputSamples);
            }        
            // Fill existing ngram samples
            HashSet<String> existingSamples = new HashSet<String>();
            
                String exampleLevel = metricTools.getLevelTiles(Files.readString(otherLevelPath));
                String[] exampleLevelLines = exampleLevel.split(System.lineSeparator());
                for (int i = 0; i < exampleLevelLines.length; i++){
                    for (int j = 0; j < exampleLevelLines[i].length()-nGramSize; j++){
                        existingSamples.add(exampleLevelLines[i].substring(j, j + nGramSize));
                    }
                }
                //System.out.println(existingSamples);                
                
                double totalInputSamples = inputSamples.size();
                // Get intersection of existing vs input level n-grams
                existingSamples.retainAll(inputSamples);
                HashSet<String> overlaps = existingSamples;
                int totalOverlappingSamples = overlaps.size();
                //System.out.println(overlaps);
                
                // Give the input level a plagiarism level in refernce to the other level
                // This is calculated by the fraction of n-grams in the input level that can be found in the selected corpus level
                return totalOverlappingSamples/totalInputSamples;
                
            }
            
            catch (IOException e1){
            e1.printStackTrace();
            }
            return 0;
        }


        public static void main(String[] args) throws IOException{

        String testFolder = "generatedExamples/geminiLevelGenerator/realsokoban";
        System.out.println(calculateMetric(testFolder, 5));

        }
    }
    

