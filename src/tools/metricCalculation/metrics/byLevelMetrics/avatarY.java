package tools.metricCalculation.metrics.byLevelMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import tools.metricCalculation.metricTools;

public class avatarY {
    public static double calculateMetric(String levelPath) throws IOException{
        String levelText = Files.readString(Path.of(levelPath));

        String levelprocessed = metricTools.applyPadding(metricTools.getLevelTiles(levelText));
        
        ArrayList<ArrayList<Character>> array = metricTools.toArray(levelprocessed);

        for (ArrayList<Character> a : array){
            for (Character c : a){
                if (c == 'A'){
                    //System.out.println(a.size());
                    return (array.size() - (array.indexOf(a))) / ((double) array.size());
                }
            }
        }
        // Avatar not found
        return -1;

        
    }

    public static void main(String[] args) throws IOException{
        String testLevel1 = ("generatedExamples\\geminiLevelGenerator\\aliens\\aliens_lvl001.txt");
        String testLevel2 = ("generatedExamples\\geminiLevelGenerator\\aliens\\aliens_lvl002.txt");
        System.out.println(calculateMetric(testLevel1));
    }
}
