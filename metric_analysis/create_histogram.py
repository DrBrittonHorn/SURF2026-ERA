from matplotlib import pyplot as plt
import numpy as np
import sys, os
# Fixes local import behavior
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from metric_analysis.tools import create_attribute_dict, parseBinning

def create_graph(selected_metric, json_path: str, exclude_malformed=True):
    # plt.style.use('_mpl-gallery')
    dict = create_attribute_dict(json_path)
    listX = []

    for level_path, metrics in dict.items():
        # Create lists of the variables used in this graph
        if (metrics[selected_metric] > 0): # Revisit this, a negative number may not mean error for all metrics...
            if (not exclude_malformed or (parseBinning(level_path, dict))): 
                listX.append(metrics[selected_metric])
        # print(listX)

    fig, ax = plt.subplots()

    ax.hist(listX, bins=5, linewidth=0.5, edgecolor="white")

    ax.set(xlim=(min(listX), max(listX)), xticks=np.arange(min(listX), max(listX), round((max(listX)-min(listX))/10, 3)),
        ylim=(0, len(listX)), yticks=np.linspace(0, len(listX), 9)) # TODO Create more precide algorithm for creating (y) upper bounds

    # Set exterior characteristics
    generator_name = json_path.split("/")[1]
    if json_path.split("/")[2] != "metrics.json": game_name = json_path.split("/")[2].capitalize() 
    else: game_name = ""
    ax.set_title(generator_name + " " + game_name + " Histogram")
    ax.set_xlabel(selected_metric)

    # Save and show
    plt.savefig(("figures/Histograms/" + generator_name + game_name + selected_metric + ".png"), dpi=300, bbox_inches="tight")
    plt.show()


# USAGE: Select a metrics.json file path, then determinr the graph's selected_metric variable

metric_path = "generatedExamples/geminiLevelGenerator/metrics.json"
# metric_path = "generatedExamples/LocalLanguageModelGenerator/metrics.json"
# metric_path = "generatedExamples/constructiveLevelGenerator/metrics.json"
# metric_path = "generatedExamples/randomLevelGenerator/metrics.json"

# metric_path = "generatedExamples/constructiveLevelGenerator/dungeon/metrics.json"
# metric_path = "generatedExamples/geminiLevelGenerator/frogs/metrics.json"

selected_metric = "FloodReachability"
create_graph(selected_metric, metric_path, exclude_malformed=True)