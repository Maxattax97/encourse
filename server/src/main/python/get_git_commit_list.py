import sys
import json
import copy
import argparse
from datetime import datetime
from helper import time_string
from daily_git_data import get_daily_commit_data as get_progress


def date_string(date):
    # print(date)
    return date.isoformat()


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


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("logfile", help="path to commit log file")
    parser.add_argument("name", help="user name")
    parser.add_argument("-O", "--obfuscate", action="store_true", help="obfuscate flag")

    args = parser.parse_args()

    student_id = args.name
    commit_data_file = open(args.logfile, "r")
    data = get_progress(commit_data_file)

    api_formatted_data = api_format_data(data)[student_id]
    api_json = json.dumps(api_formatted_data)
    print(api_json)
    # print(data)
    formatted_data = format_data(data)[student_id]
    json = json.dumps(formatted_data)
    # print(json)
