
import json


def create_attribute_dict(jsonPath : str):
    with open(jsonPath) as f:
        string = f.read()
        data = json.loads(string)
        # print(data)
        # print(data["generatedExamples\\geminiLevelGenerator\\zelda\\zelda_lvl027.txt"])
        return data




sample_path = "generatedExamples/geminiLevelGenerator/metrics.json"
create_attribute_dict(sample_path)