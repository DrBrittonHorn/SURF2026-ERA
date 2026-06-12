package tools.metricCalculation.metrics.byLevelMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class TilePositionStats {
    
    /*
    For each of the tiles we get:
    • µx and σx - the mean and standard deviation x position
    of that tile type.
    • µy and σy - the mean and standard deviation y position of
    that tile type.
    */

    public static HashMap<Character, ArrayList<Double>> calculateMetric(String LevelText) {
        HashMap<Character, ArrayList<Double>> TileStats = new HashMap<>();


        return TileStats;
    }

    public static void main(String[] args) throws IOException {
        //String testLevel = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/zelda/zelda_lvl000.txt"));
        //System.out.println("Avatar Position is " + String.valueOf(calculateMetric(testLevel)));
    }
}
