import subprocess
import glob
import os
from multiprocessing import Pool

# Determines where playtraces are stored
playtrace_collection_name = "generatedExamplesPlaytracesLab-1k-80ms"

# Runs createPlaytraces for one level (level path String is used as input)
def create_playtrace(level_path : str):
    subprocess.run(["java", "-cp", "playtrace_parallelization/playtraceClasses", "tools.metricCalculation.createPlaytraces", level_path, playtrace_collection_name], 
                cwd=r".")


if __name__ == '__main__':

    print('Starting subprocesses')
    # Compiles and our latest createPlaytraces file for use here.
    subprocess.run(["javac", "-d", "playtrace_parallelization/playtraceClasses", "-sourcepath", "src", r"src/tools/metricCalculation/createPlaytraces.java"], 
                   check=True, cwd=r".")
    
    # Get paths of levels that need playtraces
    selected_generators = [#"constructiveLevelGenerator",
                            #"claudeLevelGenerator",
                            #"fineTunedLLMGenerator",
                            #"geminiLevelGenerator",
                            #"geneticLevelGenerator",
                            #"localLanguageModel",
                            #"randomLevelGenerator",
                            #"sturgeonLevelGenerator1x1",
                            "sturgeonLevelGenerator2x2",
                            "sturgeonLevelGenerator3x3",
                            "sturgeonLevelGenerator4x4",
                           ]


    for generator in selected_generators:
        files = glob.glob(f'./generatedExamples/{generator}/*/*')
        # filter for level files
        # Create list of levels that need playtraces
        to_compute = []
        # print(files)
        for file in files:
            if (not os.path.exists(file.replace("generatedExamples", playtrace_collection_name)) and file.endswith(".txt") and not os.path.isdir(file)):
                to_compute.append(file[2:])  # remove the leading './' from the path
        # print(len(files))
        # print(len(to_compute))

        for file in files:
            if file not in to_compute:
                # print(f"Skipping {file} because playtrace already exists or is not a level file.")
                pass
        

        with Pool(12) as p:
            (p.map(create_playtrace, to_compute))

