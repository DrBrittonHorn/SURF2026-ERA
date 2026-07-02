package tools.metricCalculation.metrics.byGeneratorMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

import tools.metricCalculation.metricTools;
import tools.metricCalculation.metrics.byLevelMetrics.JensenShannonDivergence2D;
import tools.metricCalculation.metrics.byLevelMetrics.KLDivergence2D;

public class OutputKLDivergence2D {
    public static double calculateMetric(String generatorFolderPath, int windowSize) throws IOException{
        ArrayList<Path> allLevelPaths = new ArrayList<Path>();
        try {
            Stream<Path> levelStream = Files.list(Path.of(generatorFolderPath)).filter(f -> f.toString().endsWith(".txt"));
            levelStream.forEach(path -> {
               allLevelPaths.add(path);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        double totalDivergence = 0;
        for (Path path1 : allLevelPaths){
            //System.out.println(path1.toString());
            for (Path path2 : allLevelPaths){
                //System.out.println(path2.toString());
                // Level1
                int level1TotalWindows = 0;
                HashMap<String, Integer> level1Counts = new HashMap<String, Integer>();
                String level1 = metricTools.getLevelTiles(Files.readString(path1));
                String[] level1Lines = (level1).split("\n");
                
                int longestLineLength1 = 0;
                for (String s: level1Lines){if (s.length() > longestLineLength1){longestLineLength1 = s.length();}}
                //System.out.println("Longest" + longestLineLength);

                for (int i = 0; i < level1Lines.length; i++){
                    if (level1Lines[i].length() < longestLineLength1){
                        level1Lines[i] += "~".repeat(longestLineLength1 - level1Lines[i].length());                
                    }
                }

                //for (String s : level1Lines){System.out.println(s); System.out.println(s.length());}
                
                for (int i = 0; i < level1Lines.length-windowSize; i++){
                    for (int j = 0; j < level1Lines[i].length()-windowSize; j++){
                        // We will represent our n-gram as a string of length n-gram x n-gram
                        //[a, b]
                        //[c, d] becomes [abcd]
                        // Here, we compose the n-gram of size n-gram x n-gram starting at point [i, j]
                        String window = "";
                        for (int m = 0; m < windowSize; m++){
                            for (int n = 0; n < windowSize; n++){
                                window += level1Lines[m+i].charAt(n+j);
                            }
                        }
                        level1Counts.put(window, level1Counts.getOrDefault(window, 0)+1);
                        level1TotalWindows++;
                    }
                }
                // Level2
                int level2TotalWindows = 0;
                HashMap<String, Integer> level2Counts = new HashMap<String, Integer>();
                String level2 = Files.readString(path2);
                String[] level2Lines = level2.split("\n");

                int longestLineLength2 = 0;
                for (String s: level2Lines){if (s.length() > longestLineLength2){longestLineLength2 = s.length();}}
                //System.out.println("Longest" + longestLineLength);

                for (int i = 0; i < level2Lines.length; i++){
                    if (level2Lines[i].length() < longestLineLength2){
                        level2Lines[i] += "~".repeat(longestLineLength2 - level2Lines[i].length());                
                    }
                }


                for (int i = 0; i < level2Lines.length-windowSize; i++){
                    for (int j = 0; j < level2Lines[i].length()-windowSize; j++){
                        // We will represent our n-gram as a string of length n-gram x n-gram
                        //[a, b]
                        //[c, d] becomes [abcd]
                        // Here, we compose the n-gram of size n-gram x n-gram starting at point [i, j]
                        String window = "";
                        for (int m = 0; m < windowSize; m++){
                            for (int n = 0; n < windowSize; n++){
                                window += level2Lines[m+i].charAt(n+j);
                            }
                        }
                        level2Counts.put(window, level2Counts.getOrDefault(window, 0)+1);
                        level2TotalWindows++;
                    }
                }

                
                double addedTerm = KLDivergence2D.KLDivergence(level1Counts, level2Counts, level1TotalWindows, level2TotalWindows);
                //System.out.println(addedTerm);
                if (Double.isNaN(addedTerm)){
                    ArithmeticException a = new ArithmeticException("NaN detected for " + path1.toString() + "\n and \n" + path2.toString());
                    a.printStackTrace();
                    
                }
                totalDivergence += addedTerm;
            }
        }
        
        // Return total normalized by the total number of comparisons (n^2)
        double ret = totalDivergence / (allLevelPaths.size() * allLevelPaths.size());
        //System.out.println("returning " + ret);
        return ret;
    }

    public static void main(String[] args) throws IOException{

        String testFolder = "generatedExamples/geminiLevelGenerator/dungeon";
        testFolder = "generatedExamples/claudeLevelGenerator/zelda";
        testFolder = "generatedExamples/randomLevelGenerator/asteroids";
        System.out.println(calculateMetric(testFolder, 2));

    }
}
