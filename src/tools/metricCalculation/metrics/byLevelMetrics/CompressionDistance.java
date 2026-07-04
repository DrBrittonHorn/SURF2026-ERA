package tools.metricCalculation.metrics.byLevelMetrics;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPOutputStream;

import tools.metricCalculation.metricTools;

// Takes an average of compression distance between a generated level and its 5 corpus levels
public class CompressionDistance {
    public static double calculateMetric(String levelPath) throws IOException{
        String inputLevelMap = metricTools.getLevelTiles(Files.readString(Path.of(levelPath)));
        
        // Extract game name from the file path (e.g., generatedExamples/{generator}/aliens/aliens_lvl001.txt -> aliens)
        String gameName = levelPath.split("\\\\|/")[2];
        
        // Construct the base path to example files
        String gamePath = "examples/selectedGameFiles/" + gameName + ".txt";

        String[] exampleLevels = new String[5];
        for (int i = 0; i < exampleLevels.length; i++){
            exampleLevels[i] = metricTools.getLevelTiles(Files.readString(Path.of(gamePath.split(".txt")[0] + "_lvl" + i + ".txt")));
        }

        double totalDistance = 0;
        for (int i = 0; i < exampleLevels.length; i++){
            totalDistance += getNCD(inputLevelMap, exampleLevels[i]);
        }
        return totalDistance / 5;
    }

    public static void main(String[] args) throws IOException{
        String testLevel1 = Files.readString(Path.of("generatedExamples/geminiLevelGenerator/aliens/aliens_lvl001.txt"));
        String testLevel2 = Files.readString(Path.of("generatedExamples/geminiLevelGenerator/realsokoban/realsokoban_lvl002.txt"));
        compressedStringSize(testLevel2);
        System.out.println(calculateMetric(testLevel2));
    }
    
    //NCD(x, y) = [C(xy) − min{C(x), C(y)}] / max{C(x), C(y)}
    public static double getNCD(String level1, String level2) throws IOException{
        int X = compressedStringSize(level1);
        int Y = compressedStringSize(level2);
        int XY = compressedStringSize(level1 + level2);
        return (XY - Math.min(X, Y)) / (double) Math.max(X, Y);
    }

    public static int compressedStringSize(String input) throws IOException{
       byte[] b = input.getBytes();

       ByteArrayOutputStream baos = new ByteArrayOutputStream();
       try (GZIPOutputStream gzip = new GZIPOutputStream(baos)){
        gzip.write(b);
       }
       return baos.size();
    }
}
