package tools.metricCalculation.metrics.byGeneratorMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Stream;

public class OutputNGramSimilarity1D {
    // Fix later
    public static double calculateMetric(String generatorFolderPath, int nGramSize){
        ArrayList<Path> allLevelPaths = new ArrayList<Path>();
        try {
            
            int totalLevels = 0;
            double totalSimilarity = 0;
            Stream<Path> levelStream = Files.list(Path.of(generatorFolderPath)).filter(f -> f.toString().endsWith(".txt"));
            levelStream.forEach(path -> {
               // allLevelPaths.add(f);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        //return getSimilarity1D()
        return 0;
    }

    public double getSimilarity1D(Path subjectLevelPath, ArrayList<Path> remainingOutputLevels, int nGramSize){
        int totalSampleLevels = remainingOutputLevels.size();
        
        HashSet<String> inputSamples = new HashSet<String>();
        try {
            String[] subjectLevelLiines = Files.readString(subjectLevelPath).split(System.lineSeparator());

            for (int i = 0; i < subjectLevelLiines.length; i++){
                for (int j = 0; j < subjectLevelLiines[i].length()-nGramSize; j++){
                    inputSamples.add(subjectLevelLiines[i].substring(j, j+nGramSize));
                }
                System.out.println("Input Samples: " + inputSamples);
            }
        
            HashMap<Path, Double> examplesToPlagiarismLevel = new HashMap<Path, Double>();
        
            for (Path p : remainingOutputLevels){
                // Fill existing ngram samples
                HashSet<String> existingSamples = new HashSet<String>();
                
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
                    System.out.println(overlaps);

                    // Give the input level a plagiarism level in refernce to the iterated existing corpus level
                    // This is calculated by the fraction of n-grams in the input level that can be found in the selected corpus level
                    examplesToPlagiarismLevel.put(p, totalOverlappingSamples/totalInputSamples);
                }
                

                double maxPlagiarism = 0;
                for (Path key : examplesToPlagiarismLevel.keySet()){
                    if (examplesToPlagiarismLevel.get(key) > maxPlagiarism){
                        maxPlagiarism = examplesToPlagiarismLevel.get(key);
                    }
                }
                return maxPlagiarism;
            }
            
            catch (IOException e1){
            e1.printStackTrace();
            }
            return 0;
        }


        public static void main(String[] args) throws IOException{
        String testLevel2 = Files.readString(Path.of("generatedExamples\\geminiLevelGenerator\\realsokoban"));
        System.out.println(calculateMetric(testLevel2, 5));

    }
    }
    

