import sys
import json
import copy
from datetime import datetime
from add_del import get_daily_commit_data as get_progress

def date_string(date):
    #print(date)
    return date.isoformat()

def  time_string(seconds):
    """
    Converts a datetime object to a concise string
    returns a String:
    'X days' if the number of days is greater than 0
    'X hours' if the number of hours is greater than 0
    'X minutes' if the number of minutes if greater than 1
    'Not started' otherwise
    """

    if seconds < 60:
        return "None"
    minutes, seconds = divmod(seconds, 60)
    if minutes < 60:
        return "{} minutes".format(int(minutes))
    hours, minutes = divmod(minutes, 60)
    if hours < 24:
        return "{} hours".format(int(hours))
    days, hours = divmod(hours, 24)
    return "{} days".format(int(days))

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
#print(api_json)
#print(data)
formatted_data = format_data(data)
json = json.dumps(formatted_data)
print(json)
