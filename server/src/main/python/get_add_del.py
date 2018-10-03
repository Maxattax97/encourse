import sys
import json
import copy
from datetime import datetime
from helper import time_string
from helper import daterange
from helper import date_string
from daily_git_data import get_daily_commit_data as get_progress
from start_end import commit_data

def reformat_commit_data(data):
    """
    Creates a dict of date: additions,deletions
    """
    new_data = {}
    for info in data:
        new_entry = {}
        new_entry["additions"] = info["additions"]
        new_entry["deletions"] = info["deletions"]
        date = date_string(info["date"])
        new_data[date] = new_entry
    return new_data

def api_format_data(data, times):
    daily_data = []
    date1 = datetime.strptime(times[0], "%Y-%m-%d").date()
    date2 = datetime.strptime(times[1], "%Y-%m-%d").date()
    dates = daterange(date1, date2)
    for day in dates:
        day = date_string(day)
        new_entry = {}
        new_entry["date"] = day
        if day in data:
            new_entry["additions"] = data[day]["additions"]
            new_entry["deletions"] = data[day]["deletions"]
        else:
            new_entry["additions"] = 0
            new_entry["deletions"] = 0
            
        daily_data.append(new_entry)
    return daily_data

if len(sys.argv) != 4:
    print("USAGE: \t`python getProgressHistogram.py file1`")
    print("\tfile1 is a properly formatted commit data file")
    print("\tfile2 is a properly formatted commit time file")
    print("\tname is the student for which git data is being requested")
    sys.exit()

commit_data_file = open(sys.argv[1], "r")
commit_times_file = open(sys.argv[2], "r")
student_id = sys.argv[3]


data = get_progress(commit_data_file)
individual_data = data[student_id]
#print(individual_data)
#print("\n")
reformatted_data = reformat_commit_data(individual_data)

commit_times = commit_data(commit_times_file)
#print(commit_times)
individual_commit_times = commit_times[student_id]

api_formatted_data = api_format_data(reformatted_data, individual_commit_times)
api_json = json.dumps(api_formatted_data)
print(api_json)
