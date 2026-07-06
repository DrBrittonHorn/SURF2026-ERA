package tools.metricCalculation.metrics.byLevelMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import tools.metricCalculation.metricTools;

public class EnemySparsity {
    
    private static ArrayList<Character> aliensEnemies = new ArrayList<>(List.of('1', '2'));
    private static ArrayList<Character> artilleryEnemies = new ArrayList<>(List.of('G'));
    private static ArrayList<Character> asteroidsEnemies = new ArrayList<>(List.of('G'));
    private static ArrayList<Character> frogsEnemies = new ArrayList<>(List.of('-', 'x', '_', '1'));
    private static ArrayList<Character> marioEnemies = new ArrayList<>(List.of('1', '2'));
    private static ArrayList<Character> towerdefenseEnemies = new ArrayList<>(List.of('1', '2', '3', '4'));
    private static ArrayList<Character> roguelikeEnemies = new ArrayList<>(List.of('p', 'r'));
    private static ArrayList<Character> zeldaEnemies = new ArrayList<>(List.of('1', '2', '3'));

    private static ArrayList<Integer> Enemyxvalues = new ArrayList<>();

    private static ArrayList<String> GameOptions = new ArrayList<>(List.of("aliens", "artillery", "asteroids", "frogs", "mario", "towerdefense", "roguelike", "zelda"));

    public static double calculateMetric(String levelPath) throws IOException {
        
        int EnemyNum = EnemyDensity.calculateEnemyCount(levelPath);
        ArrayList<Integer> positions = new ArrayList<>();
        
        String levelString = Files.readString(Path.of(levelPath));
        String gameName = levelPath.split("\\\\|/")[2];
        ArrayList<ArrayList<Character>> ARRAY = metricTools.toArray(metricTools.getLevelTiles(levelString));

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
            //System.out.println("This game does not have enemies");
            return -1;
        }

        int enemies = 0;
        int totalArea = 0;
        String charMap = null;
        String map = null;

        for (int y = 0; y < ARRAY.size(); y++) {
            for (int x = 0; x < ARRAY.get(y).size(); x++) {
                //char tile = MAP[y][x];
                char tile = ARRAY.get(y).get(x);
                if (enemytiles.contains(tile)) {
                    enemies++;
                    totalArea++;
                    Enemyxvalues.add(x);
                    //System.out.println(Enemyxvalues);
                }
                if (Character.isLetterOrDigit(tile)) { totalArea++;}
            }
        }

        positions = Enemyxvalues;

        //System.out.println("Positions" + positions);
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
        String testLevel = "generatedExamples/geminiLevelGenerator/zelda/zelda_lvl037.txt";
        System.out.println("Enemy Sparsity is " + String.valueOf(calculateMetric(testLevel)));
    }
}
