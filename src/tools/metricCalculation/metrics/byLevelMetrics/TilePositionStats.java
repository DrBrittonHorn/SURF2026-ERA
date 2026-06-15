package tools.metricCalculation.metrics.byLevelMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

import tools.Position;
import tools.metricCalculation.metricTools;

public class TilePositionStats {
    
    /*
    For each of the tiles we get:
    • µx and σx - the mean and standard deviation x position
    of that tile type.
    • µy and σy - the mean and standard deviation y position of
    that tile type.
    */

    public static double CalculateMean(ArrayList<Double> values) {
        double sum = 0.0;
        double total = (double) values.size();

        for (int i = 0; i < values.size(); i++) {
            sum += values.get(i);
        }

        return sum / total;
    }

    public static double CalculateStanDev(ArrayList<Double> values) {
        double mean = CalculateMean(values);
        double sumSquaredDeviations = 0.0;

        for (int i = 0; i < values.size(); i++) {
            double deviation = values.get(i) - mean;
            sumSquaredDeviations += deviation * deviation;
        }

        return Math.sqrt(sumSquaredDeviations / values.size());
    }

    public static HashMap<Character, ArrayList<Double>> calculateMetric(String LevelText) {
        // each tile will have an ArrayList<Double> with it where [0] = µx mean x, [1] = σx SD x, [2] = µy mean y, and [3] = σy SD y
        HashMap<Character, ArrayList<Double>> TileStats = new HashMap<>();
        int totalArea = 0;
        String levelMap = null;

        // splits level to get the actual map
        if (LevelText.contains("LevelDescription")) {
            String[] level = LevelText.split("LevelDescription");
            levelMap = level[1].strip();
        }

        char[][] levelGraph = metricTools.toMap(levelMap);

        HashMap<Character, ArrayList<Position>> tilePositions = new HashMap<>();

        for (int y = 0; y < levelGraph.length; y++) {
            for (int x = 0; x < levelGraph[y].length; x++) {
                char tile = levelGraph[y][x];
                if (!tilePositions.containsKey(tile)) {
                    tilePositions.put(tile, new ArrayList<>());
                    tilePositions.get(tile).add(new Position(x, y));
                }
                else if (tilePositions.containsKey(tile)) {
                    tilePositions.get(tile).add(new Position(x, y));
                }
                totalArea++;

                if (Character.isLetterOrDigit(tile)) {
                    totalArea++;
                }
            }
        }

        for (Character key : tilePositions.keySet()) {
            ArrayList<Position> positions = tilePositions.get(key);
            ArrayList<Double> xs = new ArrayList<>();
            ArrayList<Double> ys = new ArrayList<>();
            for (int i = 0; i < positions.size(); i++) {
                xs.add(positions.get(i).getX());
                ys.add(positions.get(i).getY());
            }

            ArrayList<Double> stats = new ArrayList<>();
            stats.add(CalculateMean(xs));
            stats.add(CalculateStanDev(xs));
            stats.add(CalculateMean(ys));
            stats.add(CalculateStanDev(ys));

            TileStats.put(key, stats);
        }

        // System.out.println(levelMap);
        // System.out.println(tilePositions);
        return TileStats;
    }

    public static void main(String[] args) throws IOException {
        String testLevel = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/zelda/zelda_lvl000.txt"));
        System.out.println("Tile Position Stats are " + String.valueOf(calculateMetric(testLevel)));
    }
}
