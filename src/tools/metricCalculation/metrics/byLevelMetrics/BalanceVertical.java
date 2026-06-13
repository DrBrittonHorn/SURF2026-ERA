package tools.metricCalculation.metrics.byLevelMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import tools.metricCalculation.metricTools;

public class BalanceVertical {
    public static double calculateMetric(String levelText){
        ArrayList<ArrayList<Character>> levelArray = metricTools.toArray(levelText);
        return balanceFromArray(levelArray);
    }

    public static double balanceFromArray(ArrayList<ArrayList<Character>> levelArray){
        Character emptyChar = '.';
        double equator = (levelArray.size()-1)/2.0;
        
        double totalElements = 0;
        double balanceScore = 0;
        for (int i = 0; i < levelArray.size(); i++){
            for (int j = 0; j < levelArray.get(i).size(); j++){
                if (levelArray.get(i).get(j) != emptyChar){
                    totalElements++;
                    balanceScore += equator - i;
                }
            }
        }
        // Decided to normalize balance by totalElements
        // A positive balance score means the level is top heavy, while a negative balance score indicates bottom-heavyness
        return balanceScore / totalElements;
    }

    public static void main(String[] args) throws IOException{
        String testLevel1 = Files.readString(Path.of("generatedExamples/geminiLevelGenerator/aliens/aliens_lvl001.txt"));
        String testLevel2 = Files.readString(Path.of("generatedExamples/geminiLevelGenerator/realsokoban/realsokoban_lvl000.txt"));
        System.out.println(calculateMetric(testLevel2));

    }

    
    
}
