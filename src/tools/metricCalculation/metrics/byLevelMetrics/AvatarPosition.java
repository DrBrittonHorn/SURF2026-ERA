package tools.metricCalculation.metrics.byLevelMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;

import tools.Position;
import tools.metricCalculation.metricTools;

import java.util.ArrayList;

public class AvatarPosition {

    @SuppressWarnings("unused")
    private static AbstractMap.SimpleEntry<Integer, Integer> positionValue;

    /**
     * locates the (X, Y) position of the avatar
     * and stores it as an AbstractMap.SImpleEntry<Integer, Integer>
     * @param LevelPath the url path of the level
     * @return the avatar position as a custom position class
     */
    @SuppressWarnings("unused")
    public static Position calculateMetric(String LevelText) {
        
        int totalArea = 0;
        Integer Xvalue = 0;
        Integer Yvalue = 0;
        Position returned = null;

        String charMap = null;
        String map = null;

        // splits level to get the actual time map
        if (LevelText.contains("LevelDescription")) {
            String[] level = LevelText.split("LevelDescription");
            charMap = level[0];
            map = level[1];
        }

        char[][] MAP = metricTools.toMap(map);

        for (int y = 0; y < MAP.length; y++) {
            for (int x = 0; x< MAP[y].length; x++) {
                char tile = MAP[y][x];
                if (tile == 'A') {
                    Xvalue = x;
                    Yvalue = y;
                    totalArea++;
                    returned = new Position(x, y);
                }
                if (Character.isLetterOrDigit(tile)) {
                    totalArea++;
                }
            }
        }

        positionValue = new SimpleEntry<Integer,Integer>(Xvalue, Yvalue);
        
        return returned;
    }

    public static void main(String[] args) throws IOException {
        String testLevel = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/zelda/zelda_lvl000.txt"));
        System.out.println("Avatar Position is " + calculateMetric(testLevel).toString());
    }
}
