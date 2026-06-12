package tools.metricCalculation.metrics.byLevelMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.List;

import tools.metricCalculation.metricTools;

public class EnemyCount {
   
    private static ArrayList<Character> aliensEnemies = new ArrayList<>(List.of('1', '2'));
    private static ArrayList<Character> artilleryEnemies = new ArrayList<>(List.of('G'));
    private static ArrayList<Character> asteroidsEnemies = new ArrayList<>(List.of('G'));
    private static ArrayList<Character> frogsEnemies = new ArrayList<>(List.of('-', 'x', '_', '1'));
    private static ArrayList<Character> marioEnemies = new ArrayList<>(List.of('1', '2'));
    private static ArrayList<Character> towerdefenseEnemies = new ArrayList<>(List.of('1', '2', '3', '4'));
    private static ArrayList<Character> roguelikeEnemies = new ArrayList<>(List.of('p', 'r'));
    private static ArrayList<Character> zeldaEnemies = new ArrayList<>(List.of('1', '2', '3'));
    
    private static ArrayList<String> GameOptions = new ArrayList<>(List.of("aliens", "artillery", "asteroids", "frogs", "mario", "towerdefense", "roguelike", "zelda"));

    static ArrayList<Integer> Enemyxvalues = new ArrayList<>();

    /**
     * calculates the Enemy Count metric of a level
     * @param LevelPath the url path of the level
     * @return the number of enemies as an int
     */
    @SuppressWarnings("unused")
    public static int calculateMetric(String LevelPath) throws IOException {
        Enemyxvalues.clear();

        // determines the level description and also retains the level path
        String levelString = Files.readString(Path.of(LevelPath));
        String[] gamePath = LevelPath.split("/"); // gamePath[0] is "generatedEzamples", [1] is the generator type, [2] is the game, and [3] is the level
        String gameName = gamePath[2];
        
        

        ArrayList<Character> enemytiles = new ArrayList<>();
        if (gameName.equals("aliens")) {
            enemytiles = aliensEnemies;
        }
        else if (gameName.equals("artillery")) {
            enemytiles = artilleryEnemies;
        }
        else if (gameName.equals("asteroids")) {
            enemytiles = asteroidsEnemies;
        }
        else if (gameName.equals("frogs")) {
            enemytiles = frogsEnemies;
        }
        else if (gameName.equals("mario")) {
            enemytiles = marioEnemies;
        }
        else if (gameName.equals("towerdefense")) {
            enemytiles = towerdefenseEnemies;
        }
        else if (gameName.equals("roguelike")) {
            enemytiles = roguelikeEnemies;
        }
        else if (gameName.equals("zelda")) {
            enemytiles = zeldaEnemies;
        }

        if (!GameOptions.contains(gameName)) {
            System.out.println("This game does not have enemies");
            return -1;
        }
        int enemies = 0;
        int totalArea = 0;
        String charMap = null;
        String map = null;

        // splits level to get the actual time map
        if (levelString.contains("LevelDescription")) {
            String[] level = levelString.split("LevelDescription");
            charMap = level[0];
            map = level[1];
        }

        // test print statements
        // System.out.println(charMap);
        //System.out.println(map);
        // System.out.println(gameName);
        // System.out.println(enemytiles);
        // for (int i = 0; i<enemytiles.size(); i ++) {
        //     System.out.println(enemytiles.get(i));
        // }

        char[][] MAP = metricTools.toMap(map);

        for (int y = 0; y < MAP.length; y++) {
            for (int x = 0; x < MAP[y].length; x++) {
                char tile = MAP[y][x];
                if (enemytiles.contains(tile)) {
                    enemies++;
                    totalArea++;
                    Enemyxvalues.add(x);
                }
                if (Character.isLetterOrDigit(tile)) { totalArea++;}
            }
        }

        //for(int i=0;i<map.length();i++){if(enemytiles.contains(map.charAt(i))){enemies++;totalArea++;}if(Character.isLetterOrDigit(map.charAt(i))){totalArea++;rowlength=1;}}

        if (totalArea > 0) {
            return enemies;
        }
        else {
            return -1; // error value
        }
    }

    public static void main(String[] args) throws IOException{
        String testLevel = "generatedExamples/geminiLevelGenerator/zelda/zelda_lvl143.txt";
        System.out.println("Enemy Count is " + String.valueOf(calculateMetric(testLevel)));
    }
}
