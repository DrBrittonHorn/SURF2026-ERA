package tools.metricCalculation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import tracks.ArcadeMachine;

public class metricTools {
    
    

// Converts levels into a form that is compatible with spatial metrics
    public static String applySpatialMapping(String level){
        try {
            String aliensMapping = Files.readString(Path.of("generatedExamples/geminiLevelGenerator/aliens/aliens_lvl001.txt")).split("LevelDescription")[0];
            String artilleryMapping = Files.readString(Path.of("generatedExamples/geminiLevelGenerator/artillery/artillery_lvl001.txt")).split("LevelDescription")[0];
            String asteroidsMapping = Files.readString(Path.of("generatedExamples/geminiLevelGenerator/asteroids/asteroids_lvl001.txt")).split("LevelDescription")[0];
            String dungeonMapping = Files.readString(Path.of("generatedExamples/geminiLevelGenerator/dungeon/dungeon_lvl001.txt")).split("LevelDescription")[0];
            String frogsMapping = Files.readString(Path.of("generatedExamples/geminiLevelGenerator/frogs/frogs_lvl001.txt")).split("LevelDescription")[0];
            String marioMapping = Files.readString(Path.of("generatedExamples/geminiLevelGenerator/mario/mario_lvl001.txt")).split("LevelDescription")[0];
            String realsokobanMapping = Files.readString(Path.of("generatedExamples/geminiLevelGenerator/realsokoban/realsokoban_lvl001.txt")).split("LevelDescription")[0];
            String roguelikeMapping = Files.readString(Path.of("generatedExamples/geminiLevelGenerator/roguelike/roguelike_lvl001.txt")).split("LevelDescription")[0];
            String towerdefenseMapping = Files.readString(Path.of("generatedExamples/geminiLevelGenerator/towerdefense/towerdefense_lvl001.txt")).split("LevelDescription")[0];
            String zeldaMapping = Files.readString(Path.of("generatedExamples/geminiLevelGenerator/zelda/zelda_lvl001.txt")).split("LevelDescription")[0];
            
            // Fix for changes to level description creation changes in generator code
            String zeldaMapping2 = "LevelMapping\r\n" + //
                                "    A > floor nokey \r\n" + //
                                "    1 > floor monsterQuick \r\n" + //
                                "    2 > floor monsterNormal \r\n" + //
                                "    3 > monsterSlow floor \r\n" + //
                                "    w > wall \r\n" + //
                                "    g > floor goal \r\n" + //
                                "    + > floor key \r\n" + //
                                "    . > floor";


            // No work needed to maintain avatar consistency
            
            // New Standard Block = "S"
            // New goal symbol = 'G'
            // New transparent block symbol = 'T'
            // New enemy symbol = 'E'
            // New Breakable/movable Block = 'B'
            // New Obstacle = 'O'
            String[] levelParts = level.split("LevelDescription");
            if (levelParts[0].equals(aliensMapping)){
                levelParts[1] = levelParts[1].replace(" ", "."); // Fixes new issue of level being filled with blank spaces
                //System.out.println("Aliens Level preprocessing");
                // Replace enemies with enemy symbol
                levelParts[1] = levelParts[1].replace("1", "E");
                levelParts[1] = levelParts[1].replace("2", "E");
                //Represent standard block
                levelParts[1] = levelParts[1].replace("w", "S"); // Standard block chosen because all blocks are breakable in this game
            }
            else if (levelParts[0].equals(artilleryMapping)){
                levelParts[1] = levelParts[1].replace(" ", "."); // Fixes new issue of level being filled with blank spaces
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
                levelParts[1] = levelParts[1].replace(" ", "."); // Fixes new issue of level being filled with blank spaces
                levelParts[1] = levelParts[1].replace("G", "E"); // Replace with goal instead?
                // Replace breakable blocks
                levelParts[1] = levelParts[1].replace("b", "B");
                //Represent standard block
                levelParts[1] = levelParts[1].replace("w", "S");
            }
            else if (levelParts[0].equals(dungeonMapping)){
                //System.out.println("Dungeon Level preprocessing");
                levelParts[1] = levelParts[1].replace(" ", "."); // Fixes new issue of level being filled with blank spaces
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
                levelParts[1] = levelParts[1].replace(" ", "."); // Fixes new issue of level being filled with blank spaces
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
                
                //Represent grass as empty block
                levelParts[1] = levelParts[1].replace("+", ".");
            }
            else if (levelParts[0].equals(marioMapping)){
                levelParts[1] = levelParts[1].replace(" ", "."); // Fixes new issue of level being filled with blank spaces
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
                levelParts[1] = levelParts[1].replace(" ", "."); // Fixes new issue of level being filled with blank spaces
                //System.out.println("RealSokoban Level preprocessing");
                // Represent boxes as movable
                levelParts[1] = levelParts[1].replace("*", "B");
                // Represent goal spaces as transparent
                levelParts[1] = levelParts[1].replace("o", "T");
                // Represent walls as standard block
                levelParts[1] = levelParts[1].replace("w", "S");
            }
            else if (levelParts[0].equals(roguelikeMapping)){
                levelParts[1] = levelParts[1].replace(" ", "."); // Fixes new issue of level being filled with blank spaces
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
                levelParts[1] = levelParts[1].replace(" ", "."); // Fixes new issue of level being filled with blank spaces

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
            else if (levelParts[0].equals(zeldaMapping) || levelParts[0].equals(zeldaMapping2)){
                levelParts[1] = levelParts[1].replace(" ", "."); // Fixes new issue of level being filled with blank spaces
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
                String out = "No spatial mapping found for " + level;
                System.out.println(out);
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

    public static String getGameFilePath(String levelText){
        String levelDesc = levelText.split("LevelDescription")[0];
        // Levels with an empty description will cause an error because a gamePath that does not exist will be created!
        try{
            String aliensMapping = Files.readString(Path.of("generatedExamples/geminiLevelGenerator/aliens/aliens_lvl001.txt")).split("LevelDescription")[0];
            String artilleryMapping = Files.readString(Path.of("generatedExamples/geminiLevelGenerator/artillery/artillery_lvl001.txt")).split("LevelDescription")[0];
            String asteroidsMapping = Files.readString(Path.of("generatedExamples/geminiLevelGenerator/asteroids/asteroids_lvl001.txt")).split("LevelDescription")[0];
            String dungeonMapping = Files.readString(Path.of("generatedExamples/geminiLevelGenerator/dungeon/dungeon_lvl001.txt")).split("LevelDescription")[0];
            String frogsMapping = Files.readString(Path.of("generatedExamples/geminiLevelGenerator/frogs/frogs_lvl001.txt")).split("LevelDescription")[0];
            String marioMapping = Files.readString(Path.of("generatedExamples/geminiLevelGenerator/mario/mario_lvl001.txt")).split("LevelDescription")[0];
            String realsokobanMapping = Files.readString(Path.of("generatedExamples/geminiLevelGenerator/realsokoban/realsokoban_lvl001.txt")).split("LevelDescription")[0];
            String roguelikeMapping = Files.readString(Path.of("generatedExamples/geminiLevelGenerator/roguelike/roguelike_lvl001.txt")).split("LevelDescription")[0];
            String towerdefenseMapping = Files.readString(Path.of("generatedExamples/geminiLevelGenerator/towerdefense/towerdefense_lvl001.txt")).split("LevelDescription")[0];
            String zeldaMapping = Files.readString(Path.of("generatedExamples/geminiLevelGenerator/zelda/zelda_lvl001.txt")).split("LevelDescription")[0];

            // Fix for changes to level description creation changes in generator code
            String zeldaMapping2 = "LevelMapping\r\n" + //
                                "    A > floor nokey \r\n" + //
                                "    1 > floor monsterQuick \r\n" + //
                                "    2 > floor monsterNormal \r\n" + //
                                "    3 > monsterSlow floor \r\n" + //
                                "    w > wall \r\n" + //
                                "    g > floor goal \r\n" + //
                                "    + > floor key \r\n" + //
                                "    . > floor";

            if (levelDesc.equals(aliensMapping)){
                return "examples/gridphysics/aliens.txt";
            }
            else if (levelDesc.equals(artilleryMapping)){
                return "examples/contphysics/artillery.txt";
            }
            else if (levelDesc.equals(asteroidsMapping)){
                return "examples/contphysics/asteroids.txt";
            }
            else if (levelDesc.equals(dungeonMapping)){
                return "examples/gridphysics/dungeon.txt";
            }
            else if (levelDesc.equals(frogsMapping)){
                return "examples/gridphysics/frogs.txt";
            }
            else if (levelDesc.equals(marioMapping)){
                return "examples/contphysics/mario.txt";
            }
            else if (levelDesc.equals(realsokobanMapping)){
                return "examples/gridphysics/realsokoban.txt";
            }
            else if (levelDesc.equals(roguelikeMapping)){
                return "examples/gridphysics/roguelike.txt";
            }
            else if (levelDesc.equals(towerdefenseMapping)){
                return "examples/gridphysics/towerdefense.txt";
            }
            else if (levelDesc.equals(zeldaMapping) || levelDesc.equals(zeldaMapping2)){
                return "examples/gridphysics/zelda.txt";
            }
            else{
                throw new NoSuchElementException("Game desc not found for " + levelDesc);
            }

        }
        catch (IOException e1){
            e1.printStackTrace();
            return "";
        }
        
    }

    public static ArrayList<ArrayList<Character>> toArray(String levelString) {
        String levelMap;
        if (levelString.contains("LevelDescription")){
            levelMap = levelString.split("LevelDescription")[1].trim();
        } 
        else{
            levelMap = levelString.trim();
        }

        ArrayList<ArrayList<Character>> list = new ArrayList<>();
        for (String row : levelMap.split("\\r?\\n")) {
            ArrayList<Character> rowList = new ArrayList<>();
            for (char c : row.toCharArray()) {
                rowList.add(c);
            }
            list.add(rowList);
        }
        return list;
    }

    public static double similarityScore(ArrayList<ArrayList<Character>> level1, ArrayList<ArrayList<Character>> level2){
        int dissimilar = 0;
        int level1Size = 0;
        int level2Size = 0;
        // Calculate dissimilarity and level overlaps
        for (int row = 0; row < Math.min(level1.size(), level2.size()); row++){
            // Count uneven row lengths to dissimilarity
            dissimilar += Math.abs(level1.get(row).size() - level2.get(row).size());
            for (int col = 0; col < Math.min(level1.get(row).size(), level2.get(row).size()); col++){
                // Account for dissimilar blocks
                if (level1.get(row).get(col) != level2.get(row).get(col)){
                    dissimilar += 1;
                }
            }
        }
        
        for (int row = Math.min(level1.size(), level2.size()); row < level1.size(); row++) {
            dissimilar += level1.get(row).size();
        }

        for (int row = Math.min(level1.size(), level2.size()); row < level2.size(); row++) {
            dissimilar += level2.get(row).size();
        }
        
        // Calculate size of both levels
        for (ArrayList<Character> a : level1){
            level1Size += a.size();
        }
        for (ArrayList<Character> a : level2){
            level2Size += a.size();
        }
        int maxLevelSize = Math.max(level1Size, level2Size);
        // Similarity = (BiggestLevel - differences) / BiggestLevel
        return (double) ((maxLevelSize)-dissimilar)/(maxLevelSize);
    }

    /**
     * Turns a given level discrption into an
     * easily accessible [y][x] graph
     * @param levelText the string level text
     * @return a char[][] 'graph'
     */
    public static char[][] toMap(String levelText) {
        String[] rows = levelText.strip().split("\n");
        char[][] map = new char[rows.length][rows[0].length()]; // (y, x)
        
        for (int y = 0; y < rows.length; y++) {
            for (int x = 0; x<rows[y].length(); x++) {
                map[y][x] = rows[y].charAt(x);
            }
        }
        return map;
    }
    // Strips away a level's tile mapping and other extraneous attributes, leaving only the level's tiles. Additionally fixes any whitespace errors
    public static String getLevelTiles(String rawLevel){
        if (rawLevel.split("LevelDescription").length > 1){
            return rawLevel.split("LevelDescription")[1].replace(" ", ".").trim();
        }
        return rawLevel.replace(" ", ".");
    }


    /**
     * Plays a generated level and creates a playtrace for it in generatedExamplesPlaytraces
     * The level is evaluated and a new playtrace created when one does not already exist
     * @param levelPath The string path to a level in generated examples
     */
    public static void createPlaytrace(String levelPath) throws IOException{
        levelPath = levelPath.replace("\\\\", "/");
        String sampleRandomController = "tracks.singlePlayer.simple.sampleRandom.Agent";
		String doNothingController = "tracks.singlePlayer.simple.doNothing.Agent";
		String sampleOneStepController = "tracks.singlePlayer.simple.sampleonesteplookahead.Agent";
		String sampleFlatMCTSController = "tracks.singlePlayer.simple.greedyTreeSearch.Agent";

		String sampleMCTSController = "tracks.singlePlayer.advanced.sampleMCTS.Agent";
        String sampleRSController = "tracks.singlePlayer.advanced.sampleRS.Agent";
        String sampleRHEAController = "tracks.singlePlayer.advanced.sampleRHEA.Agent";
		String sampleOLETSController = "tracks.singlePlayer.advanced.olets.Agent";

        String selectedAgent = sampleOLETSController;
        
        String recordActionsFile = levelPath.replace("generatedExamples", "generatedExamplesPlaytraces");
        // If the level playtrace already exists, don't make a new one
        if (Files.isRegularFile(Path.of(recordActionsFile))){
            return;
        }
        // Create parent directories
        String recordActionsFolder = "";
        String[] rAF = recordActionsFile.split("/");
        for (int i = 0; i < rAF.length-1; i++){recordActionsFolder += rAF[i] + "/";}
        Files.createDirectories(Path.of(recordActionsFolder));

        String gameName = levelPath.split("/|\\\\")[2];

        String levelNoTileMapping = Files.readString(Path.of(levelPath));
        if (levelNoTileMapping.split("LevelDescription").length > 1){
            levelNoTileMapping = levelNoTileMapping.split("LevelDescription")[1].trim();
        }
        String tempLevelPath = "src/tools/metricCalculation/tempFiles/tempLevelMap.txt";
        Files.writeString(Path.of(tempLevelPath), levelNoTileMapping);

        ArcadeMachine.runOneGame("examples/selectedGameFiles/" + gameName + ".txt", tempLevelPath, false, selectedAgent, recordActionsFile, 0, 0);
        //String[] levelOutcome = Files.readString(Path.of(recordActionsFile)).split("\n");
        //String[] levelActions = new String[levelOutcome.length-1];
        //for (int i = 1; i < levelOutcome.length; i++){
            //levelActions[i-1] = levelOutcome[i];
        //}
        //allLevelActions.add(levelActions);
        //System.out.println(allLevelActions.get(0).length);
    }


    public static void main(String args[]) throws IOException{
        //String testLevel = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/zelda/zelda_lvl001.txt"));
        //String testLevel1 = Files.readString(Path.of("generatedExamples\\geminiLevelGenerator\\aliens\\aliens_lvl001.txt"));
        //String testLevel2 = Files.readString(Path.of("generatedExamples\\geminiLevelGenerator\\aliens\\aliens_lvl002.txt"));
        //System.out.println(applySpatialMapping(testLevel));
        //System.out.println(toArray(testLevel));
        //ArrayList<ArrayList<Character>> testArray1 = toArray(testLevel1);
        //ArrayList<ArrayList<Character>> testArray2 = toArray(testLevel2);
        //System.out.println(similarityScore(testArray1, testArray2));
        //createPlaytrace("generatedExamples/geminiLevelGenerator/aliens/aliens_lvl001.txt");
        createPlaytrace("generatedExamples/constructiveLevelGenerator/realsokoban/realsokoban_lvl001.txt");
        createPlaytrace("generatedExamples\\geminiLevelGenerator\\asteroids\\asteroids_lvl002.txt");
    }
}
