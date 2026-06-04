
import json


def createAttributeDict(jsonPath : str):
    with open(jsonPath) as f:
        string = f.read()
        data = json.loads(string)
        # print(data)
        print(data["generatedExamples\\geminiLevelGenerator\\zelda\\zelda_lvl027.txt"])





sample_path = "generatedExamples/geminiLevelGenerator/metrics.json"
createAttributeDict(sample_path)