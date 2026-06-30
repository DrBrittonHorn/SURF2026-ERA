from matplotlib import pyplot as plt
import numpy as np
import sys, os
# Fixes local import behavior
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__)))))
from metric_analysis.tools import create_attribute_dict, get_generator_title, get_metric_title, parse_binning

def create_ERA_graph(selected_metrics_tuple: tuple, json_path: str, exclude_malformed=True, ax=None):
    standalone = False
    if ax is None:
        standalone = True
        fig, ax = plt.subplots()
    dict = create_attribute_dict(json_path)
    
    listX = []
    listY = []
    for level_path, metrics in dict.items():
        # Create lists of the variables used in this graph
        if (metrics[selected_metrics_tuple[0]] > 0 and metrics[selected_metrics_tuple[1]] > 0): # Revisit this, a negative number may not mean error for all metrics...
            if (not exclude_malformed or (parse_binning(level_path, dict))): 
                listX.append(metrics[selected_metrics_tuple[0]])
                listY.append(metrics[selected_metrics_tuple[1]])
            
    
    print(f"Creating an ERA chart with {len(listX)} levels: ({selected_metrics_tuple[1]} to {selected_metrics_tuple[0]})")
    if (len(listX) == 0): return

    padding_factor = .25
    x_padding = (max(listX) - min(listX)) * padding_factor
    y_padding = (max(listY) - min(listY)) * padding_factor

    x_min = min(listX) - x_padding
    x_max = max(listX) + x_padding
    y_min = min(listY) - y_padding
    y_max = max(listY) + y_padding

    hexbin = ax.hexbin(listX, listY, gridsize=75, extent=(x_min, x_max, y_min, y_max), cmap="plasma")

    ax.set_xlim(x_min, x_max)
    ax.set_ylim(y_min, y_max)
    
    
    
    
    if standalone:
        # Set exterior/standalone characteristics
        generator_name = json_path.split("/")[1]
        if json_path.split("/")[2] != "levelMetrics.json": game_name = json_path.split("/")[2].capitalize() 
        else: game_name = ""

        ax.set_xlabel(get_metric_title(selected_metrics_tuple[0]))
        ax.set_ylabel(get_metric_title(selected_metrics_tuple[1]))
        colorbar = plt.colorbar(hexbin, ax=ax)
        colorbar.set_label("Count")
        ax.set_title(get_generator_title(json_path) + "" + game_name + " ERA Chart")
        # "Y axis to X axis"
        save_file_name = "figures/" + generator_name + "/ERA/" + game_name + selected_metrics_tuple[1] + "To" + selected_metrics_tuple[0] + ".png"
        if os.path.isfile(save_file_name): os.remove(save_file_name)
        plt.savefig((save_file_name), dpi=300, bbox_inches="tight")
        # plt.show()
        plt.close()
    
    return ax



# USAGE: Select a metrics.json file path, then determinr the graph's x and y axis by completing the selected metrics tuple

metric_path = "generatedExamples/geminiLevelGenerator/levelMetrics.json"
# metric_path = "generatedExamples/LocalLanguageModelGenerator/metrics.json"
# metric_path = "generatedExamples/constructiveLevelGenerator/metrics.json"
# metric_path = "generatedExamples/randomLevelGenerator/metrics.json"


# metric_path = "generatedExamples/constructiveLevelGenerator/dungeon/metrics.json"
# metric_path = "generatedExamples/geminiLevelGenerator/frogs/metrics.json"

# selected_metrics = ("Density", "ShannonEntropy")
# create_ERA_graph(selected_metrics, metric_path, exclude_malformed=True)
