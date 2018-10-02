import sys
import json
from datetime import datetime
from helper import time_string
from start_end import commit_data as commit_times
from daily_git_data import get_daily_commit_data as commit_list
from test_completion import get_test_completion as test_completion

def format_commit_data(dates, stats, tests):
    data = {}
    for user in dates.keys():
        user_dates = dates[user]
        additions = 0
        deletions = 0
        count = 0
        time = 0
        if user in stats:
            info = stats[user]
            additions = info["additions"]
            deletions = info["deletions"]
            count = info["commit_count"]
            time = info["time_spent"]
        test_score = 0
        if user in tests:
            info = tests[user]
            test_score = info["total"]
        user_data = {}
        if len(user_dates) == 2:
            user_data["Start Date"] = format_date(user_dates[0])
            user_data["End Date"] = format_date(user_dates[1])
        user_data["Additions"] = "{} lines".format(additions)
        user_data["Deletions"] = "{} lines".format(deletions)
        user_data["Commit Count"] = "{} commits".format(count)
        user_data["Estimated Time Spent"] = time_string(time)
        user_data["Current Test Score"] = "{}%".format(int(test_score))
        
        array_data = []
        for stat_name in user_data:
            stat_value = user_data[stat_name]
            array_data.append({"stat_name": stat_name, "stat_value": stat_value})
        data[user] = array_data

    return data

def format_date(date):
    date_data = datetime.strptime(date, "%Y-%m-%d")
    formatted_date = {}
    formatted_date["day"] = date_data.day
    formatted_date["month"] = date_data.month
    formatted_date["year"] = date_data.year
    return date_data.date().isoformat()

def sum_statistics(data):
    new_data = {}
    for student in data:
        commits = data[student]
        total_add = 0
        total_del = 0
        total_count = 0
        total_time = 0
        for commit in commits:
            total_add += commit["additions"]
            total_del += commit["deletions"]
            total_time += commit["time_spent"]
            total_count += commit["commit_count"]
        student_data = {}
        student_data["additions"] = total_add
        student_data["deletions"] = total_del
        student_data["commit_count"] = total_count
        student_data["time_spent"] = total_time
        new_data[student] = student_data
    return new_data

# Runs on file call
if len(sys.argv) != 5:
    print("USAGE: python getStatistics.py name file_1 file_2")
    print("name is the name of the student for which statistics are being requested")
    print("file_1 should be a properly formatted commit times file")
    print("file_2 should be a properly formatted commit list file")
    print("file_3 should be a properly formatted test case file")

    sys.exit("Incorrect usage")

student_id = sys.argv[1]
commit_date_file = open(sys.argv[2], "r")
commit_data_file = open(sys.argv[3], "r")
test_case_file = open(sys.argv[4], "r")

dates_dict = commit_times(commit_date_file)
#for user in dates_dict.keys():
#    start_end = dates_dict[user]
#    print("{} -> {}".format(user, start_end))

#print(counts_dict)

student_data = commit_list(commit_data_file)
formatted_student_data = sum_statistics(student_data)
# TODO: check for valid dicts

test_data = test_completion(test_case_file)
#print(test_data)

data = format_commit_data(dates_dict, formatted_student_data, test_data)
#print(data)
json = json.dumps(data[student_id])
# Outputs json to stdout
print(json)






