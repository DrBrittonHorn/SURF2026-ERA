
package tools.metricCalculation.metrics.byGeneratorMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;

import tracks.ArcadeMachine;

public class PlaytraceDiversity{
    public static double calculateMetric(String generatorFolderPath) throws IOException{
        
        // Available agents for metric
        String sampleRandomController = "tracks.singlePlayer.simple.sampleRandom.Agent";
		String doNothingController = "tracks.singlePlayer.simple.doNothing.Agent";
		String sampleOneStepController = "tracks.singlePlayer.simple.sampleonesteplookahead.Agent";
		String sampleFlatMCTSController = "tracks.singlePlayer.simple.greedyTreeSearch.Agent";

		String sampleMCTSController = "tracks.singlePlayer.advanced.sampleMCTS.Agent";
        String sampleRSController = "tracks.singlePlayer.advanced.sampleRS.Agent";
        String sampleRHEAController = "tracks.singlePlayer.advanced.sampleRHEA.Agent";
		String sampleOLETSController = "tracks.singlePlayer.advanced.olets.Agent";


        String selectedAgent = sampleOLETSController;
        String recordActionsFile = "src/tools/metricCalculation/tempFiles/playtraceDiversityTemp.txt";
        String gameName = generatorFolderPath.split("/")[2];
        Stream<Path> levels = Files.list(Path.of(generatorFolderPath)).filter(path -> path.toString().endsWith(".txt"));

        ArrayList<String[]> allLevelActions = new ArrayList<String[]>();

        levels.forEach(path -> {
            // Create a level without the tilemapping heading to pass into ArcadeMachine.runOneGame()
            try {
                String levelNoTileMapping = Files.readString(path);
                if (levelNoTileMapping.split("LevelDescription").length > 1){
                    levelNoTileMapping = levelNoTileMapping.split("LevelDescription")[1].trim();
                }
                String tempLevelPath = "src/tools/metricCalculation/tempFiles/tempLevelMap.txt";
                Files.writeString(Path.of(tempLevelPath), levelNoTileMapping);

                ArcadeMachine.runOneGame("examples/selectedGameFiles/" + gameName + ".txt", tempLevelPath, true, selectedAgent, recordActionsFile, 0, 0);
                // TODO fix trailing newline
                String[] levelOutcome = Files.readString(Path.of(recordActionsFile)).split("\n");
                String[] levelActions = new String[levelOutcome.length-1];
                for (int i = 1; i < levelOutcome.length; i++){
                    levelActions[i-1] = levelOutcome[i];
                }
                allLevelActions.add(levelActions);
                System.out.println(allLevelActions.get(0).length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        

        });
        return 0;
    }

    public static void main(String[] args) throws IOException{

        String testFolder = "generatedExamples/geminiLevelGenerator/realsokoban";
        System.out.println(calculateMetric(testFolder));

    }
}