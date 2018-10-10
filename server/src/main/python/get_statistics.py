import sys
import json
import argparse
import random
from datetime import datetime
from helper import time_string
from helper import eprint
from start_end import commit_data as commit_times
from daily_git_data import get_daily_commit_data as commit_list
from test_completion import get_test_completion as test_completion
from test_completion import get_test_completion_string as test_completion_string

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
parser = argparse.ArgumentParser()
parser.add_argument("logfile", help="path to commit log file")
parser.add_argument("timefile", help="path to commit time file")
parser.add_argument("name", help="user name")
parser.add_argument("tests", help="test case string")
parser.add_argument("-t", "--timeout", help="time spent timeout")
parser.add_argument("-O", "--obfuscate", action="store_true", help="obfuscate flag")

args = parser.parse_args()

if args.obfuscate:
    fake_data = [{'stat_name': 'Start Date', 'stat_value': '2018-08-0{}'.format(random.randint(1,9))}, {'stat_name': 'End Date', 'stat_value': '2018-09-0{}'.format(random.randint(1,9))}, {'stat_name': 'Additions', 'stat_value': '{} lines'.format(random.randint(2000,5000))}, {'stat_name': 'Deletions', 'stat_value': '{} lines'.format(random.randint(0,2000))}, {'stat_name': 'Commit Count', 'stat_value': '{} commits'.format(random.randint(0,200))}, {'stat_name': 'Estimated Time Spent', 'stat_value': '{} hours'.format(random.randint(0,36))}, {'stat_name': 'Current Test Score', 'stat_value': '{}%'.format(10*random.randint(0,10))}]
    print(json.dumps(fake_data))
    sys.exit()

student_id = args.name
commit_date_file = open(args.timefile, "r")
commit_data_file = open(args.logfile, "r")
test_case_string = args.tests

dates_dict = commit_times(commit_date_file)
#for user in dates_dict.keys():
#    start_end = dates_dict[user]
#    print("{} -> {}".format(user, start_end))

#print(counts_dict)

print(args.timeout)
student_data = commit_list(commit_data_file, timeout=args.timeout) if args.timeout else commit_list(commit_data_file)
formatted_student_data = sum_statistics(student_data)
# TODO: check for valid dicts

test_data = test_completion_string(test_case_string)
#print(test_data)

data = format_commit_data(dates_dict, formatted_student_data, test_data)
#print(data)
json = json.dumps(data[student_id])
# Outputs json to stdout
print(json)
