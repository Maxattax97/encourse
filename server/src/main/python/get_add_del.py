import sys
import json
import copy
from datetime import datetime
from helper import time_string
from daily_git_data import get_daily_commit_data as get_progress

def date_string(date):
    #print(date)
    return date.isoformat()


def api_format_data(data):
    new_data = {}
    for student in data:
        student_data = data[student]
        daily_data = []
        for day in student_data:
            new_entry = {}
            new_entry["date"] = date_string(day["date"])
            new_entry["additions"] = day["additions"]
            new_entry["deletions"] = day["deletions"]
            daily_data.append(new_entry)
        new_data[student] = daily_data
    return new_data

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
