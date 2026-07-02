package tools.metricCalculation.metrics.byLevelMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import tools.metricCalculation.metricTools;

public class StaticPathLength {
    public static double calculateMetric(String levelPath) throws IOException{
        //System.out.println(levelPath);
        List<String> enemiesAreGoal = new ArrayList<>();
        enemiesAreGoal.add("aliens");
        enemiesAreGoal.add("asteroids");
        enemiesAreGoal.add("artillery");
        enemiesAreGoal.add("towerdefense");
        String gameName = levelPath.split("\\\\|/")[2];
        String levelText = metricTools.applySpatialMapping(Files.readString(Path.of(levelPath)));
        if (enemiesAreGoal.contains(gameName)){levelText = levelText.replace("E", "G");}
        // Return if no avatar detected
        if (!metricTools.getLevelTiles(Files.readString(Path.of(levelPath))).contains("A")){
            return -2;
        }

        ArrayList<ArrayList<Character>> levelMatrix = metricTools.toArray(levelText);

            double distanceSum = 0;
            ArrayList<AbstractMap.SimpleEntry<Integer, Integer>> goals = new ArrayList<AbstractMap.SimpleEntry<Integer, Integer>>();
            AbstractMap.SimpleEntry<Integer, Integer> avatar = new AbstractMap.SimpleEntry<Integer, Integer>(-1, -1);
            for (int i = 0; i < levelMatrix.size(); i++){
                for (int j = 0; j < levelMatrix.get(i).size(); j++)
                {
                    if (levelMatrix.get(i).get(j).equals('A')){
                        avatar = new AbstractMap.SimpleEntry<Integer, Integer>(i, j);
                    }
                    else if (levelMatrix.get(i).get(j).equals('G')){
                        goals.add(new AbstractMap.SimpleEntry<Integer, Integer>(i, j));
                    }
                }
            }
            if (avatar.getKey() != null && goals.size() > 0){
                for (AbstractMap.SimpleEntry<Integer, Integer> goal : goals){
                distanceSum += BFSPathLength(levelMatrix, avatar, goal);
                }
                
                return distanceSum / (double) goals.size();
            }
            else{
                return -1;
            }
            
       

    }

    public static HashSet<AbstractMap.SimpleEntry<Integer, Integer>> getNeighbors(ArrayList<ArrayList<Character>> map, AbstractMap.SimpleEntry<Integer, Integer> coords){
        // Warning: Directions incorrect but this function still works
        HashSet<AbstractMap.SimpleEntry<Integer, Integer>> neighbors = new HashSet<AbstractMap.SimpleEntry<Integer, Integer>>();
        // Left neighbor
        if (coords.getKey() > 0){neighbors.add(new AbstractMap.SimpleEntry<Integer, Integer>(coords.getKey()-1, coords.getValue()));}
        // Above neighbor
        if (coords.getValue() < map.get(coords.getKey()).size()-1){neighbors.add(new AbstractMap.SimpleEntry<Integer, Integer>(coords.getKey(), coords.getValue()+1));}
        // Right neighbor
        if (coords.getKey() < map.size()-1){neighbors.add(new AbstractMap.SimpleEntry<Integer, Integer>(coords.getKey()+1, coords.getValue()));}
        // Below neighbor
        if (coords.getValue() > 0){neighbors.add(new AbstractMap.SimpleEntry<Integer, Integer>(coords.getKey(), coords.getValue()-1));}
        return neighbors;
    }

    public static void main(String[] args) throws IOException{

        String testLevel1 = "generatedExamples/geminiLevelGenerator/zelda/zelda_lvl003.txt";
        testLevel1 = ("generatedExamples/geminiLevelGenerator/aliens/aliens_lvl000.txt");
        testLevel1 = "generatedExamples\\geminiLevelGenerator\\frogs\\frogs_lvl142.txt";
        //testLevel1 = PreprocessLevel.applySpatialMapping(testLevel1);
        System.out.println(metricTools.applySpatialMapping(testLevel1));
        System.out.println("Path length is... " + calculateMetric(testLevel1));
    }

    public static double BFSPathLength(ArrayList<ArrayList<Character>> levelMatrix, AbstractMap.SimpleEntry<Integer, Integer> start, AbstractMap.SimpleEntry<Integer, Integer> end){
        //Based on the assumption that all generators use this character to represent blank space
        HashSet<Character> traversable = new HashSet<Character>(); // Based on the tile conversion for spatial preprocessing
        traversable.add('.'); // Empty Blocks
        traversable.add('G'); // Goal
        traversable.add('T'); // "Transparent Block"
        traversable.add('B'); // Breakable and movable Blocks
        traversable.add('A');
        //traversable.add('E'); // Enemies // Commented out because some enemies never move

        HashSet<AbstractMap.SimpleEntry<Integer, Integer>> seen = new HashSet<AbstractMap.SimpleEntry<Integer, Integer>>();
        
        // Actual BFS logic
        Queue<AbstractMap.SimpleEntry<Integer, Integer>> todo = new LinkedList<AbstractMap.SimpleEntry<Integer, Integer>>();
        
        Map<AbstractMap.SimpleEntry<Integer,Integer>, Integer> entryToDistance = new HashMap<>();
        entryToDistance.put(start, 0);
        todo.offer(start);
        seen.add(start);

        while (!todo.isEmpty()) {
            AbstractMap.SimpleEntry<Integer,Integer> currentEntry = todo.remove();
            int d = entryToDistance.get(currentEntry);

            if (currentEntry.equals(end)) {
                return d;
            }

            for (AbstractMap.SimpleEntry<Integer,Integer> e : getNeighbors(levelMatrix, currentEntry)) {
                if (e.getKey() < levelMatrix.size() && e.getValue() < levelMatrix.get(e.getKey()).size()) {
                    if (traversable.contains(levelMatrix.get(e.getKey()).get(e.getValue())) && !seen.contains(e)) {
                        seen.add(e);
                        entryToDistance.put(e, d + 1);
                        todo.offer(e);
                    }
                }
            }
        }
        return -1;
    }
}