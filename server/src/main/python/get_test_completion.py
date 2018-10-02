import sys
import json
import copy
from datetime import datetime
from test_completion import get_test_completion as get_scores

def format_data(data):
    data = copy.deepcopy(data)
    for student in data:
        pass
    return data

if len(sys.argv) != 2:
    print("USAGE: \t`python getProgressHistogram.py file1`")
    print("\tfile1 is a properly formatted test case progress file")
    sys.exit()
commit_data_path = sys.argv[1]
commit_data_file = open(commit_data_path, "r")
data = get_scores(commit_data_file)
#print(data)

formatted_data = format_data(data)
json = json.dumps(formatted_data)
print(json)
