import sys
import json
from getStartEnd import import_commit_data as import_commit_times
from getCommitCounts import import_commit_counts

def format_commit_data(dates, commits):
    data = {}
    for user in dates.keys():
        user_dates = dates[user]
        count = 0   # Default value, could be -1 to show no count was found. 0 may have some meaning
        if user in commits:
            count = commits[user]
        user_data = {}
        if len(user_dates) == 2:
            user_data["start"] = user_dates[0]
            user_data["end"] = user_dates[1]
            user_data["count"] = count
        data[user] = user_data
    return data

# Runs on file call
if len(sys.argv) != 3:
    print("USAGE: python getStatistics.py file_1 file_2")
    print("file_1 should be a properly formatted commit times file")
    print("file_2 should be a properly formatted commit count file")
    sys.exit("Incorrect usage")

commit_date_file = open(sys.argv[1], "r")
commit_count_file = open(sys.argv[2], "r")

dates_dict = import_commit_times(commit_date_file)
#for user in dates_dict.keys():
#    start_end = dates_dict[user]
#    print("{} -> {}".format(user, start_end))

counts_dict = import_commit_counts(commit_count_file)
#print(counts_dict)

# TODO: check for valid dicts

data = format_commit_data(dates_dict, counts_dict)
#print(data)
json = json.dumps(data)
# Outputs json to stdout
print(json)






