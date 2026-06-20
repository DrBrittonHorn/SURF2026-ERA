package tools.metricCalculation.metrics.byLevelMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import tools.metricCalculation.metricTools;

public class Symmetry {
    
    public static double calculateMetric(String levelText) {
        ArrayList<ArrayList<Character>> levelArray = metricTools.toArray(levelText);
        int maxLength = 0;
        Character padding = '~';
        for (int i = 0; i < levelArray.size(); i++){
            if (levelArray.get(i).size() > maxLength){
                maxLength = levelArray.get(i).size();
            }
        }

        for (ArrayList<Character> row : levelArray){
            while (row.size() < maxLength){
                row.add(padding);
            }
        }
        //System.out.println(levelArray);

        //System.out.println("Vsym" + vSymmetry(levelArray));
        //System.out.println("Hsym" + vSymmetry(transposeArrayList(levelArray)));
        return .5 * vSymmetry(levelArray) + .5 * vSymmetry(transposeArrayList(levelArray));
    }

    
    public static double vSymmetry(ArrayList<ArrayList<Character>> level){
        double vSymmetry = 0;
        double totalArea = 0;    
        for (int i = 0; i < level.size(); i++){
                for (int j = 0; j < level.get(i).size(); j++){
                    totalArea++;
                    if (level.get(i).get(j).equals(level.get(level.size()-i-1).get(j)) && level.get(i).get(j) != '~'){
                        vSymmetry++;
                    }
                    else{
                        //System.out.println("Vertical I: " + i + " J: " + j);
                        //System.out.println(levelArray.get(i).get(j) + " " + levelArray.get(levelArray.size()-i-1).get(j));
                    }
                }
            }
            return vSymmetry/totalArea;
        }

    // Source - https://stackoverflow.com/a/79546554
    // Posted by Kuldeep Yadav
    // Retrieved 2026-06-17, License - CC BY-SA 4.0

    public static ArrayList<ArrayList<Character>> transposeArrayList(ArrayList<ArrayList<Character>> A) {
        ArrayList<ArrayList<Character>> transposeList = new ArrayList<ArrayList<Character>>();

        //Initialize Array List
        for (int i=0; i<A.get(0).size();i++){
            transposeList.add(new ArrayList<Character>());
        }

        //Fill Transpose Array List
        for (int i=0; i<A.size(); i++){
            for (int j=0; j<A.get(0).size(); j++){
                transposeList.get(j).add(A.get(i).get(j));
            }
        }

        return transposeList;
    }



    public static void main(String[] args) throws IOException {
        String testLevel = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/realsokoban/realsokoban_lvl001.txt"));
        System.out.print("Symmetry is " + String.valueOf(calculateMetric(testLevel)));
    }
}