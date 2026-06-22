package tools.metricCalculation.metrics.byLevelMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import tools.metricCalculation.metricTools;

public class StructuralSimilarityToCorpus {
    // This is a horrible metric when levels may differ in size (all of GVGAI)
    public static double calculateMetric(String levelText){
        String gameFile = metricTools.getGameFilePath(levelText);
        double accumulatedSimilarity = 0;
        ArrayList<Path> examplePaths = new ArrayList<Path>();
        ArrayList<ArrayList<Character>> levelArray = metricTools.toArray(levelText);
        // Based on the assumption that each game has 5 samples levels
        int totalSampleLevels = 5;
        for (int i = 0; i < totalSampleLevels; i++){
            examplePaths.add(Path.of(gameFile.split(".txt")[0] + "_lvl" + i + ".txt"));
        }
        for (Path p : examplePaths){ // If we have manually changed level mappings, then comparisons between generated and existing levels will not be accurate
            try {
                accumulatedSimilarity += structuralSimilarity(metricTools.toArray(levelText), metricTools.toArray(Files.readString(p)));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        return accumulatedSimilarity / totalSampleLevels;
    }


    public static void main(String[] args) throws IOException{
        String testLevel1 = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/aliens/aliens_lvl001.txt"));
        String testLevel2 = Files.readString(Path.of("generatedExamples/geminiLevelGenerator/aliens/aliens_lvl002.txt"));
        System.out.println(calculateMetric(testLevel2));
    }

    public static double structuralSimilarity(ArrayList<ArrayList<Character>> list1, ArrayList<ArrayList<Character>> list2){
        int longestRow = 0;
        int longestCol = Math.max(list1.size(), list2.size());

        // Create padding
        // Find longest row
        for (int i = 0; i < list1.size(); i++){
            if (list1.get(i).size() > longestRow){
                longestRow = list1.get(i).size();
            }
        }
        for (int i = 0; i < list2.size(); i++){
            if (list2.get(i).size() > longestRow){
                longestRow = list2.get(i).size();
            }
        }
        // Add padding to correct row size
        for (ArrayList<Character> list : list1){
            if (list.size() < longestRow){
                int paddingNeeded = longestRow-list.size();
                for (int j = 0; j < paddingNeeded; j++){
                    list.add('~');
                }
            }
        }
        for (ArrayList<Character> list : list2){
            if (list.size() < longestRow){
                int paddingNeeded = longestRow-list.size();
                for (int j = 0; j < paddingNeeded; j++){
                    list.add('~');
                }
            }
        }
        // Add padding to make column size even
        if (list1.size() < list2.size()){
            int paddingNeeded = list2.size()-list1.size();
            for (int i = 0; i < paddingNeeded; i++){
                ArrayList<Character> paddingList = new ArrayList<Character>();
                for (int j = 0; j < longestRow; j++){
                    paddingList.add('~');
                }
                list1.add(paddingList);
            }
        }
        else if (list2.size() < list1.size()){
            int paddingNeeded = list1.size()-list2.size();
            for (int i = 0; i < paddingNeeded; i++){
                ArrayList<Character> paddingList = new ArrayList<Character>();
                for (int j = 0; j < longestRow; j++){
                    paddingList.add('~');
                }
                list2.add(paddingList);
            }
        }

        //for (ArrayList<Character> l : list1){System.out.println(l);}
        //System.out.println();
        //for (ArrayList<Character> l : list2){System.out.println(l);}

        // Evaluate similarity of each feature vector (row/column)
        double hSim = 0;
        double vSim = 0;

        for (int i = 0; i < longestCol; i++){
            if (list1.get(i).equals(list2.get(i))){hSim++;}
        }
        for (int i = 0; i < longestRow; i++){
            int colIsIdentical = 1;
            for (int j = 0; j < longestCol; j++){
                if (list1.get(j).get(i) != list2.get(j).get(i)){
                    colIsIdentical = 0;
                }
            }
            vSim += colIsIdentical;
        }

        hSim = hSim / longestRow;
        vSim = vSim / longestCol;

        return .5 * hSim + .5 * vSim;
    }
}
