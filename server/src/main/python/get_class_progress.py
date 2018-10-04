import sys
import json
import copy
from datetime import datetime
from helper import time_string
from helper import daterange
from helper import date_string
from test_completion import get_test_completion as get_test_scores
from start_end import commit_data

def api_format_data(data):
    histogram_data = {
        "0-20%": 0,
        "20-40%": 0,
        "40-60%": 0,
        "60-80%": 0,
        "80-100%": 0
    }
    for student in data:
        #print(student)
        info = data[student]
        #print(info)
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

if len(sys.argv) != 2:
    print("USAGE: \t`python getProgressHistogram.py file1`")
    print("\tfile1 is a properly formatted test_case file")
    sys.exit()

test_score_file = open(sys.argv[1], "r")

data = get_test_scores(test_score_file)
#print(individual_data)
#print("\n")


api_formatted_data = api_format_data(data)
api_json = json.dumps(api_formatted_data)
print(api_json)