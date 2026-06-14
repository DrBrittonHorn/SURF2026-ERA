from matplotlib import pyplot as plt
import numpy as np
import sys, os
import seaborn as sns

# Fixes local import behavior
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__)))))
from metric_analysis.tools import create_attribute_dict, parseBinning

def create_density_estimation(selected_metrics_tuple: tuple, json_path: str, exclude_malformed=True):
    
    dict = create_attribute_dict(json_path)
    
    listX = []
    listY = []
    lists = []
    for level_path, metrics in dict.items():
        # Create lists of the variables used in this graph
        if (metrics[selected_metrics_tuple[0]] > 0 and metrics[selected_metrics_tuple[1]] > 0): # Revisit this, a negative number may not mean error for all metrics...
            if (not exclude_malformed or (parseBinning(level_path, dict))): 
                listX.append(metrics[selected_metrics_tuple[0]])
                listY.append(metrics[selected_metrics_tuple[1]])
            

    print(f"Creating a Density Estimation chart with {len(listX)} levels as data points")
    
    lists.append(listX)
    lists.append(listY)

    plt.figure(figsize=(8, 5))
    sns.kdeplot(x=listX, y=listY, fill=True, color = "teal", levels=8, warn_singular=False)

    # Sets bounds for figure
    #ax.set(xlim=(min(listX), max(listX)), ylim=(min(listY), max(listY)))
    padding_factor = .25
    x_padding = (max(listX) - min(listX)) * padding_factor
    y_padding = (max(listY) - min(listY)) * padding_factor
    
    plt.xlim(min(listX) - x_padding, max(listX) + x_padding)
    plt.ylim(min(listY) - y_padding, max(listY) + y_padding)


    # Set exterior characteristics
    generator_name = json_path.split("/")[1]
    if json_path.split("/")[2] != "metrics.json": game_name = json_path.split("/")[2].capitalize() 
    else: game_name = ""
    plt.title(generator_name + " " + game_name + " Density Estimation Chart")
    plt.xlabel(selected_metrics_tuple[0])
    plt.ylabel(selected_metrics_tuple[1])

    # "Y axis to X axis"
    save_file_name = "figures/" + generator_name + "/Density/" + game_name + selected_metrics_tuple[1] + "To" + selected_metrics_tuple[0] + ".png"
    if os.path.isfile(save_file_name): os.remove(save_file_name)
    plt.savefig(save_file_name, dpi=300, bbox_inches="tight")
    # plt.show()
    plt.close()


# USAGE: Select a metrics.json file path, then determinr the graph's x and y axis by completing the selected metrics tuple

# metric_path = "generatedExamples/geminiLevelGenerator/metrics.json"
# metric_path = "generatedExamples/LocalLanguageModelGenerator/metrics.json"
# metric_path = "generatedExamples/constructiveLevelGenerator/metrics.json"
metric_path = "generatedExamples/randomLevelGenerator/metrics.json"


# metric_path = "generatedExamples/constructiveLevelGenerator/dungeon/metrics.json"
# metric_path = "generatedExamples/geminiLevelGenerator/frogs/metrics.json"

# selected_metrics = ("Density", "NGramSimilarity2D")
# create_density_estimation(selected_metrics, metric_path, exclude_malformed=True)

# TODO legend for ERA chart, show total level amounts for ERA and histogram

# Metrics json reformatting
# ERA chart
# Histograms
# Tables