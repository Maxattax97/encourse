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


def api_format_data(data):
    histogram_data = {"0-20%": 0, "20-40%": 0, "40-60%": 0, "60-80%": 0, "80-100%": 0}
    for student in data:
        info = data[student]
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
    return histogram_data


def merge_data(visible, hidden):
    for key in visible:
        visible[key] += hidden[key]
    return visible


parser = argparse.ArgumentParser()
parser.add_argument("visible", help="path to visible test score file")
parser.add_argument("hidden", help="path to hidden test score file")
parser.add_argument("-O", "--obfuscate", action="store_true", help="obfuscate flag")

args = parser.parse_args()

visible_test_score_file = open(args.visible, "r")
hidden_test_score_file = open(args.hidden, "r")

visible_data = get_test_scores(visible_test_score_file)
hidden_data = get_test_scores(hidden_test_score_file)
# print(visible_data)

formatted_visible = api_format_data(visible_data)
formatted_hidden = api_format_data(hidden_data)
api_json = json.dumps(merge_data(formatted_visible, formatted_hidden))
print(api_json)
