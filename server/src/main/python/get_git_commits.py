import sys
import json
import copy
from datetime import datetime
from helper import date_string
from helper import daterange
from daily_git_data import get_daily_commit_data as commit_list

def format_data(data):
    new_data = []
    date1 = data[0]["date"]
    date2 = data[len(data)-1]["date"]
    dates = daterange(date1, date2)
    for date in dates:
        new_bar = {
            "date": date_string(date),
            "count": 0
        }
        new_data.append(new_bar)
    for entry in data:
        date = date_string(entry["date"])
        count = entry["commit_count"]
        match = None
        for e in new_data:
            if e["date"] == date:
                e["count"] = count
                break
    return new_data

if len(sys.argv) != 3:
    print("USAGE: \t`python getProgressHistogram.py file1 name`")
    print("\tfile1 is a properly formatted git log file")
    print("\tname is the name of the student whose commit count is being requested")
    sys.exit()

commit_data_file = open(sys.argv[1], "r")
student_id = sys.argv[2]

data = commit_list(commit_data_file)[student_id]

formatted_data = format_data(data)
json = json.dumps(formatted_data)
print(json)
