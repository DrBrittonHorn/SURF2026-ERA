
import json

from matplotlib.colors import LinearSegmentedColormap

# Takes in a metrics json path and returns the object in the form of a python dict
def create_attribute_dict(metrics_json_path : str):
    with open(metrics_json_path) as f:
        string = f.read()
        data = json.loads(string)
        return data

# Returns true if and only if all binning properties of the level are satisfied (the level is structurally valid)
# Takes in a level path (which must exist in the attribute dict) and a level attribute dict, which can be created using the function above
def parse_binning(level_path : str, attribute_dict : dict):
    for requirement, value in attribute_dict[level_path]["Binning*"].items():
        if value == False:
            return False
    return True

def get_generator_title(json_path : str):
    raw_name = json_path.split("/")[1]
    name_to_title = {
                     "constructiveLevelGenerator" : "Constructive Level Generator",
                     "fineTunedLLMGenerator" : "Fine Tuned LLM Generator",
                     "geminiLevelGenerator" : "Gemini Level Generator",
                     "geneticLevelGenerator" : "Genetic Level Generator",
                     "localLanguageModelGenerator" : "Local LLM Generator",
                     "randomLevelGenerator" : "Random Level Generator",
                     "claudeLevelGenerator" : "Claude Level Generator"}
    return name_to_title[raw_name]

# Optional Means of renaming metrics in figures
def get_metric_title(metric_name):
    name_to_title = {
        "AvatarPosition" : "Avatar Position",
        "BalanceHorizontal" : "Horizontal Balance",
        "BalanceVertical" : "Vertical Balance",
        "CompressionDistance" : "Compression Distance",
        "Density" : "Density",
        "EnemyCount" : "Enemy Count",
        "FloodReachability" : "Flood Reachability",
        "HammingDistance" : "Hamming Distance",
        "JensenShannonDivergence1D" : "Jensen-Shannon Divergence (1D)",
        "JensenShannonDivergence2D" : "Jensen-Shannon Divergence (2D)",
        "KLDivergence1D" : "KL Divergence (1D)",
        "KLDivergence2D" : "KL Divergence (2D)",
        "Linearity" : "Linearity",
        "NaiveSimilarity" : "Naive Similarity",
        "NegativeSpace" : "Negative Space",
        "NgramSimilarity1D" : "N-gram Similarity (1D)",
        "NgramSimilarity2D" : "N-gram Similarity (2D)",
        "ShannonEntropy" : "Shannon Entropy",
        "Symmetry" : "Symmetry",
        "TileFrequency" : "Tile Frequency",
        "TilePositionStats" : "Tile Position Stats",
        "WallFloorRatio" : "Wall-Floor Ration",

        "Binning" : "Binning"
    }
    # return name_to_title[metric_name]
    return metric_name

# Returns a colorbar that begins as (nearly) transparent and moves to the provided color
def create_color_bar(rgb_tuple : tuple):
    colors = [(0, 0, 0)]
    colors.append(rgb_tuple)
    
    cmap = LinearSegmentedColormap.from_list("_", colors, 300)
    return cmap

def create_game_colorbars(num_games=10):
    cmap_rgbs = [
        (255.0, 0.0, 0.0),
        (255.0, 153.0, 0.0),
        (204.0, 255.0, 0.0),
        (51.0, 255.0, 0.0),
        (0.0, 255.0, 102.0),
        (0.0, 255.0, 255.0),
        (0.0, 102.0, 255.0),
        (51.0, 0.0, 255.0),
        (204.0, 0.0, 255.0),
        (255.0, 0.0, 153.0),
    ]
    cmap_values = []
    for rgb in cmap_rgbs:
        value = tuple(x / 255 for x in rgb)
        cmap_values.append(value)
    
    # print(cmap_values)
    colormaps = []
    for value in cmap_values:
        # print(value)
        colormaps.append(LinearSegmentedColormap.from_list("_", [value, (1, 1, 1)], 300))
    return colormaps
    

# print(create_attribute_dict("generatedExamples/constructiveLevelGenerator/metrics.json"))
# print(create_attribute_dict("generatedExamples/geminiLevelGenerator/frogs/metrics.json"))

