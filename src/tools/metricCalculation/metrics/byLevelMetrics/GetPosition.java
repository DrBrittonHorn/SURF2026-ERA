package tools.metricCalculation.metrics.byLevelMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;

import tools.Position;
import tools.metricCalculation.metricTools;

import java.util.ArrayList;

public class GetPosition {

    @SuppressWarnings("unused")
    private static AbstractMap.SimpleEntry<Integer, Integer> positionValue;

    /**
     * locates the (X, Y) position of any tile in a level
     * and stores it as an AbstractMap.SImpleEntry<Integer, Integer>
     * @param LevelPath the url path of the level as a String
     * @param get the desired tile as a char
     * @return the position of the tile as a custom position class
     */
    @SuppressWarnings("unused")
    public static Position calculateMetric(String LevelText, char get) {
        
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
        else {
            map = LevelText.strip();
        }

        char[][] MAP = metricTools.toMapOther(map);

        for (int y = 0; y < MAP.length; y++) {
            for (int x = 0; x< MAP[y].length; x++) {
                char tile = MAP[y][x];
                if (tile == get) {
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
}