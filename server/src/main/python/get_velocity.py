import sys
import json
import copy
import argparse
from datetime import datetime
from helper import time_string
from helper import daterange
from helper import date_string
from helper import eprint
from helper import times_from_dailydata as times
from start_end import commit_data
from past_progress import past_progress
from daily_git_data import get_daily_commit_data as commit_list


def jsonify(
    scores, daily_data, times, hidden_scores=None, max_velocity=None, max_rate=None
):
    """Convert data to json for /velocity endpoint

    Converts the data to a mapping of students to their respective daily velocities. 
    Each student is flagged as suspicious if their progress per hour exceeds 
    **max_velocity**, or if their progress per commit exceeds **max_rate**
    A dictionary is also created for each day in between the first and last date, 
    even if there is no data for that date. In that case, both velocity and rate 
    for that day are set zero.
    
    **Args**:
        **daily_data** (dict): A dictionary containing the git commit list:
        **times** (str, str): A tuple of start and end dates of the form "mm-dd-yyyy"

    **Returns**:
        json: A json dictionary

    """

    # Create date range using times file
    date1 = datetime.strptime(times[0], "%Y-%m-%d").date()
    date2 = datetime.strptime(times[1], "%Y-%m-%d").date()
    dates = daterange(date1, date2)

    scores = {date_string(x["date"]): x for x in scores}
    hidden_scores = {date_string(x["date"]): x for x in hidden_scores}
    daily_data = {date_string(x["date"]): x for x in daily_data}

    velocity_data = []
    cumulative_progress = 0
    min_progress = 0

    for day in dates:
        day = date_string(day)
        new_entry = {}
        new_entry["date"] = day

        # get cumulative progress
        if day in scores:
            cumulative_progress = scores[day]["progress"]
        if day in hidden_scores:
            cumulative_progress = hidden_scores[day]["progress"]

        # calculate daily progress
        new_entry["progress"] = cumulative_progress - min_progress
        min_progress = max(min_progress, cumulative_progress)

        # copy daily data
        new_entry["timeSpent"] = 0
        new_entry["commitCount"] = 0
        if day in daily_data:
            new_entry["timeSpent"] = daily_data[day]["time_spent"]
            new_entry["commitCount"] = daily_data[day]["commit_count"]

        velocity_data.append(new_entry)

    return json.dumps(velocity_data)


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "visiblefile", help="path to historic progress file for visible test cases"
    )
    parser.add_argument(
        "hiddenfile", help="path to historic progress file for hidden test cases"
    )
    parser.add_argument("logfile", help="path to log file")
    parser.add_argument("timefile", help="path to time file")
    parser.add_argument("name", help="user name")
    parser.add_argument("-t", "--timeout", help="time spent timeout")
    parser.add_argument("-l", "--limit", help="ignore file changes above limit")
    parser.add_argument(
        "-v",
        "--velocity",
        help="the maximum daily progress per hour spent before a student is flagged as suspicious",
    )
    parser.add_argument(
        "-r",
        "--rate",
        help="the maximum daily progress per commit before a student is flagged as suspicious",
    )
    parser.add_argument("-O", "--obfuscate", action="store_true", help="obfuscate flag")

    args = parser.parse_args()

    visible_file = open(args.visiblefile, "r")
    hidden_file = open(args.hiddenfile, "r")
    commit_log_file = open(args.logfile, "r")
    commit_times_file = open(args.timefile, "r")
    student_id = args.name

    visible_data = past_progress(visible_file)
    individual_visible_data = visible_data[student_id]

    hidden_data = past_progress(hidden_file)
    individual_hidden_data = hidden_data[student_id]

    daily_data = commit_list(
        commit_log_file, max_change=args.limit, timeout=args.timeout
    )
    individual_daily_data = daily_data[student_id]

    startend = times(individual_student_data)
    commit_times = commit_data(commit_times_file)
    individual_commit_times = commit_times[student_id]

    api_json = jsonify(
        individual_visible_data,
        individual_daily_data,
        individual_commit_times,
        hidden_scores=individual_hidden_data
    )
    print(api_json)
