package tools.metricCalculation.metrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Symmetry {
    
    public static double calculateMetric(String LevelText) {
        double Symmetry = 0.0;

        
        return Symmetry;
    }

    public static void main(String[] args) throws IOException {
        String testLevel = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/zelda/zelda_lvl000.txt"));
        System.out.println("Avatar Position is " + String.valueOf(calculateMetric(testLevel)));
    }
}