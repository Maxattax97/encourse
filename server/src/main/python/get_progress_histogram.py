import sys
import json
from datetime import datetime

from add_del import get_daily_commit_data as get_progress

# Returns an json array of datapoints
#   datapoint: {
#       date: {
#           month: "MM"
#           day: "DD"
#           year: "YYYY"
#       }
#       progress: p in [0,100]
#   }


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

def format_data(data):
    for student in data:
        student_data = data[student]
        for day in student_data:
            day["date"] = date_string(day["date"])
            day["time_spent"] = time_string(day["time_spent"])
    return data

if len(sys.argv) != 2:
    print("USAGE: python getProgressHistogram.py file1\n")
    print("file1 is the properly formatted commit data file")
commit_data_path = sys.argv[1]
commit_data_file = open(commit_data_path, "r")
data = get_progress(commit_data_file)
#print(data)
formatted_data = format_data(data)
#print(formatted_data)
json = json.dumps(formatted_data)
print(json)

