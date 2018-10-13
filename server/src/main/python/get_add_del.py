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


def reformat(commit_list) -> dict:
    """Reformats a list of dates into a dictionary
    
    Keyword arguments:
    commit_list -- a list of dictionaries

    Returns:
    new_data: additions,deletions
    """
    commit_dict = {}
    for info in commit_list:
        new_entry = {}
        new_entry["additions"] = info["additions"]
        new_entry["deletions"] = info["deletions"]
        date = date_string(info["date"])
        commit_dict[date] = new_entry
    return commit_dict


def api_format_data(data, times):
    daily_data = []
    date1 = datetime.strptime(times[0], "%Y-%m-%d").date()
    date2 = datetime.strptime(times[1], "%Y-%m-%d").date()
    dates = daterange(date1, date2)
    for day in dates:
        day = date_string(day)
        new_entry = {}
        new_entry["date"] = day
        if day in data:
            new_entry["additions"] = data[day]["additions"]
            new_entry["deletions"] = data[day]["deletions"]
        else:
            new_entry["additions"] = 0
            new_entry["deletions"] = 0

        daily_data.append(new_entry)
    return daily_data

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("logfile", help="path to commit log file")
    parser.add_argument("timefile", help="path to commit time file")
    parser.add_argument("name", help="user name")
    parser.add_argument("-l", "--limit", help="ignore file changes above limit")
    parser.add_argument("-O", "--obfuscate", action="store_true", help="obfuscate flag")


    args = parser.parse_args()

    commit_data_file = open(args.logfile, "r")
    commit_times_file = open(args.timefile, "r")
    student_id = args.name


    data = (
        get_progress(commit_data_file, max_change=int(args.limit))
        if args.limit
        else get_progress(commit_data_file)
    )
    individual_data = data[student_id]
    # print(individual_data)
    # print("\n")
    reformatted_data = reformat(individual_data)

    commit_times = commit_data(commit_times_file)
    # print(commit_times)
    individual_commit_times = commit_times[student_id]

    api_formatted_data = api_format_data(reformatted_data, individual_commit_times)
    api_json = json.dumps(api_formatted_data)
    print(api_json)
