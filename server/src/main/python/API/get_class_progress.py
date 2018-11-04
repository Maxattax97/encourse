import sys
import json
import copy
import argparse
from datetime import datetime
from helper import time_string
from helper import daterange
from helper import date_string
from helper import eprint
from test_completion import get_test_completion as get_test_scores
from start_end import commit_data


def jsonify(visible_data, hidden_data):
    """Formats data for /classProgress endpoint

    Converts information in **data** into an appropriately formatted json 
    for the /classProgress endpoint     

    **Args**:
        **data** (dict): A dictionary of the following format: ::

            {
               "name1": {
                    "Test1": (bool, float),
                    ...
               },
               ...
            }

    **Return**:
        dict: A dictionary of histogram data split into 20% bins: ::

            {
                "0-20%": int,
                "20-40%": int,
                "40-60%": int,
                "60-80%": int,
                "80-100%": int
            }

        where each percentage bin contains a value 0 <= int <= 100

    """
    test_data = merge_inputs(visible_data, hidden_data)
    histogram_data = {"0-20%": 0, "20-40%": 0, "40-60%": 0, "60-80%": 0, "80-100%": 0}
    for student in test_data:
        info = test_data[student]
        if info["total"] <= 20:
            histogram_data["0-20%"] += 1
        elif info["total"] <= 40:
            histogram_data["20-40%"] += 1
        elif info["total"] <= 60:
            histogram_data["40-60%"] += 1
        elif info["total"] <= 80:
            histogram_data["60-80%"] += 1
        elif info["total"] <= 100:
            histogram_data["80-100%"] += 1
    return json.dumps(histogram_data)

def merge_inputs(visible, hidden):
    """Merge test results per student from two input sources"""
    merged = visible
    # Add hidden values to merged
    for key in hidden:
        print(key)
        print(hidden[key])
        if key in merged:
            merged[key]["tests"].update(hidden[key]["tests"])
        else:
            merged[key]["tests"] = hidden[key]["tests"]
    eprint(merged)
    return merged
        
def merge_data(visible, hidden):
    """Sums the values in **visible** and **hidden** for each bin"""
    visible = json.loads(visible)
    hidden = json.loads(hidden)
    for key in visible:
        visible[key] += hidden[key]
    return json.dumps(visible)

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("visible", help="path to visible test score file")
    parser.add_argument("hidden", help="path to hidden test score file")

    args = parser.parse_args()

    visible_test_score_file = open(args.visible, "r")
    hidden_test_score_file = open(args.hidden, "r")

    visible_data = get_test_scores(visible_test_score_file)
    hidden_data = get_test_scores(hidden_test_score_file)

    formatted_data = jsonify(visible_data, hidden_data)
    api_json = formatted_data
    print(api_json)
