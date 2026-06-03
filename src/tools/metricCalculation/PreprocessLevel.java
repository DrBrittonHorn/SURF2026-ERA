package tools.metricCalculation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PreprocessLevel {
    
    

// Converts levels into a form that is compatible with spatial metrics
    public static String applySpatialMapping(String level){
        try {
            String aliensMapping = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/aliens/aliens_lvl001.txt")).split("LevelDescription")[0];
            String artilleryMapping = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/artillery/artillery_lvl001.txt")).split("LevelDescription")[0];
            String asteroidsMapping = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/asteroids/asteroids_lvl001.txt")).split("LevelDescription")[0];
            String dungeonMapping = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/dungeon/dungeon_lvl001.txt")).split("LevelDescription")[0];
            String frogsMapping = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/frogs/frogs_lvl001.txt")).split("LevelDescription")[0];
            String marioMapping = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/mario/mario_lvl001.txt")).split("LevelDescription")[0];
            String realsokobanMapping = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/realsokoban/realsokoban_lvl001.txt")).split("LevelDescription")[0];
            String roguelikeMapping = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/roguelike/roguelike_lvl001.txt")).split("LevelDescription")[0];
            String towerdefenseMapping = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/towerdefense/towerdefense_lvl001.txt")).split("LevelDescription")[0];
            String zeldaMapping = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/zelda/zelda_lvl001.txt")).split("LevelDescription")[0];
            
            // No work needed to maintain avatar consistency
            
            // New Standard Block = "S"
            // New goal symbol = 'G'
            // New transparent block symbol = 'T'
            // New enemy symbol = 'E'
            // New Breakable/movable Block = 'B'
            // New Obstacle = 'O'
            String[] levelParts = level.split("LevelDescription");
            if (levelParts[0].equals(aliensMapping)){
                //System.out.println("Aliens Level preprocessing");
                // Replace enemies with enemy symbol
                levelParts[1] = levelParts[1].replace("1", "E");
                levelParts[1] = levelParts[1].replace("2", "E");
                //Represent standard block
                levelParts[1] = levelParts[1].replace("w", "S"); // Standard block chosen because all blocks are breakable in this game
            }
            else if (levelParts[0].equals(artilleryMapping)){
                //System.out.println("Artillery Level preprocessing");
                // Sets breakable blocks
                levelParts[1] = levelParts[1].replace("d", "B");
                // Sets enemies
                levelParts[1] = levelParts[1].replace("G", "E"); // Replace with goal instead?
                //Represent standard block
                levelParts[1] = levelParts[1].replace("w", "S");
            }
            else if (levelParts[0].equals(asteroidsMapping)){
                //System.out.println("Asteroids Level preprocessing");
                // Replace enemies
                levelParts[1] = levelParts[1].replace("G", "E"); // Replace with goal instead?
                // Replace breakable blocks
                levelParts[1] = levelParts[1].replace("b", "B");
                //Represent standard block
                levelParts[1] = levelParts[1].replace("w", "S");
            }
            else if (levelParts[0].equals(dungeonMapping)){
                //System.out.println("Dungeon Level preprocessing");
                // Represent cannon block as an enemy
                levelParts[1] = levelParts[1].replace("1", "E"); // The cannon doesn't move, so it is not like the typical enemy...
                levelParts[1] = levelParts[1].replace("2", "E"); // The cannon doesn't move, so it is not like the typical enemy...
                //Represent the beam as an enemy
                levelParts[1] = levelParts[1].replace("u", "E"); // The beam doesn't move, so it is not like the typical enemy...
                levelParts[1] = levelParts[1].replace("d", "E"); // The beam doesn't move, so it is not like the typical enemy...
                levelParts[1] = levelParts[1].replace("l", "E"); // The beam doesn't move, so it is not like the typical enemy...
                levelParts[1] = levelParts[1].replace("r", "E"); // The beam doesn't move, so it is not like the typical enemy...
                // Represent the fire as an enemy
                levelParts[1] = levelParts[1].replace("f", "E"); // Fire doesn't move, so it is not like the typical enemy...
                //Replace coins
                levelParts[1] = levelParts[1].replace("g", "T");
                // Replace goal
                levelParts[1] = levelParts[1].replace("x", "G");
                // Represent standard block
                levelParts[1] = levelParts[1].replace("w", "S");
                //Represent lock then key as breakable/movable
                levelParts[1] = levelParts[1].replace("m", "B");
                levelParts[1] = levelParts[1].replace("k", "B");

                // Represent trapdoor as obstacle
                levelParts[1] = levelParts[1].replace("t", "o");

            }
            else if (levelParts[0].equals(frogsMapping)){
                //System.out.println("Frogs Level preprocessing");
                // Replace Goal
                levelParts[1] = levelParts[1].replace("g", "G");
                
                //Represent logs
                levelParts[1] = levelParts[1].replace("1", "T"); //Replace log generators
                levelParts[1] = levelParts[1].replace("2", "T"); //Replace log generators
                levelParts[1] = levelParts[1].replace("3", "T"); //Replace log generators
                levelParts[1] = levelParts[1].replace("4", "T"); //Replace log generators
                levelParts[1] = levelParts[1].replace("=", "T");
                //Represent water gaps as obstacles
                levelParts[1] = levelParts[1].replace("0", "O");

                //Represent enemies
                levelParts[1] = levelParts[1].replace("l", "E");
                levelParts[1] = levelParts[1].replace("-", "E");
                levelParts[1] = levelParts[1].replace("x", "E");
                levelParts[1] = levelParts[1].replace("_", "E");

                // Represent walls as standard block
                levelParts[1] = levelParts[1].replace("w", "S");                
            }
            else if (levelParts[0].equals(marioMapping)){
                //System.out.println("Mario Level preprocessing");
                // Represent enemies
                levelParts[1] = levelParts[1].replace("1", "E");
                levelParts[1] = levelParts[1].replace("2", "E");
                //Replace coins
                levelParts[1] = levelParts[1].replace("c", "T");
                // G for goal already set
                // Set elevator to breakable/(movable)
                levelParts[1] = levelParts[1].replace("=", "B");
                // Set fire to obstacle
                levelParts[1] = levelParts[1].replace("f", "O");
                // Set cloud to breakable/(movable)
                levelParts[1] = levelParts[1].replace("l", "B");
                // Represent walls as standard block
                levelParts[1] = levelParts[1].replace("w", "S");
            }
            else if (levelParts[0].equals(realsokobanMapping)){ // This one is super questionable
                //System.out.println("RealSokoban Level preprocessing");
                // Represent boxes as movable
                levelParts[1] = levelParts[1].replace("*", "B");
                // Represent goal spaces as transparent
                levelParts[1] = levelParts[1].replace("o", "T");
                // Represent walls as standard block
                levelParts[1] = levelParts[1].replace("w", "S");
            }
            else if (levelParts[0].equals(roguelikeMapping)){
                //System.out.println("Roguelike Level preprocessing");
                //Replace "exit floor" to goal symbol
                levelParts[1] = levelParts[1].replace("g", "T");
                // Replace heart objects with transparent object symbol; Revisit?
                levelParts[1] = levelParts[1].replace("h", "T");
                //Replace gold coins with transparent object symbol
                levelParts[1] = levelParts[1].replace("x", "G");
                //Replace enemies with enemy symbol
                levelParts[1] = levelParts[1].replace("p", "E");
                levelParts[1] = levelParts[1].replace("r", "E");
                // Represent walls as standard block
                levelParts[1] = levelParts[1].replace("w", "S");

                // Represent key and lock as breakable/movable
                levelParts[1] = levelParts[1].replace("k", "B");
                levelParts[1] = levelParts[1].replace("l", "B");

                // Represent market as standard block
                levelParts[1] = levelParts[1].replace("m", "S");
            }
            else if (levelParts[0].equals(towerdefenseMapping)){
                //System.out.println("Towerdefense Level preprocessing");
                // Replace turrets wiht movable because they can be interacted with
                levelParts[1] = levelParts[1].replace("h", "B");
                levelParts[1] = levelParts[1].replace("v", "B");
                
                //Replace enemies
                levelParts[1] = levelParts[1].replace("1", "E");
                levelParts[1] = levelParts[1].replace("2", "E");
                levelParts[1] = levelParts[1].replace("3", "E");
                levelParts[1] = levelParts[1].replace("4", "E");

                //Replace Paths
                levelParts[1] = levelParts[1].replace("d", "T");
                levelParts[1] = levelParts[1].replace("l", "T");
                levelParts[1] = levelParts[1].replace("p", "T");
                levelParts[1] = levelParts[1].replace("r", "T");
                levelParts[1] = levelParts[1].replace("u", "T");

                //Represent tower as standard block (questionable...)
                levelParts[1] = levelParts[1].replace("t", "S");
                // Represent walls as standard block
                levelParts[1] = levelParts[1].replace("w", "S");
            }
            else if (levelParts[0].equals(zeldaMapping)){
                //System.out.println("Zelda Level preprocessing");
                //Replace enemies
                levelParts[1] = levelParts[1].replace("1", "E");
                levelParts[1] = levelParts[1].replace("2", "E");
                levelParts[1] = levelParts[1].replace("3", "E");

                // Represent key as breakable/movable
                levelParts[1] = levelParts[1].replace("+", "B");
                // Represent goal
                levelParts[1] = levelParts[1].replace("g", "G");

                // Represent walls as standard block
                levelParts[1] = levelParts[1].replace("w", "S");
            }
            else{
                System.out.println("No spatial mapping found");
            }
            // If the level contains its original description mapping as well as the level itself
            if (levelParts.length == 2){
                return "Original " + levelParts[0] + "LevelDescription"+ levelParts[1];

            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String args[]) throws IOException{
        String testLevel = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/zelda/zelda_lvl001.txt"));
        System.out.println(applySpatialMapping(testLevel));

    }
}
