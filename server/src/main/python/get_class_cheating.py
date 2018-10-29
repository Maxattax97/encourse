from daily_git_data import get_daily_commit_data as daily_data
from past_progress import past_progress
from get_velocity import jsonify as velocity

import argparse

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "visiblefile", help="path to historic progress file for visible test cases"
    )
    parser.add_argument(
        "hiddenfile", help="path to historic progress file for hidden test cases"
    )
    parser.add_argument("logfile", help="path to log file")
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

    visible_progress = past_progress(visible_file)
    hidden_progress = past_progress(hidden_file)

    daily_data = commit_list(
        commit_log_file, max_change=args.limit, timeout=args.timeout
    )

    for student in data.keys():
        student_data = data[student]
        startend = times(student_data)

        velocity_data = velocity(student_data, progress_file)

    api_json = jsonify(
        individual_visible_data,
        individual_daily_data,
        startend,
        hidden_scores=individual_hidden_data,
    )
