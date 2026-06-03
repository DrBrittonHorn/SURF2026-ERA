package tracks.metricCalculation.metrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;

import tracks.metricCalculation.PreprocessLevel;

public class FloodReachability {
    public static double calculateMetric(String levelText){
        //Based on the assumption that all generators use this character to represent blank space
        HashSet<Character> traversible = new HashSet<Character>(); // Based on the tile conversion for spatial preprocessing
        traversible.add('.'); // Empty Blocks
        traversible.add('G'); // Goal
        traversible.add('T'); // "Transparent Block"
        traversible.add('B'); // Breakable and movable Blocks
        //traversible.add('E'); // Enemies // Commented out because some enemies never move
        char emptyChar = '.';

        String map = levelText;
        String characterMapping;
        double totalArea = 0.0;
        ArrayList<ArrayList<Character>> levelMatrix = new ArrayList<ArrayList<Character>>();
        HashSet<AbstractMap.SimpleEntry<Integer, Integer>> seen = new HashSet<AbstractMap.SimpleEntry<Integer, Integer>>();
        AbstractMap.SimpleEntry<Integer, Integer> avatarLocation = null;
        int totalTraversableTiles = 0;

        // Split level and description
        if (levelText.contains("LevelDescription")){
            String[] level = levelText.split("LevelDescription");
            characterMapping = level[0];
            map = level[1];
        }

        // Transfers map to an AraryList

        //Deletes any errrant newLines
        if (map.charAt(0) == '\n'){map = map.substring(1, map.length());}

        int currentRow = levelMatrix.size()-1;

        for (int i = 0; i < map.length(); i++){
            char c = map.charAt(i);
            if (c == '\n'){
                levelMatrix.add(new ArrayList<Character>()); // Add new row to level map
                currentRow++;
            }
            else if (c == emptyChar || Character.isLetterOrDigit(c)){
                totalArea++;
                levelMatrix.get(currentRow).add(c);

                if (c == 'A'){
                    avatarLocation = new AbstractMap.SimpleEntry<Integer, Integer>(currentRow, levelMatrix.get(currentRow).size()-1);
                    //System.out.println(avatarLocation.toString());
                }
                
            }   
        }


        
        // Actual BFS logic
        Stack<AbstractMap.SimpleEntry<Integer, Integer>> todo = new Stack<AbstractMap.SimpleEntry<Integer, Integer>>();
        if (avatarLocation == null){return -1;}
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
                    if (traversible.contains(levelMatrix.get(e.getKey()).get(e.getValue())) && (!seen.contains(new AbstractMap.SimpleEntry<Integer, Integer>(e.getKey(), e.getValue())))){
                        //System.out.println("Of those, we pushed" + e.toString());
                        seen.add(e); // Spent 30mins here after accidentally writing seen.add(currentEntry)
                        todo.push(e);
                    }
                }
                    
            }
            
        }        

        //System.out.println(totalTraversableTiles + "/" + totalArea);
        return totalTraversableTiles/totalArea;


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

        String testLevel1 = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/dungeon/dungeon_lvl003.txt")); // 6 blocks should be reachable (when preprocessing is deactivated)
        //testLevel1 = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/aliens/aliens_lvl001.txt"));
        //testLevel1 = PreprocessLevel.applySpatialMapping(testLevel1);
        System.out.println(testLevel1);
        System.out.println("Flood reachability is... " + calculateMetric(testLevel1));
    }

}
