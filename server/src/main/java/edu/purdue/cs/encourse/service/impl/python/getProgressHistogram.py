import sys
import json
import datetime

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
def formatHistogramData(data):
   pass 

if len(sys.argv) != 3:
    print("USAGE: python getProgressHistogram.py name file1\n")
    print("name is the name of the student for which you are requesting a progress histogram\n")
    print("file1 is the properly formatted commit data file")

studentID = sys.argv[1]
commit_data_path = sys.argv[2]
commit_data_file = open(commit_data_path, "r")
data, add_total, del_total = get_progress(studentID, commit_data_file)
print(data, add_total, del_total)

