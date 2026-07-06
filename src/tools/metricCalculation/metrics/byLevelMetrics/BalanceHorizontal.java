package tools.metricCalculation.metrics.byLevelMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import tools.metricCalculation.metricTools;

public class BalanceHorizontal {
    public static double calculateMetric(String levelPath) throws IOException{
        String levelText = Files.readString(Path.of(levelPath));
        String levelMap = metricTools.getLevelTiles(levelText);
        
        // Add padding for transpose
        String[] lines = levelMap.split(System.lineSeparator());
        int maxLength = 0;
        for (String s: lines){
            if (s.length() > maxLength){maxLength = s.length();}
        }
        for (int i = 0; i < lines.length; i++){
            if (lines[i].length() < maxLength){
                for (int j = 0; j < maxLength-lines[i].length(); j++){
                    lines[i] = lines[i] + ".".repeat(maxLength-lines[i].length());
                }
            }
        }
        char[][] chars = new char[lines[0].length()][lines.length];
        for (int i = 0; i < lines.length; i++){
            for (int j = 0; j < lines[0].length(); j++){
                chars[j][i] = lines[i].charAt(j);
            }
        }
        String levelTransposed = "";
        for (char[] l: chars){
            levelTransposed += System.lineSeparator();
            for (char c : l){
                levelTransposed += c;
            }
        }
        levelTransposed = levelTransposed.substring(1);
        //System.out.println(levelTransposed);
        // -1 ensures that left heavy levels have a negative balance score and right heavy levels have a positive one.
        return -1 * BalanceVertical.calculateMetric(levelTransposed);
    }

    public static void main(String[] args) throws IOException{
        String testLevel1 = Files.readString(Path.of("generatedExamples/geminiLevelGenerator/aliens/aliens_lvl001.txt"));
        String testLevel2 = Files.readString(Path.of("generatedExamples/geminiLevelGenerator/realsokoban/realsokoban_lvl000.txt"));
        System.out.println(calculateMetric(testLevel2));
    }
}
