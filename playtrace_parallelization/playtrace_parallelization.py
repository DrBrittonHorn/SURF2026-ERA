import subprocess
import glob
import os
from multiprocessing import Pool

# Runs createPlaytraces for one level (level path String is used as input)
def create_playtrace(level_path : str):
    subprocess.run(["java", "-cp", "playtrace_parallelization\\playtraceClasses", "tools.metricCalculation.createPlaytraces", level_path], 
                cwd=r".")


if __name__ == '__main__':

    print('Starting subprocesses')
    # Compiles and our latest createPlaytraces file for use here.
    subprocess.run(["javac", "-d", "playtrace_parallelization/playtraceClasses", "-sourcepath", "src", r"src/tools/metricCalculation/createPlaytraces.java"], 
                   check=True, cwd=r".")
    
    # Get paths of levels that need playtraces
    selected_generators = ["constructiveLevelGenerator",
                           "claudeLevelGenerator",
                           "fineTunedLLMGenerator",
                           "geminiLevelGenerator",
                           "geneticLevelGenerator",
                           "localLanguageModel",
                           "randomLevelGenerator"]
    
    for generator in selected_generators:
        files = glob.glob(f'./generatedExamples/{generator}/*/*')
        # filter for level files
        
        # Create list of levels that need playtraces
        to_compute = []
        print(files)
        for file in files:
            if (not os.path.exists(file.replace("generatedExamples", "generatedExamplesPlaytraces")) and file.endswith(".txt") and not os.path.isdir(file)):
                to_compute.append(file)


        with Pool(2) as p:
            (p.map(create_playtrace, to_compute))



