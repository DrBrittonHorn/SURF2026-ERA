package tracks.levelGeneration;

import java.nio.file.Files;
import java.nio.file.Path;

// Include these so that the compiled version of this program can find the agents (and constraints) it needs
import tracks.levelGeneration.geneticLevelGenerator.LevelGenerator;
import tracks.levelGeneration.constraints.GoalConstraint;
import tracks.levelGeneration.constraints.GameEndConstraint;
import tracks.levelGeneration.constraints.WinConstraint;
import tracks.levelGeneration.constraints.AvatarNumberConstraint;
import tracks.levelGeneration.constraints.SpriteNumberConstraint;
import tracks.levelGeneration.constraints.DeathConstraint;
import tracks.levelGeneration.constraints.SolutionLengthConstraint;
import tracks.singlePlayer.simple.sampleonesteplookahead.Agent;

public class generateGeneticLevels {
    public static void main(String[] args) throws Exception{
        // provide output level file
        //String outputLocation = "generatedExamples/geneticParallel/aliens/aliens_lvl001.txt";
        String outputLocation = args[0];
        String gameName = outputLocation.split("/")[2];
        String gameFile = "examples/selectedGameFiles/" + gameName + ".txt";
        
        //Create directory for our new level if it doesn't exist
        String outputDirectory = outputLocation.replace(outputLocation.split("/")[3], "");
        Files.createDirectories(Path.of(outputDirectory));

        // Takes in a file path that needs to have a genetic level written to it and write the level to it
        String selectedGenerator = "tracks.levelGeneration.geneticLevelGenerator.LevelGenerator";
        LevelGenMachine.generateOneLevel(gameFile, selectedGenerator, outputLocation);
        
    }
}
