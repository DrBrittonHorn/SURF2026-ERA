
import json

# Takes in a metrics json path and returns the object in the form of a python dict
def create_attribute_dict(metrics_json_path : str):
    with open(metrics_json_path) as f:
        string = f.read()
        data = json.loads(string)
        return data

# Returns true if and only if all binning properties of the level are satisfied (the level is structurally valid)
# Takes in a level path (which must exist in the attribute dict) and a level attribute dict, which can be created using the function above
def parseBinning(level_path : str, attribute_dict : dict):
    for requirement, value in attribute_dict[level_path]["Binning"].items():
        if value == False:
            return False
    return True