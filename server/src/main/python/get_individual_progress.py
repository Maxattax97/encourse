import sys
import json
import copy
import argparse
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
    progress = 0
    for day in dates:
        day = date_string(day)
        new_entry = {}
        new_entry["date"] = day
        if day in data:
            progress += data[day]["additions"] - data[day]["deletions"]
        new_entry["progress"] = progress
        daily_data.append(new_entry)
    for item in daily_data:
        item["progress"] = round(item["progress"] / progress * 100)
    return daily_data


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("logfile", help="path to commit log file")
    parser.add_argument("timefile", help="path to commit time file")
    parser.add_argument("name", help="user name")
    parser.add_argument("-O", "--obfuscate", action="store_true", help="obfuscate flag")

    args = parser.parse_args()

    commit_data_file = open(args.logfile, "r")
    commit_times_file = open(args.timefile, "r")
    student_id = args.name


    data = get_progress(commit_data_file)
    individual_data = data[student_id]
    # print(individual_data)
    # print("\n")
    reformatted_data = reformat_commit_data(individual_data)

    commit_times = commit_data(commit_times_file)
    # print(commit_times)
    individual_commit_times = commit_times[student_id]

    api_formatted_data = api_format_data(reformatted_data, individual_commit_times)
    api_json = json.dumps(api_formatted_data)
    print(api_json)
