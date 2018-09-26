import sys
import json
from datetime import datetime

from getStudentProgress import get_cumulative_progress_for_student as get_progress

# Returns an json array of datapoints
#   datapoint: {
#       date: {
#           month: "MM"
#           day: "DD"
#           year: "YYYY"
#       }
#       progress: p in [0,100]
#   }
def format_data(data):
    new_data = []
    for point in data:
        date = point[0]
        additions = point[1]
        deletions = point[2]
        date_data = datetime.strptime(date, "%Y-%m-%d")
        formatted_date = {}
        formatted_date["day"] = date_data.day
        formatted_date["month"] = date_data.month
        formatted_date["year"] = date_data.year
        new_data_point = {}
        new_data_point["additions"] = additions
        new_data_point["deletions"] = deletions
        new_data_point["date"] = formatted_date
        new_data.append(new_data_point)
    return new_data

if len(sys.argv) != 3:
    print("USAGE: python getProgressHistogram.py name file1\n")
    print("name is the name of the student for which you are requesting a progress histogram\n")
    print("file1 is the properly formatted commit data file")
studentID = sys.argv[1]
commit_data_path = sys.argv[2]
commit_data_file = open(commit_data_path, "r")
data, add_total, del_total = get_progress(studentID, commit_data_file)
formatted_data = format_data(data)
json = json.dumps(formatted_data)
print(json)

