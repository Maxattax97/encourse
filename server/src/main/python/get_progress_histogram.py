import sys
import json
import copy
from datetime import datetime
from helper import time_string
from add_del import get_daily_commit_data as get_progress

def date_string(date):
    #print(date)
    return date.isoformat()


def api_format_data(data):
    data = copy.deepcopy(data)
    for student in data:
        student_data = data[student]
        for day in student_data:
            day["date"] = date_string(day["date"])
            day["time_spent"] = time_string(day["time_spent"])
    return data

def format_data(data):
    data = copy.deepcopy(data)
    for student in data:
        student_data = data[student]
        for day in student_data:
            day["date"] = date_string(day["date"])
    return data

if len(sys.argv) != 2:
    print("USAGE: \t`python getProgressHistogram.py file1`")
    print("\tfile1 is a properly formatted commit data file")
    sys.exit()
commit_data_path = sys.argv[1]
commit_data_file = open(commit_data_path, "r")
data = get_progress(commit_data_file)

api_formatted_data = api_format_data(data)
api_json = json.dumps(api_formatted_data)
print(api_json)
#print(data)
formatted_data = format_data(data)
json = json.dumps(formatted_data)
#print(json)
