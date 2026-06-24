package tools.metricCalculation.metrics.byLevelMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import tools.metricCalculation.metricTools;

public class Difficulty {
    public static double calculateMetric(String levelPath) throws IOException{
        String level = Files.readString(Path.of(levelPath));
        String levelTiles = metricTools.getLevelTiles(level);
        String gameName = levelPath.split("/|\\\\")[2];

        HashMap<Character, Double> tileDifficulties = new HashMap<Character, Double>();

        if (gameName.equals("aliens")){
            tileDifficulties.put('1', 3.);
            tileDifficulties.put('2', 5.);
            tileDifficulties.put('0', 1.);
        }
        else if (gameName.equals("artillery")){
            tileDifficulties.put('d', .25);
            tileDifficulties.put('w',.5);
            tileDifficulties.put('G', 3.);
        }
        else if (gameName.equals("asteroids")){
            tileDifficulties.put('b', .25);
            tileDifficulties.put('G', 3.);
        }
        else if (gameName.equals("dungeon")){
            tileDifficulties.put('l', 2.);
            tileDifficulties.put('r', 2.);
            tileDifficulties.put('u', 2.);
            tileDifficulties.put('d', 2.);
            tileDifficulties.put('1', 2.);
            tileDifficulties.put('2', 2.);
            tileDifficulties.put('d', 1.);
            tileDifficulties.put('m', 1.);
            tileDifficulties.put('t', 1.);
        }
        else if (gameName.equals("frogs")){
            tileDifficulties.put('.', .25);
            tileDifficulties.put('-', 3.);
            tileDifficulties.put('_', 3.);
            tileDifficulties.put('l', 5.);
            tileDifficulties.put('x', 5.);
            tileDifficulties.put('1', .25);
            tileDifficulties.put('3', .25);
        }
        else if (gameName.equals("mario")){
            tileDifficulties.put('1', 3.);
            tileDifficulties.put('2', 5.);
            tileDifficulties.put('f', 2.);
        }
        else if (gameName.equals("realsokoban")){
            tileDifficulties.put('w', .25);
            tileDifficulties.put('*', 5.);
        }
        else if (gameName.equals("roguelike")){
            tileDifficulties.put('p', 5.);
            tileDifficulties.put('r', 3.);
        }
        else if (gameName.equals("towerdefense")){
            tileDifficulties.put('1', 5.);
            tileDifficulties.put('2', 5.);
            tileDifficulties.put('3', 5.);
            tileDifficulties.put('4', 5.);
        }
        else if (gameName.equals("zelda")){
            tileDifficulties.put('1', 5.);
            tileDifficulties.put('2', 3.);
            tileDifficulties.put('3', 2.);
        }

        double totalDifficulty = 0;
        for (int i = 0; i < levelTiles.length(); i++){
            if (tileDifficulties.containsKey(levelTiles.charAt(i))){
                totalDifficulty += tileDifficulties.get(levelTiles.charAt(i));
            }
        }
        return totalDifficulty;
    }

    public static void main(String[] args) throws IOException{
        String testLevel = "generatedExamples/constructiveLevelGenerator/zelda/zelda_lvl001.txt";
        System.out.println("Difficulty is... " + calculateMetric(testLevel));
    }
}
