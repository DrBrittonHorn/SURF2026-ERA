from matplotlib import pyplot as plt
import numpy as np
import sys, os
import pandas as pd

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__)))))
from metric_analysis.tools import create_attribute_dict, get_generator_title, get_metric_title

def create_table(json_path: str, exclude_malformed=True):
    dict_data = create_attribute_dict(json_path)
    generator_name = json_path.split("/")[1]

    rows = []
    cols = []
    data_map = {}

    # Prepares row and column data
    for level_path, metrics in dict_data.items():
        game_name = level_path.split("\\\\|/")[2]
        if game_name not in rows:
            rows.append(game_name)
        for metric, value in metrics.items():
            if metric not in cols:
                cols.append(get_metric_title(metric))
            if game_name not in data_map:
                data_map[game_name] = {}
            if metric not in data_map[game_name]:
                data_map[game_name][metric] = []

            if not isinstance(value, dict):
                data_map[game_name][metric].append(value)

    
    # Remove non-plottable fields (ex. Binning*) (These fields are demarcated by an asterisk).
    for i in cols:
        if '*' in i: cols.remove(i)

    # Builds 2d array for mean and standard deviation used to construct ax.table
    cell_text = []
    for game in rows:
        row = []
        for metric in cols:
            vals = data_map[game][metric]
            row.append(f"{np.mean(vals):.2f} ± {np.std(vals):.2f}")
        cell_text.append(row)

    print(f"Creating a Table with {len(dict_data.items())} levels as data points")


    fig, ax = plt.subplots()
    ax.axis("off")

    table = ax.table(cellText=cell_text, rowLabels=rows, colLabels=cols, cellLoc="center", rowLoc="center",loc="center")


    table.scale(1, 1.6)

    
    if json_path.split("/")[2] == "levelMetrics.json":
        game_label = "" 
    else:
        game_label = json_path.split("/")[2].capitalize()

    ax.set_title(get_generator_title(json_path) + "" + game_label + " Table")

    save_file_name = ("figures/" + generator_name + "/Tables/" + game_label + "Table.png")
    if os.path.isfile(save_file_name): os.remove(save_file_name)
    plt.savefig(save_file_name, dpi=300, bbox_inches="tight"
    )
    # plt.show()
    plt.close()


metric_path = "generatedExamples/geminiLevelGenerator/metrics.json"
metric_path = "generatedExamples/LocalLanguageModelGenerator/metrics.json"
# metric_path = "generatedExamples/constructiveLevelGenerator/metrics.json"
# metric_path = "generatedExamples/randomLevelGenerator/metrics.json"

# metric_path = "generatedExamples/constructiveLevelGenerator/dungeon/metrics.json"
# metric_path = "generatedExamples/geminiLevelGenerator/frogs/metrics.json"

# create_table(metric_path, exclude_malformed=True)