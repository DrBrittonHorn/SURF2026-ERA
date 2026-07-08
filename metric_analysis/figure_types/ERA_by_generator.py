from pathlib import Path

from matplotlib import pyplot as plt
from matplotlib.colors import LinearSegmentedColormap
import numpy as np
import sys, os
# Fixes local import behavior
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__)))))

from metric_analysis.tools import create_attribute_dict, create_color_bar, create_game_colorbars, get_generator_title, get_metric_title, parse_binning

def create_ERA_by_Generator(selected_metrics_tuple: tuple, exclude_malformed=True, ax=None):
    standalone = False
    if ax is None:
        standalone = True
        fig, ax = plt.subplots(figsize=(7, 4), layout="constrained")
    # dict = create_attribute_dict(json_path)
    
    generators = [
            "claudeLevelGenerator",
            "constructiveLevelGenerator",
            "enhancedClaudeGenerator",
            "geminiLevelGenerator",
            "geneticLevelGenerator",
            "randomLevelGenerator",
            "sturgeonLevelGenerator1x1",
            "sturgeonLevelGenerator2x2",
            "sturgeonLevelGenerator3x3",
            "sturgeonLevelGenerator4x4"
    ]

    x_lists_bygenerator = [[] for i in range(10)]
    y_lists_by_generator = [[] for i in range(10)]

    inclusive_x_list = []
    inclusive_y_list = []

    for i in range(len(generators)):
        metricsPath = "generatedExamples/" + generators[i] + "/levelMetrics.json"
        dict = create_attribute_dict(metricsPath)


        for level_path, metrics in dict.items():
            # print(level_path)
            level_game_name = level_path.split("\\")[2]
            # Create lists of the variables used in this graph
            if (metrics[selected_metrics_tuple[0]] > 0 and metrics[selected_metrics_tuple[1]] > 0): # Revisit this, a negative number may not mean error for all metrics...
                if (not exclude_malformed or (parse_binning(level_path, dict))): 
                    x_lists_bygenerator[i].append(metrics[selected_metrics_tuple[0]])
                    y_lists_by_generator[i].append(metrics[selected_metrics_tuple[1]])
                    inclusive_x_list.append(metrics[selected_metrics_tuple[0]])
                    inclusive_y_list.append(metrics[selected_metrics_tuple[1]])
    
    print(f"Creating an ERA by generator chart with {len(inclusive_x_list)} levels: ({selected_metrics_tuple[1]} to {selected_metrics_tuple[0]})")
    if (len(inclusive_x_list) == 0): print("No data points found!"); return

    padding_factor = .25
    x_padding = (max(inclusive_x_list) - min(inclusive_x_list)) * padding_factor
    y_padding = (max(inclusive_y_list) - min(inclusive_y_list)) * padding_factor

    x_min = min(inclusive_x_list) - x_padding
    x_max = max(inclusive_x_list) + x_padding
    y_min = min(inclusive_y_list) - y_padding
    y_max = max(inclusive_y_list) + y_padding


    
    # Creates colorbars for each of our 10 games
    colorbars = create_game_colorbars()
    
    # USE THIS to debug yellow only graphs
    # print(x_lists_bygenerator)

    # Layer a hexbin with a different colormap for each game
    hexbins = []
    for i in range(10):
        hexbins.append(ax.hexbin(x_lists_bygenerator[i], y_lists_by_generator[i], gridsize=150, extent=(x_min, x_max, y_min, y_max), cmap=colorbars[i], mincnt=1, alpha=.5, edgecolors="none"))
        # hexbin = ax.hexbin(x_lists_by_game[i], y_lists_by_game[i], gridsize=75, extent=(x_min, x_max, y_min, y_max), cmap=ordered_color_maps[i], mincnt=1, alpha=.75, edgecolors="none")
        # hexbin = ax.hexbin(x_lists_by_game[1], y_lists_by_game[1], gridsize=75, extent=(x_min, x_max, y_min, y_max), cmap="Blues", mincnt=1, alpha=.75, edgecolors="none")

    ax.set_xlim(x_min, x_max)
    ax.set_ylim(y_min, y_max)
    # Keep ERA square

    if standalone:
        # Set exterior/standalone characteristics
        #generator_name = json_path.split("/")[1]
        #if json_path.split("/")[2] != "levelMetrics.json": game_name = json_path.split("/")[2].capitalize() 
        #else: game_name = ""

        # Create colorbar labels for each game
        # Create colorbar labels for each game
        for i in range(10):

            cbar = plt.colorbar(hexbins[9-i],
                ax=ax, shrink=1, aspect=50, pad=0.01, orientation="vertical")
            cbar.set_ticks(ticks=[])
            cbar.set_label(generators[i], fontsize=10, labelpad=2, rotation=90) # Check that each generator is correctly ordered
            cbar.ax.tick_params(length=0)
        
            
        ax.set_xlabel(get_metric_title(selected_metrics_tuple[0]))
        ax.set_ylabel(get_metric_title(selected_metrics_tuple[1]))
        ax.set_title("ERA Chart by Individual Generator")
        # Titling format is "Y axis to X axis"
        folder_name = "figures/" + "ERA_by_generator/"
        Path(folder_name).mkdir(parents=True, exist_ok=True)
        save_file_name = folder_name + selected_metrics_tuple[1] + "To" + selected_metrics_tuple[0] + ".png"
        if os.path.isfile(save_file_name): os.remove(save_file_name)
        plt.savefig((save_file_name), dpi=300, bbox_inches="tight")
        
        # plt.show()
        plt.close()
        
        return ax


    
    


# USAGE: Select a metrics.json file path, then determine the graph's x and y axis by completing the selected metrics tuple
if __name__ == "__main__":
    metric_path = "generatedExamples/geminiLevelGenerator/levelMetrics.json"
    # metric_path = "generatedExamples/LocalLanguageModelGenerator/metrics.json"
    # metric_path = "generatedExamples/constructiveLevelGenerator/metrics.json"
    # metric_path = "generatedExamples/randomLevelGenerator/metrics.json"


    # metric_path = "generatedExamples/constructiveLevelGenerator/dungeon/metrics.json"
    # metric_path = "generatedExamples/geminiLevelGenerator/frogs/metrics.json"

    selected_metrics = ("Density", "ShannonEntropy")
    selected_metrics = ("Density", "Symmetry")
    create_ERA_by_Generator(selected_metrics, exclude_malformed=True)
