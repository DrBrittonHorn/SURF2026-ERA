package tools.metricCalculation.metrics.byLevelMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import tools.Position;
import tools.metricCalculation.metricTools;

public class HammingDistanceToCorpus {
    
    public static double calculateMetric(String levelPath) throws IOException{
        String levelText = Files.readString(Path.of(levelPath));


        String gameName = levelPath.split("\\\\|/")[2];
        
        // Construct the base path to example files
        String gameFile = "examples/selectedGameFiles/" + gameName + ".txt";
        double totalDistance = 0;
        ArrayList<Path> examplePaths = new ArrayList<Path>();

        // Based on the assumption that each game has 5 samples levels
        int totalSampleLevels = 5;
        for (int i = 0; i < totalSampleLevels; i++){
            examplePaths.add(Path.of(gameFile.split(".txt")[0] + "_lvl" + i + ".txt"));
        }
        for (Path p : examplePaths){
            try {
                totalDistance += NoveltyScoreToCorpus.normalizedHammingDistance(
                    metricTools.getLevelTiles(levelText), 
                    metricTools.getLevelTiles(Files.readString(p)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return totalDistance / totalSampleLevels;
    }
    
    
    
    /**
     * Calculates the Hamming Distance of two levels
     * @param Level1Text the description of the first level
     * @param Level2Text the description of the second level
     * @return the Hamming Distance from the first level to the second level as a double, normalized by the size of the larger level
     */
    private static double hammingDistance(String Level1Text, String Level2Text) {
        double HD = 0.0;
        double totalArea = 0.0;

        String firstMap = Level1Text.strip();
        String secondMap = Level2Text.strip();

        // splits level to get the actual time map
        if (Level1Text.contains("LevelDescription")) {
            String[] level = Level1Text.split("LevelDescription");
            firstMap = level[1].strip();
        }
        if (Level2Text.contains("LevelDescription")) {
            String[] level = Level2Text.split("LevelDescription");
            secondMap = level[1].strip();
        }
        double NormalizationValue = (double) Math.max(firstMap.strip().length(), secondMap.strip().length());

        char[][] FirstMAP = metricTools.toMap(firstMap.strip());
        char[][] SecondMAP = metricTools.toMap(secondMap.strip());

        int maxRows = Math.max(FirstMAP.length, SecondMAP.length);
        int maxCols = Math.max(FirstMAP[0].length, SecondMAP[0].length);

        for (int y = 0; y < maxRows; y++) {
            for (int x = 0; x < maxCols; x++) {

                char tile1 = '\0';
                char tile2 = '\0';

                if (y < FirstMAP.length && x < FirstMAP[y].length) {
                    tile1 = FirstMAP[y][x];
                }
                if (y < SecondMAP.length && x < SecondMAP[y].length) {
                    tile2 = SecondMAP[y][x];
                }

                if (tile1 != tile2) {
                    HD++;
                    totalArea++;
                }
                if (tile1 != '.' || tile2 != '.') {
                    totalArea++;
                }
            }
        }

        if (totalArea > 0){
            return HD / NormalizationValue;
        }
        else{
            return -1; // something went wrong
        }

    }

    public static void main(String[] args) throws IOException {
        String testLevel1 = "generatedExamples/geminiLevelGenerator/aliens/aliens_lvl000.txt";
        String testLevel2 = "generatedExamples/constructiveLevelGenerator/zelda/zelda_lvl001.txt";
        System.out.println("Hamming Distance is " + calculateMetric(testLevel2));
    }
}
