package tools.metricCalculation.metrics.byLevelMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;

import tools.metricCalculation.metricTools;

// This metric uses spatial preprocessing
public class FloodReachability {
    public static double calculateMetric(String levelPath) throws IOException{
        String levelText = Files.readString(Path.of(levelPath));
        levelText = metricTools.applySpatialMapping(levelText, levelPath.split("\\\\|/")[2]);;
        ArrayList<ArrayList<Character>> levelMatrix = metricTools.toArray(levelText);

        //Based on the assumption that all generators use this character to represent blank space
        HashSet<Character> traversable = new HashSet<Character>(); // Based on the tile conversion for spatial preprocessing
        traversable.add('.'); // Empty Blocks
        traversable.add('G'); // Goal
        traversable.add('T'); // "Transparent Block"
        traversable.add('B'); // Breakable and movable Blocks
        //traversable.add('E'); // Enemies // Commented out because some enemies never move
        char emptyChar = '.';

        String map = levelText;
        double totalArea = 0.0;
        HashSet<AbstractMap.SimpleEntry<Integer, Integer>> seen = new HashSet<AbstractMap.SimpleEntry<Integer, Integer>>();
        AbstractMap.SimpleEntry<Integer, Integer> avatarLocation = null;
        int totalTraversableTiles = 0;

        // Find avatar location
        for (int row = 0; row < levelMatrix.size(); row++){
            totalArea += levelMatrix.get(row).size();
            for (int col = 0; col < levelMatrix.get(row).size(); col++){
                if (levelMatrix.get(row).get(col) == 'A'){
                    avatarLocation = new AbstractMap.SimpleEntry<Integer, Integer>(row, col); // Don't swap these Don't swap these Don't swap these
                }
            }
        }
        
        // Actual DFS logic
        Stack<AbstractMap.SimpleEntry<Integer, Integer>> todo = new Stack<AbstractMap.SimpleEntry<Integer, Integer>>();
        if (avatarLocation == null){return -2;} // No avatar
        else{todo.push(avatarLocation);}
        
        while (!todo.isEmpty()){
            AbstractMap.SimpleEntry<Integer, Integer> currentEntry = todo.pop();
            totalTraversableTiles++;
            
            
            //System.out.println("Just popped " + currentEntry.toString());
            //System.out.println("Seen looks like " + seen.toString());
            HashSet<AbstractMap.SimpleEntry<Integer, Integer>> currentNeighbors = getNeighbors(levelMatrix, currentEntry);
            //System.out.println("Its neighbors are " + currentNeighbors);
            for (AbstractMap.SimpleEntry<Integer, Integer> e: currentNeighbors){
                if (e.getKey() < levelMatrix.size() && e.getValue() < levelMatrix.get(e.getKey()).size()){
                    if (traversable.contains(levelMatrix.get(e.getKey()).get(e.getValue())) && (!seen.contains(new AbstractMap.SimpleEntry<Integer, Integer>(e.getKey(), e.getValue())))){
                        //System.out.println("Of those, we pushed" + e.toString());
                        seen.add(e); // Spent 30mins here after accidentally writing seen.add(currentEntry)
                        todo.push(e);
                    }
                }
                    
            }
            
        }        

        //System.out.println(totalTraversableTiles + "/" + totalArea);
        return totalTraversableTiles/totalArea;
        //return totalTraversableTiles/totalArea;

    }

    public static HashSet<AbstractMap.SimpleEntry<Integer, Integer>> getNeighbors(ArrayList<ArrayList<Character>> map, AbstractMap.SimpleEntry<Integer, Integer> coords){
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

        String testLevel1 = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/dungeon/dungeon_lvl003.txt"));
        //testLevel1 = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/aliens/aliens_lvl001.txt"));
        //testLevel1 = PreprocessLevel.applySpatialMapping(testLevel1);
        System.out.println("Flood reachability is... " + calculateMetric(testLevel1));
    }
}
