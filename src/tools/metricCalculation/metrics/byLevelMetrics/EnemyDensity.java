package tools.metricCalculation.metrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class EnemyDensity {
    
    public static double calculateMetric(String LevelPath) throws IOException {
        
        int EnemyNum = EnemyCount.calculateMetric(LevelPath);
        ArrayList<Integer> positions = EnemyCount.Enemyxvalues;
        
        if (positions.isEmpty() || EnemyNum < 1) {
            return -1;
        }

        int positiontotal = 0;
        for (int i = 0; i < positions.size(); i++) {
            positiontotal+=positions.get(i);
        }
        double average = (double) positiontotal/positions.size();

        ArrayList<Double> distances = new ArrayList<>();
        for (int i = 0; i < positions.size(); i++) {
            distances.add(Math.abs(positions.get(i)-average));
        }
        double distancetotal = 0;
        for (int i = 0; i < distances.size(); i++) {
            distancetotal+=distances.get(i);
        }

        double enemyDensity = distancetotal/EnemyNum;
        return enemyDensity;
    }

    public static void main(String[] args) throws IOException {
        String testLevel = "generatedExamples/geminiLevelGenerator/zelda/zelda_lvl143.txt";
        System.out.println("Enemy Density is " + String.valueOf(calculateMetric(testLevel)));
    }
}
